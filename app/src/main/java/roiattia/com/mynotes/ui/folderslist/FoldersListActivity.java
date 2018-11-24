package roiattia.com.mynotes.ui.folderslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.ui.dialogs.DeleteDialog;
import roiattia.com.mynotes.ui.dialogs.ListDialog;
import roiattia.com.mynotes.ui.dialogs.TextInputDialog;
import roiattia.com.mynotes.ui.noteslist.NotesListActivity;
import roiattia.com.mynotes.utils.PreferencesUtil;
import roiattia.com.mynotes.utils.SearchUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static roiattia.com.mynotes.utils.Constants.EMPTY_STRING;
import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_NAME_KEY;
import static roiattia.com.mynotes.utils.Constants.PREF_SORT_FOLDER_BY_OPTION;

public class FoldersListActivity extends AppCompatActivity
    implements FoldersListAdapter.OnFolderClick, TextInputDialog.TextInputDialogListener,
        DeleteDialog.DeleteDialogListener, ListDialog.ListDialogListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = FoldersListActivity.class.getSimpleName();

    private FoldersListAdapter mFoldersAdapter;
    private FoldersListViewModel mViewModel;
    // all folders in the db
    private List<FolderListItem> mFolderListItems;
    // save the ids of the folders checked for deletion
    private List<Long> mFoldersForDeletionIds;
    // save the overall notes count that will get deleted
    private int mNotesForDeletionCount;
    // new folder dialog
    private TextInputDialog mAddFolderDialog;
    // delete folders dialog
    private DeleteDialog mDeleteFoldersDialog;
    // sort folders dialog
    private ListDialog mSortFoldersDialog;

    @BindView(R.id.rv_folders) RecyclerView mFoldersRecyclerView;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;
    @BindView(R.id.cl_delete) ConstraintLayout mDeleteLayout;
    @BindView(R.id.btn_delete) Button mDeleteButton;
    @BindView(R.id.btn_cancel) Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);

