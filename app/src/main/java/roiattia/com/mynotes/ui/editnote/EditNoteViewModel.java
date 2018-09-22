package roiattia.com.mynotes.ui.editnote;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import roiattia.com.mynotes.database.AppExecutors;
import roiattia.com.mynotes.database.AppRepository;
import roiattia.com.mynotes.database.NoteEntity;

public class EditNoteViewModel extends AndroidViewModel {

    private static final String TAG = EditNoteViewModel.class.getSimpleName();
    private MutableLiveData<NoteEntity> mMutableLiveNote;
    private AppRepository mRepository;
    private AppExecutors mExecutors;

    public EditNoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mExecutors = AppExecutors.getInstance();
        mMutableLiveNote = new MutableLiveData<>();
    }

    public MutableLiveData<NoteEntity> getMutableLiveNote(){
        return mMutableLiveNote;
    }

    public void loadNote(final int noteId){
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                NoteEntity noteEntity = mRepository.getNoteById(noteId);
                mMutableLiveNote.postValue(noteEntity);
            }
        });
    }

    public void saveNote(String noteText) {
        NoteEntity noteEntity = mMutableLiveNote.getValue();
        if(noteEntity == null){
            if(TextUtils.isEmpty(noteText.trim())){
                return;
            }
            noteEntity = new NoteEntity(new Date(), noteText.trim());
        } else {
            noteEntity.setText(noteText);
        }
        mRepository.insertNote(noteEntity);
    }

    public void deleteNote() {
        mRepository.deleteNote(mMutableLiveNote.getValue());
    }
}
