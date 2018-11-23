package roiattia.com.mynotes.ui.folderslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.database.repositories.FoldersRepository;

public class FoldersListViewModel extends AndroidViewModel {

    private FoldersRepository mRepository;
    private AppExecutors mExecutors;
    private LiveData<List<FolderListItem>> mFoldersLiveData;
    private MutableLiveData<List<FolderListItem>> mMutableLiveDataFolders;

    public FoldersListViewModel(@NonNull Application application) {
        super(application);
        mRepository = FoldersRepository.getInstance(application.getApplicationContext());
        mExecutors = AppExecutors.getInstance();
        mFoldersLiveData = mRepository.getFoldersItems();
        mMutableLiveDataFolders = new MutableLiveData<>();
    }

    /**
     * Handle folders list retrieval request
     * @return LiveData object of a list of all the folders in the db
     */
//    public LiveData<List<FolderListItem>> getFoldersLiveData() {
//        return mFoldersLiveData;
//    }

    /**
     * Handle insert new folder to db request
     * @param folderEntity the new folder to enter db
     */
    public void insertFolder(FolderEntity folderEntity) {
        mRepository.insertFolder(folderEntity);
    }

    /**
     * Handle delete folder request
     * @param id the id of the folder to get deleted
     */
    public void deleteFolderById(long id) {
        mRepository.deleteFolderById(id);
    }

    public LiveData<List<FolderListItem>> getFoldersLiveData() {
        return mMutableLiveDataFolders;
    }

    public void loadFoldersByNotesCount() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<FolderListItem> folders = mRepository.getFoldersByNotesCount();
                mMutableLiveDataFolders.postValue(folders);
            }
        });
    }

    public void loadFoldersByEditDate() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<FolderListItem> folders = mRepository.loadFoldersByEditDate();
                mMutableLiveDataFolders.postValue(folders);
            }
        });
    }

    public void deleteFoldersById(List<Long> foldersForDeletionIds) {
        mRepository.deleteFoldersByIds(foldersForDeletionIds);
    }
}
