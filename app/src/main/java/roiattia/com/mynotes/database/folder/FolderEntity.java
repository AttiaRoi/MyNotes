package roiattia.com.mynotes.database.folder;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

@Entity(tableName = "folder")
public class FolderEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "folder_id")
    private long mId;
    @ColumnInfo(name = "creation_date")
    private LocalDateTime mCreationDate;
    @ColumnInfo(name = "last_note_inserted_date")
    private LocalDateTime mLastNoteInsertedDate;
    @ColumnInfo(name = "folder_name")
    private String mName;

    public FolderEntity(long id, LocalDateTime creationDate,
                        LocalDateTime lastNoteInsertedDate, String name) {
        mId = id;
        mCreationDate = creationDate;
        mLastNoteInsertedDate = lastNoteInsertedDate;
        mName = name;
    }

    @Ignore
    public FolderEntity(LocalDateTime creationDate, String name) {
        mCreationDate = creationDate;
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public LocalDateTime getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        mCreationDate = creationDate;
    }

    public LocalDateTime getLastNoteInsertedDate() {
        return mLastNoteInsertedDate;
    }

    public void setLastNoteInsertedDate(LocalDateTime lastNoteInsertedDate) {
        mLastNoteInsertedDate = lastNoteInsertedDate;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