//        setupAd();

        mNotesForDeletionCount = 0;

        setTitle(getString(R.string.folder_activity_title));

        setupUI();

        setupRecyclerView();

        setupViewModel();

        getSortedFolders(PreferencesUtil.getSortFoldersByOption(this));
    }

    /**
     * Handle add new folder click event
     */
    @OnClick(R.id.fab_add_folder)
    public void newFolder(){
        // check if the dialog already instantiated
        if(mAddFolderDialog == null){
            mAddFolderDialog = new TextInputDialog();
            mAddFolderDialog.setTitle(getString(R.string.new_folder_dialog_title));
        }
        mAddFolderDialog.show(getSupportFragmentManager(), "folder dialog");
    }

    /**
     * Handle folder click event - open NotesListActivity with folder's notes
     * @param index the clicked folder index
     */
    @Override
    public void onFolderClick(int index) {
        Intent intent = new Intent(FoldersListActivity.this, NotesListActivity.class);
        // send folder's id and name to NotesListActivity
        intent.putExtra(FOLDER_ID_KEY, mFolderListItems.get(index).getId());
        intent.putExtra(FOLDER_NAME_KEY, mFolderListItems.get(index).getName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // reverse list order
            case R.id.mi_swap_list:
                reverseList();
                return true;
            // sort folders list by
            case R.id.mi_sort_folders_by:
                showSortFoldersDialog();
                return true;
            // delete folders
            case R.id.mi_delete_folders:
                showDeleteLayout(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle note delete checkbox check event
     * @param folder the folder item
     * @param addForDeletion the checkbox check status
     */
    @Override
    public void onCheckBoxChecked(FolderListItem folder, boolean addForDeletion) {
        if(mFoldersForDeletionIds == null){
            mFoldersForDeletionIds = new ArrayList<>();
        }
        if(addForDeletion){
            mFoldersForDeletionIds.add(folder.getId());
            mNotesForDeletionCount += folder.getNotesCount();
        } else {
            mFoldersForDeletionIds.remove(folder.getId());
            mNotesForDeletionCount -= folder.getNotesCount();
        }
        // if mNotesForDeletion list > 0 then enable the delete button
        if(mFoldersForDeletionIds.size() > 0){
            mDeleteButton.setEnabled(true);
        }
    }

    /**
     * Handle new folder confirmed dialog action
     * @param input the new folder's name
     */
    @Override
    public void onInputConfirmed(String input) {
        // check if folder's name isn't empty
        if(input.trim().length() <= 0){
            Toast.makeText(this, getString(R.string.folder_name_required_toast_message), Toast.LENGTH_SHORT)
                    .show();
        } else {
            FolderEntity folderEntity = new FolderEntity(input, new LocalDateTime(), new LocalDateTime());
            mViewModel.insertFolder(folderEntity);
            getSortedFolders(PreferencesUtil.getSortFoldersByOption(this));
        }
    }

    /**
     * Handle the confirm delete dialog action
     */
    @Override
    public void onDeleteConfirmed() {
        mViewModel.deleteFoldersById(mFoldersForDeletionIds);
        Toast.makeText(this, "Folders deleted", Toast.LENGTH_SHORT).show();
        getSortedFolders(PreferencesUtil.getSortFoldersByOption(this));
    }

    /**
     * Handle the sort folders by dialog option click
     * @param whichSelected the selected item position
     */
    @Override
    public void onItemSelected(int whichSelected) {
        PreferencesUtil.setSortFoldersByOption(this, whichSelected);
    }

    /**
     * Setup delete layout
     * @param showLayout true - show layout. false - hide layout
     */
    private void showDeleteLayout(boolean showLayout) {
        if(showLayout) {
            // check if there are notes in the list. if not then toast a message
            if (mFoldersAdapter.getItemCount() > 0) {
                mFoldersAdapter.setShowCheckBoxes(true);
                mDeleteLayout.setVisibility(VISIBLE);
                // disable delete button. enable it only if there are notes checked
                // for deletion
                mDeleteButton.setEnabled(false);
            } else {
                Toast.makeText(this, R.string.no_notes_to_delete_message, Toast.LENGTH_SHORT).show();
            }
        } else {
            mDeleteLayout.setVisibility(GONE);
            mFoldersAdapter.setShowCheckBoxes(false);
        }
    }

    /**
     * Setup and show delete dialog
     */
    private void showDeleteDialog() {
        if(mDeleteFoldersDialog == null){
            mDeleteFoldersDialog = new DeleteDialog();
            mDeleteFoldersDialog.setTitle("Delete folders");
        }
        mDeleteFoldersDialog.setMessage(mNotesForDeletionCount + " Notes will be deleted");
        mDeleteFoldersDialog.show(getSupportFragmentManager(), "delete_folders_dialog");
    }

    /**
     * Setup and show sort folders by dialog
     */
    private void showSortFoldersDialog() {
        if(mSortFoldersDialog == null){
            mSortFoldersDialog = new ListDialog();
            mSortFoldersDialog.setTitle("Sort Folders By");
            mSortFoldersDialog.setButtons("cancel", EMPTY_STRING, EMPTY_STRING);
            mSortFoldersDialog.setListItemsStrings(getResources()
                    .getStringArray(R.array.sort_folders_options));
        }
        mSortFoldersDialog.setCheckedItem(PreferencesUtil.getSortFoldersByOption(this));
        mSortFoldersDialog.show(getSupportFragmentManager(), "sort_folders_dialog");
    }

    /**
     * Reverse the folders list order
     */
    private void reverseList() {
        Collections.reverse(mFolderListItems);
        mFoldersAdapter.setFoldersList(mFolderListItems);
    }

    /**
     * Get the sort folders by option from shared preferences
     * @param sortOption the sore option selected: 0 -> notes count. 1 -> las edit date
     */
    private void getSortedFolders(int sortOption) {
        if(sortOption == 0){ // notes count
            mViewModel.loadFoldersByNotesCount();
        } else if(sortOption == 1){ // last edit date
            mViewModel.loadFoldersByEditDate();
        } else {
            Log.i(TAG, "sort option not recognized");
        }
    }

    /**
     * Setup viewModel with observer on the folders list
     */
    private void setupViewModel(){
        mViewModel = ViewModelProviders.of(this).get(FoldersListViewModel.class);
        mViewModel.getFoldersLiveData().observe(this, new Observer<List<FolderListItem>>() {
            @Override
            public void onChanged(@Nullable List<FolderListItem> folderEntities) {
                if(folderEntities != null){
                    mFoldersAdapter.setFoldersList(folderEntities);
                    mFolderListItems = folderEntities;
                    // check if there are folders to show
                    if(folderEntities.size() > 0){
                        mEmptyListMessage.setVisibility(GONE);
                    } else {
                        mEmptyListMessage.setVisibility(VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * Setup folders list recyclerView
     */
    private void setupRecyclerView() {
        mFoldersAdapter = new FoldersListAdapter(this, this);
        mFoldersRecyclerView.setAdapter(mFoldersAdapter);
        mFoldersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFoldersRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Load ad
     */
    private void setupAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * Setup UI elements's with click listeners
     */
    private void setupUI() {
        // set delete layout cancel button click event
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteLayout(false);
            }
        });
        // set delete layout delete button click event
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete all notes selected
                showDeleteDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folders_list, menu);
        // Get the SearchView and set the searchable configuration
        MenuItem item = menu.findItem(R.id.mi_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mFoldersAdapter.setFoldersList(SearchUtils.findFolders(mFolderListItems, query));
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        getSortedFolders(PreferencesUtil.getSortFoldersByOption(this));
        showDeleteLayout(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PREF_SORT_FOLDER_BY_OPTION)){
            getSortedFolders(PreferencesUtil.getSortFoldersByOption(this));
        }
    }

    @Override
    public void onPositiveSelected() { }

    @Override
    public void onNegativeSelected() { }

    @Override
    public void onNeutralSelected() { }
}
