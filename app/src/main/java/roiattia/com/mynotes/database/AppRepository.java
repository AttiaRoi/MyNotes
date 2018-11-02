package roiattia.com.mynotes.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;

import roiattia.com.mynotes.utils.DummyData;

public class AppRepository {

    private static final String TAG = AppRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static AppRepository sInstance;
    private AppDatabase mDatabase;
    private AppExecutors mExecutors;

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppRepository(context);
            }
        }
        return sInstance;
    }

    private AppRepository(Context context) {
        mDatabase = AppDatabase.getsInstance(context);
        mExecutors = AppExecutors.getInstance();
    }

    public LiveData<List<NoteEntity>> getNotes(){
        return mDatabase.noteDao().getAllNotes();
    }

    public void insertNotes(final List<NoteEntity> notes){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().insertAllNotes(notes);
            }
        });
    }

    public void deleteAllNotes() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int rowsDelete = mDatabase.noteDao().deleteAllNotes();
                Log.i(TAG, "number of rows deleted: " + rowsDelete);
            }
        });
    }

    public NoteEntity getNoteById(int noteId) {
        return mDatabase.noteDao().getNoteById(noteId);
    }

    public void insertNote(final NoteEntity noteEntity) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().insertNote(noteEntity);
            }
        });
    }

    public void deleteNote(final NoteEntity noteEntity) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().deleteNote(noteEntity);
            }
        });
    }

    public void deleteNotes(final List<NoteEntity> notesForDeletion) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().deleteNotes(notesForDeletion);
            }
        });
    }
}
