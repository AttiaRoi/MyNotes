package roiattia.com.mynotes.sync;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.lang.ref.WeakReference;

import static roiattia.com.mynotes.utils.Constants.NOTE_ID_EXTRA;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_TEXT_EXTRA;

public class ReminderFireBaseJobService extends JobService {

    private BackgroundAsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Bundle bundle = job.getExtras();
        String noteText = "";
        long noteId = 0;
        if(bundle != null) {
            noteText = bundle.getString(NOTE_TEXT_EXTRA);
            noteId = bundle.getLong(NOTE_ID_KEY);
        }
        mBackgroundTask = new BackgroundAsyncTask(this, noteText, noteId);
        mBackgroundTask.execute(job);
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

    static class BackgroundAsyncTask extends AsyncTask<JobParameters, Void, JobParameters>{

        private WeakReference<ReminderFireBaseJobService> reference;
        private String mNoteText;
        private long mNoteId;

        BackgroundAsyncTask(ReminderFireBaseJobService context, String noteText, long noteId) {
            reference = new WeakReference<>(context);
            mNoteText = noteText;
            mNoteId = noteId;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... jobParameters) {
            ReminderFireBaseJobService service = reference.get();
            NotificationUtils.remindUserOfNote(service, mNoteText, mNoteId);
            return jobParameters[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            ReminderFireBaseJobService service = reference.get();
            service.jobFinished(jobParameters, false);
        }
    }
}
