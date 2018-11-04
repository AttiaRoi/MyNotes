package roiattia.com.mynotes;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import roiattia.com.mynotes.database.AppDatabase;
import roiattia.com.mynotes.database.note.NoteDao;
import roiattia.com.mynotes.utils.DummyData;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private static final String TAG = DatabaseTest.class.getSimpleName();
    private AppDatabase mDatabase;
    private NoteDao mNoteDao;

    @Before
    public void createDatabase(){
        Context context = InstrumentationRegistry.getTargetContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mNoteDao = mDatabase.noteDao();
        Log.i(TAG, "database created");
    }

    @After
    public void closeDatabase(){
        mDatabase.close();
        Log.i(TAG, "database closed");
    }

    @Test
    public void createNotes(){
        mNoteDao.insertAllNotes(DummyData.getDummyData());
        int notesCount = mNoteDao.getNotesCount();
        assertEquals(DummyData.getDummyData().size(), notesCount);
    }


}
