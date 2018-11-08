package roiattia.com.mynotes.database.note;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import roiattia.com.mynotes.model.NoteItem;

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

    @Query("SELECT note.id AS id, note.date AS date, note.time AS time, note.text AS text, " +
            "folder.name AS folderName, folder.id AS folderId FROM note LEFT JOIN folder on " +
            "note.folder_id = folder.id WHERE note.id =:id")
    NoteItem getNoteById(long id);

    @Query("SELECT * FROM note ORDER BY date DESC")
    LiveData<List<NoteEntity>> getAllNotes();

    @Query("SELECT COUNT(*) FROM note")
    int getNotesCount();

    @Query("SELECT note.id AS id, note.date AS date, note.time AS time, note.text AS text, " +
            "folder.name AS folderName, folder.id AS folderId FROM note JOIN folder on :folderId = folder.id " +
            "WHERE note.id = :id")
    NoteItem getNoteItemById(int id, long folderId);

    @Delete
    void deleteNotes(List<NoteEntity> notesForDeletion);

    @Query("DELETE FROM note WHERE id = :id")
    void deleteNoteById(long id);

    @Query("SELECT * FROM note WHERE folder_id=:folderId ORDER BY date DESC")
    LiveData<List<NoteEntity>> getNotesByFolderId(long folderId);
}
