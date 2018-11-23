package roiattia.com.mynotes.database.folder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import roiattia.com.mynotes.model.FolderListItem;

@Dao
public interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(FolderEntity folderEntity);

    @Insert
    long insertFolderWithCallback(FolderEntity folderEntity);

    @Query("SELECT * FROM folder WHERE folder_id = :id")
    FolderEntity getFolderById(long id);

    @Query("SELECT * FROM folder")
    LiveData<List<FolderEntity>> getAllFolders();

    @Query("SELECT folder.folder_name AS mName, folder.folder_id AS mId, COUNT(note.note_id) AS mNotesCount, " +
            "folder.last_edited_date AS mLastEditedDate FROM folder LEFT JOIN note ON " +
            "folder.folder_id = note.folder_id " +
            "GROUP BY folder.folder_id ORDER BY COUNT(note.note_id) DESC")
    LiveData<List<FolderListItem>> getAllFoldersItems();

    @Query("DELETE FROM folder WHERE folder_id=:id")
    void deleteFolderById(long id);

    @Query("SELECT folder.folder_name AS mName, folder.folder_id AS mId, COUNT(note.note_id) AS mNotesCount, " +
            "folder.last_edited_date AS mLastEditedDate FROM folder LEFT JOIN note ON " +
            "folder.folder_id = note.folder_id " +
            "GROUP BY folder.folder_id ORDER BY COUNT(note.note_id) DESC")
    List<FolderListItem> getFoldersByNotesCount();

    @Query("SELECT folder.folder_name AS mName, folder.folder_id AS mId, COUNT(note.note_id) AS mNotesCount, " +
            "folder.last_edited_date AS mLastEditedDate FROM folder LEFT JOIN note ON " +
            "folder.folder_id = note.folder_id " +
            "GROUP BY folder.folder_id ORDER BY folder.last_edited_date DESC")
    List<FolderListItem> loadFoldersByEditDate();

    @Query("DELETE FROM folder WHERE folder_id IN (:foldersForDeletionIds)")
    void deleteFoldersByIds(List<Long> foldersForDeletionIds);
}
