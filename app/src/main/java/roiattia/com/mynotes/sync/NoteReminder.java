package roiattia.com.mynotes.sync;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import java.util.concurrent.TimeUnit;

import roiattia.com.mynotes.database.note.NoteEntity;

import static com.firebase.jobdispatcher.Lifetime.FOREVER;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_TEXT_EXTRA;
import static roiattia.com.mynotes.utils.Constants.REMINDER_JOB_TAG;

public class NoteReminder {

    private static final int SYNC_FLEXTIME_SECONDS = (int) TimeUnit.SECONDS.toSeconds(30);

    synchronized public static void scheduleSalariesReminder(
            Context context, NoteEntity note){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Bundle bundle = new Bundle();
        bundle.putString(NOTE_TEXT_EXTRA, note.getText());
        bundle.putLong(NOTE_ID_KEY, note.getId());
        Job constraintsReminderJob = dispatcher.newJobBuilder()
                .setExtras(bundle)
                .setService(ReminderFireBaseJobService.class)
                .setTag(String.valueOf(note.getId()))
                .setTrigger(Trigger.executionWindow(
                        getReminderTime(note.getReminderDate()),
                        getReminderTime(note.getReminderDate()) + SYNC_FLEXTIME_SECONDS))
                .build();

        dispatcher.schedule(constraintsReminderJob);
    }

    public static void cancelReminder(Context context, long noteId){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(String.valueOf(noteId));
    }

    private static int getReminderTime(LocalDateTime localDateTime) {
        LocalDateTime now = new LocalDateTime();
        return (int) TimeUnit.MINUTES.toSeconds(Minutes.minutesBetween(now, localDateTime).getMinutes());
    }
}
