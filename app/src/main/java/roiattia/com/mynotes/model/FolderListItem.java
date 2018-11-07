package roiattia.com.mynotes.model;

public class FolderListItem {

    private String mName;
    private int mId;
    private int mNotesCount;

    public FolderListItem(String name, int id, int notesCount) {
        mName = name;
        mId = id;
        mNotesCount = notesCount;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
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
