package roiattia.com.mynotes.database.note;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

import roiattia.com.mynotes.database.folder.FolderEntity;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "note",
        foreignKeys = @ForeignKey(entity = FolderEntity.class,
        parentColumns = "id",
        childColumns = "folder_id",
        onDelete = CASCADE))
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private LocalDate date;
    private LocalTime time;
    private String text;
    @ColumnInfo(name = "folder_id")
    private Long folderId;

    @Ignore
    public NoteEntity() { }

    @Ignore
    public NoteEntity(LocalDate date, LocalTime time, String text) {
        this.date = date;
        this.time = time;
        this.text = text;
    }

    public NoteEntity(int id, LocalDate date, LocalTime time, String text, Long folderId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.text = text;
        this.folderId = folderId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", folderId=" + folderId +
                '}';
    }
}
