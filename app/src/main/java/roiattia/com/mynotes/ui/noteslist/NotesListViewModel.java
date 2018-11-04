package roiattia.com.mynotes.ui.noteslist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import roiattia.com.mynotes.database.repositories.NotesRepository;
import roiattia.com.mynotes.database.note.NoteEntity;

public class NotesListViewModel extends AndroidViewModel {

    private NotesRepository mRepository;
    private LiveData<List<NoteEntity>> mNotesList;

    public NotesListViewModel(@NonNull Application application) {
        super(application);
        mRepository = NotesRepository.getInstance(application.getApplicationContext());
        mNotesList = mRepository.getNotes();
    }

    public LiveData<List<NoteEntity>> getNotesList(){
        return mNotesList;
    }

    public void insertData(List<NoteEntity> notes) {
        mRepository.insertNotes(notes);
    }

    public void deleteAllNotes() {
        mRepository.deleteAllNotes();
    }

    public void deleteNotes(List<NoteEntity> notesForDeletion) {
        mRepository.deleteNotes(notesForDeletion);
    }
}
