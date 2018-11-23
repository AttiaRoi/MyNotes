package roiattia.com.mynotes.model;

import org.joda.time.LocalDateTime;

public class FolderListItem {

    private String mName;
    private long mId;
    private int mNotesCount;
    private LocalDateTime mLastEditedDate;

    public FolderListItem(String name, long id, int notesCount, LocalDateTime lastEditedDate) {
        mName = name;
        mId = id;
        mNotesCount = notesCount;
        mLastEditedDate = lastEditedDate;
    }

    public LocalDateTime getLastEditedDate() {
        return mLastEditedDate;
    }

    public void setLastEditedDate(LocalDateTime lastEditedDate) {
        mLastEditedDate = lastEditedDate;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getNotesCount() {
        return mNotesCount;
    }

    public void setNotesCount(int notesCount) {
        mNotesCount = notesCount;
    }
}
