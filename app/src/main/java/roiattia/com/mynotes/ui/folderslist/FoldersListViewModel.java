package roiattia.com.mynotes.ui.folderslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.database.repositories.FoldersRepository;

public class FoldersListViewModel extends AndroidViewModel {

    private FoldersRepository mRepository;
    private LiveData<List<FolderListItem>> mFoldersLiveData;

    public FoldersListViewModel(@NonNull Application application) {
        super(application);
        mRepository = FoldersRepository.getInstance(application.getApplicationContext());
        mFoldersLiveData = mRepository.getFoldersItems();
    }

    /**
     * Handle folders list retrieval request
     * @return LiveData object of a list of all the folders in the db
     */
    public LiveData<List<FolderListItem>> getFoldersLiveData() {
        return mFoldersLiveData;
    }

    /**
     * Handle insert new folder to db request
     * @param input the new folder's name
     */
    public void insertFolder(String input) {
        mRepository.insertFolder(input);
    }

    /**
     * Handle delete folder request
     * @param id the id of the folder to get deleted
     */
    public void deleteFolderById(long id) {
        mRepository.deleteFolderById(id);
    }
}
