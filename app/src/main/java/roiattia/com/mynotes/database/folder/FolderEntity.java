package roiattia.com.mynotes.database.folder;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.joda.time.LocalDate;

@Entity(tableName = "folder")
public class FolderEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private LocalDate date;
    private String name;
    private int notesCount;

    public FolderEntity(long id, LocalDate date, String name, int notesCount) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.notesCount = notesCount;
    }

    @Ignore
    public FolderEntity(LocalDate date, String name, int notesCount) {
        this.date = date;
        this.name = name;
        this.notesCount = notesCount;
    }

    public int getNotesCount() {
        return notesCount;
    }

    public void setNotesCount(int notesCount) {
        this.notesCount = notesCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
