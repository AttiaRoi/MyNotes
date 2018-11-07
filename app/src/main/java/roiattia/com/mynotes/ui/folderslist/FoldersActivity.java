package roiattia.com.mynotes.ui.folderslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.ui.dialogs.EditTextDialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FoldersActivity extends AppCompatActivity
    implements FoldersAdapter.OnFolderClick, EditTextDialog.EditTextDialogListener{

    private FoldersAdapter mFoldersAdapter;
    private FoldersListViewModel mViewModel;
    private EditTextDialog mAddFolderDialog;

    @BindView(R.id.rv_folders) RecyclerView mFoldersRecyclerView;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;

    @OnClick(R.id.fab_add_folder)
    public void addFolder(){
        if(mAddFolderDialog == null){
            mAddFolderDialog = new EditTextDialog();
        }
        mAddFolderDialog.show(getSupportFragmentManager(), "folder dialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        ButterKnife.bind(this);

        setTitle("My Folders");

        setupRecyclerView();

        setupViewModel();
    }

    private void setupViewModel(){
        Observer<List<FolderListItem>> observer = new Observer<List<FolderListItem>>() {
            @Override
            public void onChanged(@Nullable List<FolderListItem> folderEntities) {
                if(folderEntities != null){
                    mFoldersAdapter.setFoldersList(folderEntities);
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

    private void setupRecyclerView() {
        mFoldersAdapter = new FoldersAdapter(this, this);
        mFoldersRecyclerView.setAdapter(mFoldersAdapter);
        mFoldersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFoldersRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onFolderClick(int index) {

    }

    @Override
    public void onDialogFinishClick(String input) {
        if(input.trim().length() < 0){
            Toast.makeText(this, "Folder name required", Toast.LENGTH_SHORT).show();
        } else {
            mViewModel.insertFolder(input);
        }
    }
}
