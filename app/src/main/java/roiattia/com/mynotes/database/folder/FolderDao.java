package roiattia.com.mynotes.database.folder;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFolder(FolderEntity folderEntity);

    @Delete
    void deleteFolder(FolderEntity folderEntity);

    @Query("SELECT * FROM folder WHERE id = :id")
    FolderEntity getFolderById(int id);

    @Query("SELECT * FROM folder ORDER BY date DESC")
    LiveData<List<FolderEntity>> getAllFolders();
}
