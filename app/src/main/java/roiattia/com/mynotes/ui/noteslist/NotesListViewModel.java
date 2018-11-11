package roiattia.com.mynotes.ui.noteslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
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

    public LiveData<List<NoteEntity>> getNotesLiveData(){
        return mNotesRepository.getNotes();
    }

    public void deleteNotes(List<NoteEntity> notesForDeletion) {
        mNotesRepository.deleteNotes(notesForDeletion);
    }

    public LiveData<List<NoteEntity>> getNotesByFolderIdLiveData(long folderId) {
        return mNotesRepository.getNotesByFolderId(folderId);
    }

    public void insertNewNote(String text) {
        mNotesRepository.insertNote(new NoteEntity(new LocalDate(), new LocalTime(), text));
    }
}
