package roiattia.com.mynotes.ui.editnote;

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
    private MutableLiveData<NoteEntity> mMutableLiveNoteEntity;

    public EditNoteViewModel(@NonNull Application application) {
        super(application);
        mNotesRepository = NotesRepository.getInstance(application.getApplicationContext());
        mFoldersRepository = FoldersRepository.getInstance(application.getApplicationContext());
        mExecutors = AppExecutors.getInstance();
        mMutableLiveNote = new MutableLiveData<>();
        mMutableLiveNoteEntity = new MutableLiveData<>();
    }

    public LiveData<List<FolderEntity>> getFoldersLiveData() {
        return mFoldersRepository.getFolders();
    }

    public MutableLiveData<NoteItem> getMutableLiveNote(){
        return mMutableLiveNote;
    }

    public MutableLiveData<NoteEntity> getMutableLiveNoteEntity() {
        return mMutableLiveNoteEntity;
    }

    public void saveNote(NoteEntity note) {
        Log.i(TAG, note.toString());
        mNotesRepository.insertNote(note);
    }

    public void deleteNote() {
        if(mMutableLiveNote.getValue() != null) {
            mNotesRepository.deleteNoteById(mMutableLiveNote.getValue().getId());
        }
    }

    public void inertNewFolder(String input, FoldersRepository.FoldersRepositoryListener listener) {
        mFoldersRepository.insertFolder(input, listener);
    }

    public void loadNote(final int noteId){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                NoteEntity noteEntity = mNotesRepository.getNoteById(noteId);
                mMutableLiveNoteEntity.postValue(noteEntity);
            }
        });
    }

    public void loadNote(final int noteId, final long folderId) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                NoteItem noteItem = mNotesRepository.getNoteItemById(noteId, folderId);
                mMutableLiveNote.postValue(noteItem);
            }
        });
    }
}
