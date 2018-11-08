package roiattia.com.mynotes.model;

public class FolderListItem {

    private String mName;
    private long mId;
    private int mNotesCount;

    public FolderListItem(String name, long id, int notesCount) {
        mName = name;
        mId = id;
        mNotesCount = notesCount;
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
