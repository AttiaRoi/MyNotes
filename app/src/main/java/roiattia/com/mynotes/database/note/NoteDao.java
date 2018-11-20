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

    @Query("SELECT note.note_id AS mNoteId,note.creation_date AS mCreationDate, " +
            "note.last_edit_date AS mLastEditDate, note.reminder_date AS mReminderDate, " +
            "note.note_text AS mNoteText, folder.folder_name AS mFolderName, " +
            "folder.folder_id AS mFolderId FROM note LEFT JOIN folder on " +
            "note.folder_id = folder.folder_id WHERE note.note_id =:id")
    NoteItem getNoteById(long id);

    @Query("SELECT * FROM note ORDER BY last_edit_date DESC")
    LiveData<List<NoteEntity>> getAllNotes();

    @Query("SELECT COUNT(*) FROM note")
    int getNotesCount();

    @Delete
    void deleteNotes(List<NoteEntity> notesForDeletion);

    @Query("DELETE FROM note WHERE note_id = :id")
    void deleteNoteById(long id);

    @Query("SELECT * FROM note WHERE folder_id=:folderId ORDER BY last_edit_date DESC")
    LiveData<List<NoteEntity>> getNotesByFolderId(long folderId);
}
