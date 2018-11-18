package roiattia.com.mynotes.model;

import org.joda.time.LocalDateTime;

public class NoteItem {

    private int mNoteId;
    private LocalDateTime mCreationDate;
    private LocalDateTime mLastEditDate;
    private LocalDateTime mReminderDate;
    private String mNoteText;
    private String mFolderName;
    private Long mFolderId;

    public NoteItem(int noteId, LocalDateTime creationDate, LocalDateTime lastEditDate,
                    LocalDateTime reminderDate, String noteText, String folderName, Long folderId) {
        mNoteId = noteId;
        mCreationDate = creationDate;
        mLastEditDate = lastEditDate;
        mReminderDate = reminderDate;
        mNoteText = noteText;
        mFolderName = folderName;
        mFolderId = folderId;
    }

    @Override
    public String toString() {
        return "NoteItem{" +
                "mNoteId=" + mNoteId +
                ", mCreationDate=" + mCreationDate +
                ", mLastEditDate=" + mLastEditDate +
                ", mReminderDate=" + mReminderDate +
                ", mNoteText='" + mNoteText + '\'' +
                ", mFolderName='" + mFolderName + '\'' +
                ", mFolderId=" + mFolderId +
                '}';
    }

    public int getNoteId() {
        return mNoteId;
    }

    public void setNoteId(int noteId) {
        mNoteId = noteId;
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

    public LocalDateTime getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        mReminderDate = reminderDate;
    }

    public String getNoteText() {
        return mNoteText;
    }

    public void setNoteText(String noteText) {
        mNoteText = noteText;
    }

    public String getFolderName() {
        return mFolderName;
    }

    public void setFolderName(String folderName) {
        mFolderName = folderName;
    }

    public Long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(Long folderId) {
        mFolderId = folderId;
    }
}
