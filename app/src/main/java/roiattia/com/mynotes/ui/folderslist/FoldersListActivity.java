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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
    implements FoldersListAdapter.OnFolderClick, NewFolderDialog.NewFolderDialogListener {

    private FoldersListAdapter mFoldersAdapter;
    private FoldersListViewModel mViewModel;
    private NewFolderDialog mAddFolderDialog;
    private AlertDialog.Builder mDeleteFolderBuilder;
    private List<FolderListItem> mFolderListItems;
    private List<FolderListItem> mSearchedFolders;

    @BindView(R.id.rv_folders) RecyclerView mFoldersRecyclerView;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);

        setTitle(getString(R.string.folder_activity_title));

        setupRecyclerView();

        setupViewModel();
    }

    @OnClick(R.id.fab_add_folder)
    public void addFolder(){
        if(mAddFolderDialog == null){
            mAddFolderDialog = new NewFolderDialog();
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
        intent.putExtra(FOLDER_ID_KEY, mFolderListItems.get(index).getId());
        intent.putExtra(FOLDER_NAME_KEY, mFolderListItems.get(index).getName());
        startActivity(intent);
    }

    @Override
    public void onDeleteFolder(final int index) {
        final FolderListItem folderItem = mFolderListItems.get(index);
        if(mDeleteFolderBuilder == null) {
            mDeleteFolderBuilder = new AlertDialog.Builder(this);
        }
        // set title
        mDeleteFolderBuilder.setTitle("Delete " + folderItem.getName() + " Folder");
        // set dialog message
        mDeleteFolderBuilder
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mViewModel.deleteFolderById(folderItem.getId());
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        if(folderItem.getNotesCount() > 0){
            mDeleteFolderBuilder.setMessage(folderItem.getNotesCount() + " Notes will be deleted");
        } else {
            mDeleteFolderBuilder.setMessage("Empty folder");
        }
        AlertDialog deleteFolderDialog = mDeleteFolderBuilder.create();
        deleteFolderDialog.show();
    }

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
        if(input.trim().length() < 0){
            Toast.makeText(this, getString(R.string.folder_name_required_toast_message), Toast.LENGTH_SHORT)
                    .show();
        } else {
            mViewModel.insertFolder(input);
        }
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

    private void searchFolders(String newText) {
        if(mSearchedFolders == null){
            mSearchedFolders = new ArrayList<>();
        } else {
            mSearchedFolders.clear();
        }
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
}
