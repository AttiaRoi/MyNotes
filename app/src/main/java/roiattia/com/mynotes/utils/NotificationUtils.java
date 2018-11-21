package roiattia.com.mynotes.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.ui.note.EditNoteActivity;
import roiattia.com.mynotes.ui.noteslist.NotesListActivity;

import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_PENDING_INTENT_ID;
import static roiattia.com.mynotes.utils.Constants.NOTE_REMINDER_NOTIFICATION_CHANNEL_ID;
import static roiattia.com.mynotes.utils.Constants.NOTE_REMINDER_NOTIFICATION_ID;

public class NotificationUtils {

    public static void remindUserOfNote(Context context, String noteText, long noteId) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTE_REMINDER_NOTIFICATION_CHANNEL_ID,
                    "channel name",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,NOTE_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(noteText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(noteText))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, noteId))
                .setAutoCancel(true);

        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        if (notificationManager != null) {
            notificationManager.notify(NOTE_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private static PendingIntent contentIntent(Context context, long noteId) {
        Intent startActivityIntent = new Intent(context, EditNoteActivity.class);
        startActivityIntent.putExtra(NOTE_ID_KEY, noteId);
        return PendingIntent.getActivity(
                context,
                NOTE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
