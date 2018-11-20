package roiattia.com.mynotes.database.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;

import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.database.AppDatabase;
import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.folder.FolderEntity;

public class FoldersRepository {

    private static final String TAG = FoldersRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FoldersRepository sInstance;
    private AppDatabase mDatabase;
    private AppExecutors mExecutors;

    public void deleteFolderById(final long id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.folderDao().deleteFolderById(id);
            }
        });
    }

    public interface FoldersRepositoryListener{
        void onFolderInserted(FolderEntity folder);
    }

    public static FoldersRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new FoldersRepository(context);
            }
        }
        return sInstance;
    }



    private FoldersRepository(Context context) {
        mDatabase = AppDatabase.getsInstance(context);
        mExecutors = AppExecutors.getInstance();
    }

    public FolderEntity getFolderById(long folderId) {
        return mDatabase.folderDao().getFolderById(folderId);
    }

    public LiveData<List<FolderListItem>> getFoldersItems() {
        return mDatabase.folderDao().getAllFoldersItems();
    }

    public void insertFolder(final String input) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FolderEntity folder = new FolderEntity(new LocalDateTime(), input);
                mDatabase.folderDao().insertFolder(folder);
            }
        });
    }
    public void insertFolder(final String input, final FoldersRepositoryListener listener) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FolderEntity folder = new FolderEntity(new LocalDateTime(), input);
                long id = mDatabase.folderDao().insertFolderWithCallback(folder);
                folder.setId(id);
                listener.onFolderInserted(folder);
            }
        });
    }

    public LiveData<List<FolderEntity>> getFolders() {
        return mDatabase.folderDao().getAllFolders();
    }
}
