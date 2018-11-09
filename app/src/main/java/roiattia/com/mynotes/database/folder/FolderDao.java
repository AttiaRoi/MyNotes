package roiattia.com.mynotes.database.folder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
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

    @Delete
    void deleteFolder(FolderEntity folderEntity);

    @Query("SELECT * FROM folder WHERE id = :id")
    FolderEntity getFolderById(long id);

    @Query("SELECT * FROM folder")
    LiveData<List<FolderEntity>> getAllFolders();

    @Query("SELECT folder.name AS mName, folder.id AS mId, COUNT(note.id) AS mNotesCount " +
            "FROM folder LEFT JOIN note ON folder.id = note.folder_id " +
            "GROUP BY folder.id")
    LiveData<List<FolderListItem>> getAllFoldersItems();

    @Query("DELETE FROM folder WHERE id=:id")
    void deleteFolderById(long id);
}
