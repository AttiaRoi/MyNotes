package roiattia.com.mynotes.ui.noteslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.repositories.FoldersRepository;
import roiattia.com.mynotes.database.repositories.NotesRepository;
import roiattia.com.mynotes.database.note.NoteEntity;

import static roiattia.com.mynotes.utils.Constants.RECYCLE_BIN;

public class NotesListViewModel extends AndroidViewModel {

    private NotesRepository mNotesRepository;
    private AppExecutors mExecutors;
    private MutableLiveData<List<NoteEntity>> mMutableLiveDataNotes;

    public NotesListViewModel(@NonNull Application application) {
        super(application);
        mNotesRepository = NotesRepository.getInstance(application.getApplicationContext());
        mExecutors = AppExecutors.getInstance();
        mMutableLiveDataNotes = new MutableLiveData<>();
    }

    public LiveData<List<NoteEntity>> getLiveDataNotes() {
        return mMutableLiveDataNotes;
    }

    /**
     * Handle delete notes request
     * @param notesForDeletion the list of the notes to get deleted
     */
    public void deleteNotes(List<NoteEntity> notesForDeletion) {
        mNotesRepository.deleteNotes(notesForDeletion);
    }

    /**
     * Handle new note request by record action
     * @param text of the note to save
     */
    public void insertNoteByRecord(String text) {
        NoteEntity note = new NoteEntity(new LocalDateTime(), new LocalDateTime(), text);
        mNotesRepository.insertNote(note);
    }

    public void loadNotesByCreationDate() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<NoteEntity> notes = mNotesRepository.getNotesByCreationDate();
                mMutableLiveDataNotes.postValue(notes);
            }
        });
    }

    public void loadNotesByLastEditDate() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<NoteEntity> notes = mNotesRepository.getNotesByLastEditDate();
                mMutableLiveDataNotes.postValue(notes);
            }
        });
    }

    public void loadNotesByReminderDate() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<NoteEntity> notes = mNotesRepository.loadNotesByReminderDate();
                mMutableLiveDataNotes.postValue(notes);
            }
        });
    }

    public void setupFirstTimeSetup() {
        NoteEntity firstNote = new NoteEntity(new LocalDateTime(), new LocalDateTime(),
                "This is an example note. enjoy this app :)");
        NoteEntity deleteNote = new NoteEntity(new LocalDateTime(), new LocalDateTime(),
                "This is an example deleted note. all deleted notes will be saved in " +
                        "the recycler bin for you to recover or to delete entirely");
        deleteNote.setId(9999);
        deleteNote.setInRecyclerBin(true);
        List<NoteEntity> firstSetupNotes = new ArrayList<>();
        firstSetupNotes.add(firstNote);
        firstSetupNotes.add(deleteNote);
        mNotesRepository.insertNotes(firstSetupNotes);
    }

    /***********************************************************************************************
     * Handle note list retrieval request
     * @return LiveData object of List<NoteEntity> with all the notes in the db
     */
    public LiveData<List<NoteEntity>> getNotesLiveData(){
        return mNotesRepository.getNotes();
    }

    /**
     * Handle note list of a specific folder retrieval request
     * @param folderId the folder id of which notes are to fetch
     * @return LiveData object of List<NoteEntity> with all the notes of the specific folder
     */
    public LiveData<List<NoteEntity>> getNotesByFolderIdLiveData(long folderId) {
        return mNotesRepository.getNotesByFolderId(folderId);
    }
}
