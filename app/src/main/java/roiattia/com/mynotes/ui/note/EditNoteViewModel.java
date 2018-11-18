package roiattia.com.mynotes.ui.note;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import roiattia.com.mynotes.model.NoteItem;
import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.repositories.FoldersRepository;
import roiattia.com.mynotes.database.repositories.NotesRepository;
import roiattia.com.mynotes.database.note.NoteEntity;

public class EditNoteViewModel extends AndroidViewModel {

    private static final String TAG = EditNoteViewModel.class.getSimpleName();
    private NotesRepository mNotesRepository;
    private FoldersRepository mFoldersRepository;
    private AppExecutors mExecutors;
    private MutableLiveData<NoteItem> mMutableLiveNote;
    private MutableLiveData<FolderEntity> mMutableLiveFolder;

    public EditNoteViewModel(@NonNull Application application) {
        super(application);
        mNotesRepository = NotesRepository.getInstance(application.getApplicationContext());
        mFoldersRepository = FoldersRepository.getInstance(application.getApplicationContext());
        mExecutors = AppExecutors.getInstance();
        mMutableLiveNote = new MutableLiveData<>();
        mMutableLiveFolder = new MutableLiveData<>();
    }


    public void loadFolder(final long folderId) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FolderEntity folder = mFoldersRepository.getFolderById(folderId);
                mMutableLiveFolder.postValue(folder);
            }
        });
    }

    public MutableLiveData<FolderEntity> getMutableLiveFolder() {
        return mMutableLiveFolder;
    }

    public void loadNote(final long noteId){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                NoteItem NoteItem = mNotesRepository.getNoteById(noteId);
                mMutableLiveNote.postValue(NoteItem);
            }
        });
    }

    public MutableLiveData<NoteItem> getMutableLiveNote(){
        return mMutableLiveNote;
    }

    public LiveData<List<FolderEntity>> getFoldersLiveData() {
        return mFoldersRepository.getFolders();
    }

    public void saveNote(NoteEntity note) {
        Log.i(TAG, note.toString());
        mNotesRepository.insertNote(note);
    }

    public void deleteNote() {
        if(mMutableLiveNote.getValue() != null) {
            mNotesRepository.deleteNoteById(mMutableLiveNote.getValue().getNoteId());
        }
    }

    public void inertNewFolder(String input, FoldersRepository.FoldersRepositoryListener listener) {
        mFoldersRepository.insertFolder(input, listener);
    }
}
