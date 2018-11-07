package roiattia.com.mynotes.model;

public class FolderName {

    private Long mId;
    private String mName;

    public FolderName(Long id, String name) {
        mId = id;
        mName = name;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
