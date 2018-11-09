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

    public LiveData<List<FolderListItem>> getFoldersLiveData() {
        return mFoldersLiveData;
    }

    public void insertFolder(String input) {
        mRepository.insertFolder(input);
    }

    public void deleteFolderById(long id) {
        mRepository.deleteFolderById(id);
    }
}
