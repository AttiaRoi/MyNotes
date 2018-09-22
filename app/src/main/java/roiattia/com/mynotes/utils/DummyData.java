package roiattia.com.mynotes.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import roiattia.com.mynotes.database.NoteEntity;

public class DummyData {

    private static final int mDummiesNumber = 3;
    private static final String[] mTexts = {
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet\nconsectetur adipiscing elit",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                    " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam," +
                    " quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                    " Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat" +
                    " nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia" +
                    " deserunt mollit anim id est laborum."};

    private static Date getDate(int index){
        GregorianCalendar date = new GregorianCalendar();
        date.add(Calendar.HOUR, index);
        return date.getTime();
    }

    public static List<NoteEntity> getDummyData(){
        List<NoteEntity> notesList = new ArrayList<>();
        for(int i = 0; i<mDummiesNumber ; i++){
            notesList.add(new NoteEntity(getDate(i), mTexts[i]));
        }
        return notesList;
    }
}
