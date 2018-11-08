package roiattia.com.mynotes.ui.folderslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.ui.dialogs.NewFolderDialog;
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
    private List<FolderListItem> mFolderListItems;

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long folderId = viewHolder.itemView.getId();
            }
        }).attachToRecyclerView(mFoldersRecyclerView);
    }
}
