package roiattia.com.mynotes.database.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;

import roiattia.com.mynotes.model.NoteItem;
import roiattia.com.mynotes.database.AppDatabase;
import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.note.NoteEntity;

public class NotesRepository {

    private static final String TAG = NotesRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static NotesRepository sInstance;
    private AppDatabase mDatabase;
    private AppExecutors mExecutors;

    public static NotesRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NotesRepository(context);
            }
        }
        return sInstance;
    }

    private NotesRepository(Context context) {
        mDatabase = AppDatabase.getsInstance(context);
        mExecutors = AppExecutors.getInstance();
    }

    public LiveData<List<NoteEntity>> getNotes(){
        return mDatabase.noteDao().getAllNotes();
    }

    public NoteItem getNoteById(long noteId) {
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

    public void deleteNotes(final List<NoteEntity> notesForDeletion) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().deleteNotes(notesForDeletion);
            }
        });
    }

    public void deleteNoteById(final int id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().deleteNoteById(id);
            }
        });
    }

    public LiveData<List<NoteEntity>> getNotesByFolderId(long folderId) {
        return mDatabase.noteDao().getNotesByFolderId(folderId);
    }

    public List<NoteEntity> getNotesByCreationDate() {
        return mDatabase.noteDao().getNotesByCreationDate();
    }

    public List<NoteEntity> getNotesByLastEditDate() {
        return mDatabase.noteDao().getNotesByLastEditDate();
    }

    public List<NoteEntity> loadNotesByReminderDate() {
        return mDatabase.noteDao().loadNotesByReminderDate();
    }

    public void insertNotes(final List<NoteEntity> firstSetupNotes) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.noteDao().insertAllNotes(firstSetupNotes);
            }
        });
    }
}
