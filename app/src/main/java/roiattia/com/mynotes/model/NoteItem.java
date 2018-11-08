package roiattia.com.mynotes.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class NoteItem {

    private int id;
    private LocalDate date;
    private LocalTime time;
    private String text;
    private String folderName;
    private Long folderId;

    public NoteItem(int id, LocalDate date, LocalTime time, String text, String folderName, Long folderId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.text = text;
        this.folderName = folderName;
        this.folderId = folderId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String toString() {
        return "NoteItem{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", folderName='" + folderName + '\'' +
                ", folderId=" + folderId +
                '}';
    }
}
