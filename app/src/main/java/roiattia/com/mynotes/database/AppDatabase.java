package roiattia.com.mynotes.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import roiattia.com.mynotes.database.folder.FolderDao;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.note.NoteDao;
import roiattia.com.mynotes.database.note.NoteEntity;

@Database(entities = {NoteEntity.class, FolderEntity.class}, version = 11)
@TypeConverters({DateTimeTypeConverter.class, DateTypeConverter.class, TimeTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "MyNotes.db";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract NoteDao noteDao();

    public abstract FolderDao folderDao();

}
