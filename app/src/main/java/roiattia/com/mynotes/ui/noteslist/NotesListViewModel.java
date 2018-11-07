package roiattia.com.mynotes.ui.noteslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import roiattia.com.mynotes.database.repositories.NotesRepository;
import roiattia.com.mynotes.database.note.NoteEntity;

public class NotesListViewModel extends AndroidViewModel {

    private NotesRepository mNotesRepository;
    private LiveData<List<NoteEntity>> mNotesLiveData;

    public NotesListViewModel(@NonNull Application application) {
        super(application);
        mNotesRepository = NotesRepository.getInstance(application.getApplicationContext());
        mNotesLiveData = mNotesRepository.getNotes();
    }

    public LiveData<List<NoteEntity>> getNotesLiveData(){
        return mNotesLiveData;
    }

    public void insertData(List<NoteEntity> notes) {
        mNotesRepository.insertNotes(notes);
    }

    public void deleteAllNotes() {
        mNotesRepository.deleteAllNotes();
    }

    public void deleteNotes(List<NoteEntity> notesForDeletion) {
        mNotesRepository.deleteNotes(notesForDeletion);
    }
}
