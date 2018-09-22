package roiattia.com.mynotes.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntity noteEntity);

    @Insert
    void insertAllNotes(List<NoteEntity> noteEntities);

    @Delete
    void deleteNote(NoteEntity noteEntity);

    @Query("DELETE FROM note")
    int deleteAllNotes();

    @Query("SELECT * FROM note WHERE id = :id")
    NoteEntity getNoteById(int id);

    @Query("SELECT * FROM note ORDER BY date DESC")
    LiveData<List<NoteEntity>> getAllNotes();

    @Query("SELECT COUNT(*) FROM note")
    int getNotesCount();
}
