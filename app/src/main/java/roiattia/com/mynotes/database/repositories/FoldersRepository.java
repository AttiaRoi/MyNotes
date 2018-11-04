package roiattia.com.mynotes.database.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import org.joda.time.LocalDate;

import java.util.List;

import roiattia.com.mynotes.database.AppDatabase;
import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.folder.FolderEntity;

public class FoldersRepository {

    private static final String TAG = FoldersRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FoldersRepository sInstance;
    private AppDatabase mDatabase;
    private AppExecutors mExecutors;

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

    public LiveData<List<FolderEntity>> getFolders() {
        return mDatabase.folderDao().getAllFolders();
    }

    public void insertFolder(final String input) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FolderEntity folder = new FolderEntity(new LocalDate(), input, 0);
                mDatabase.folderDao().insertFolder(folder);
            }
        });
    }
}
