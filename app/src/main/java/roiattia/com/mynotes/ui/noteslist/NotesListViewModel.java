package roiattia.com.mynotes.ui.noteslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.List;

import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.repositories.NotesRepository;
import roiattia.com.mynotes.database.note.NoteEntity;

public class NotesListViewModel extends AndroidViewModel {

    private NotesRepository mNotesRepository;

    public NotesListViewModel(@NonNull Application application) {
        super(application);
        mNotesRepository = NotesRepository.getInstance(application.getApplicationContext());
    }

    /**
     * Handle note list retrieval request
     * @return LiveData object of List<NoteEntity> with all the notes in the db
     */
    public LiveData<List<NoteEntity>> getNotesLiveData(){
        return mNotesRepository.getNotes();
    }

    /**
     * Handle delete notes request
     * @param notesForDeletion the list of the notes to get deleted
     */
    public void deleteNotes(List<NoteEntity> notesForDeletion) {
        mNotesRepository.deleteNotes(notesForDeletion);
    }

    /**
     * Handle note list of a specific folder retrieval request
     * @param folderId the folder id of which notes are to fetch
     * @return LiveData object of List<NoteEntity> with all the notes of the specific folder
     */
    public LiveData<List<NoteEntity>> getNotesByFolderIdLiveData(long folderId) {
        return mNotesRepository.getNotesByFolderId(folderId);
    }

    /**
     * Handle new note request by record action
     * @param text of the note to save
     */
    public void insertNoteByRecord(String text) {
        NoteEntity note = new NoteEntity(new LocalDateTime(), new LocalDateTime(), text);
        mNotesRepository.insertNote(note);
    }
}
