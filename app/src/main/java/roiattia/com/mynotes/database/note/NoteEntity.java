package roiattia.com.mynotes.database.note;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.joda.time.LocalDateTime;

import roiattia.com.mynotes.database.folder.FolderEntity;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "note",
        foreignKeys = @ForeignKey(entity = FolderEntity.class,
        parentColumns = "id",
        childColumns = "folder_id",
        onDelete = CASCADE))
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private long mId;
    @ColumnInfo(name = "folder_id")
    private Long mFolderId;
    @ColumnInfo(name = "creation_date")
    private LocalDateTime mCreationDate;
    @ColumnInfo(name = "last_edit_date")
    private LocalDateTime mLastEditDate;
    @ColumnInfo(name = "note_text")
    private String mText;
    @ColumnInfo(name = "reminder_date")
    private LocalDateTime mReminderDate;

    @Ignore
    public NoteEntity() { }

    public NoteEntity(long id, Long folderId, LocalDateTime creationDate, LocalDateTime lastEditDate,
                      String text, LocalDateTime reminderDate) {
        mId = id;
        mFolderId = folderId;
        mCreationDate = creationDate;
        mLastEditDate = lastEditDate;
        mText = text;
        mReminderDate = reminderDate;
    }

    @Ignore
    public NoteEntity(LocalDateTime dateTime, String text) {
        mText = text;
        mLastEditDate = dateTime;
    }

    @Ignore
    public NoteEntity(LocalDateTime creationDate, LocalDateTime lastEditDate, String text) {
        mText = text;
        mCreationDate = creationDate;
        mLastEditDate = lastEditDate;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "mId=" + mId +
                ", mFolderId=" + mFolderId +
                ", mCreationDate=" + mCreationDate +
                ", mLastEditDate=" + mLastEditDate +
                ", mText='" + mText + '\'' +
                ", mReminderDate=" + mReminderDate +
                '}';
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public Long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(Long folderId) {
        mFolderId = folderId;
    }

    public LocalDateTime getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        mCreationDate = creationDate;
    }

    public LocalDateTime getLastEditDate() {
        return mLastEditDate;
    }

    public void setLastEditDate(LocalDateTime lastEditDate) {
        mLastEditDate = lastEditDate;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public LocalDateTime getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        mReminderDate = reminderDate;
    }
}
