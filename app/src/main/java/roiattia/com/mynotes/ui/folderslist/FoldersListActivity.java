package roiattia.com.mynotes.ui.folderslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.ui.dialogs.NewFolderDialog;
import roiattia.com.mynotes.ui.note.EditNoteActivity;
import roiattia.com.mynotes.ui.noteslist.NotesListActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_NAME_KEY;

public class FoldersListActivity extends AppCompatActivity
    implements FoldersListAdapter.OnFolderClick, NewFolderDialog.NewFolderDialogListener,
    DeleteFolderDialog.DeleteFolderDialogListener{

    private FoldersListAdapter mFoldersAdapter;
    private FoldersListViewModel mViewModel;
    private NewFolderDialog mAddFolderDialog;
    private DeleteFolderDialog mDeleteFolderDialog;
    // All folders in the db
    private List<FolderListItem> mFolderListItems;
    // Folders that meet a search query
    private List<FolderListItem> mSearchedFolders;
    private long mFolderIdForDeletion;

    @BindView(R.id.rv_folders) RecyclerView mFoldersRecyclerView;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);

        setupAd();

        setTitle(getString(R.string.folder_activity_title));

        setupRecyclerView();

        setupViewModel();
    }

    /**
     * Handle add new folder click event
     */
    @OnClick(R.id.fab_add_folder)
    public void addFolder(){
        // check if the dialog already instantiated
        if(mAddFolderDialog == null){
            mAddFolderDialog = new NewFolderDialog();
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

    /**
     * Handle the delete folder action
     * @param index the index of the folder that was clicked in the list
     */
    @Override
    public void onDeleteFolder(final int index) {
        // get the correct folder from the list
        final FolderListItem folderItem = mFolderListItems.get(index);
        mFolderIdForDeletion = folderItem.getId();
        // check if the dialog builder already instantiated
        if(mDeleteFolderDialog == null) {
            mDeleteFolderDialog = new DeleteFolderDialog();
        }
        // set title
        mDeleteFolderDialog.setTitle(String.format("%s %s %s",
                getString(R.string.delete_folder_dialog_delete),
                folderItem.getName(),
                getString(R.string.delete_folder_dialog_folder)));
        if(folderItem.getNotesCount() > 0){
            mDeleteFolderDialog.setMessage(String.format("%s %s",
                    folderItem.getNotesCount(),
                    getString(R.string.delete_folder_dialog_notes_count)));
        } else {
            mDeleteFolderDialog.setMessage(getString(R.string.delete_folder_dialog_empty_folder));
        }
        mDeleteFolderDialog.show(getSupportFragmentManager(), "delete_folder_dialog");
    }

    /**
     * Handle new note action
     * @param index the index of the folder
     */
    @Override
    public void onNewNoteInFolder(final int index) {
        Intent intent = new Intent(FoldersListActivity.this, EditNoteActivity.class);
        intent.putExtra(FOLDER_ID_KEY, mFolderListItems.get(index).getId());
        startActivity(intent);
    }

    /**
     * Handle new folder confirmed dialog action
     * @param input the new folder's name
     */
    @Override
    public void onFolderConfirmed(String input) {
        // check if folder's name isn't empty
        if(input.trim().length() <= 0){
            Toast.makeText(this, getString(R.string.folder_name_required_toast_message), Toast.LENGTH_SHORT)
                    .show();
        } else {
            mViewModel.insertFolder(input);
        }
    }

    /**
     * Handle confirm folder delete action
     */
    @Override
    public void onDeleteFolderConfirmed() {
        mViewModel.deleteFolderById(mFolderIdForDeletion);
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
            public boolean onQueryTextChange(String newText) {
                searchFolders(newText);
                return false;
            }
        });
        return true;
    }

    /**
     * Handle search query
     * @param newText the query to search on
     */
    private void searchFolders(String newText) {
        // check if list instantiated, if it is then empty it for new query
        if(mSearchedFolders == null){
            mSearchedFolders = new ArrayList<>();
        } else {
            mSearchedFolders.clear();
        }
        // search for folders in the entire folders list
        for(FolderListItem folder : mFolderListItems){
            if(folder.getName().toLowerCase().contains(newText.toLowerCase())){
                mSearchedFolders.add(folder);
            }
        }
        mFoldersAdapter.setFoldersList(mSearchedFolders);
    }

    /**
     * Setup viewModel with observer on the folders list
     */
    private void setupViewModel(){
        Observer<List<FolderListItem>> observer = new Observer<List<FolderListItem>>() {
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
        };

        mViewModel = ViewModelProviders.of(this).get(FoldersListViewModel.class);
        mViewModel.getFoldersLiveData().observe(this, observer);
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

}
