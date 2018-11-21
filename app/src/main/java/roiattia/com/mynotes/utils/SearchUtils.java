package roiattia.com.mynotes.utils;

import java.util.ArrayList;
import java.util.List;

import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.model.FolderListItem;

public class SearchUtils {

    private SearchUtils(){ }

    public static List<NoteEntity> findNotes(List<NoteEntity> notes, String query){
        List<NoteEntity> foundNotes = new ArrayList<>();
        for(NoteEntity note : notes){
            if(note.getText().toLowerCase().contains(query.toLowerCase())){
                foundNotes.add(note);
            }
        }
        return foundNotes;
    }

    public static List<FolderListItem> findFolders(List<FolderListItem> folders, String query){
        List<FolderListItem> foundFolders = new ArrayList<>();
        for(FolderListItem folder : folders){
            if(folder.getName().toLowerCase().contains(query.toLowerCase())){
                foundFolders.add(folder);
            }
        }
        return foundFolders;
    }
}
