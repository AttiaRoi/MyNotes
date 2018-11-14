package roiattia.com.mynotes.ui.note;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.model.NoteItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.database.repositories.FoldersRepository;
import roiattia.com.mynotes.sync.NoteReminder;
import roiattia.com.mynotes.ui.dialogs.NewFolderDialog;
import roiattia.com.mynotes.ui.folderslist.FoldersListActivity;
import roiattia.com.mynotes.ui.noteslist.NotesListActivity;

import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_NAME_KEY;
import static roiattia.com.mynotes.utils.Constants.INSIDE_FOLDER;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class EditNoteActivity extends AppCompatActivity
    implements PickFolderDialog.FoldersDialogListener,
        NewFolderDialog.NewFolderDialogListener, FoldersRepository.FoldersRepositoryListener{

    private static final String TAG = EditNoteActivity.class.getSimpleName();

    private EditNoteViewModel mViewModel;
    private boolean mIsNewNote, mIsInsideFolder;
    private List<FolderEntity> mFoldersList;
    private NoteEntity mNote;
    private LocalDateTime mReminderDateTime;
    private Calendar mCalendar;
    // Dialogs
    private AlertDialog mDeleteNoteDialog;
    private PickFolderDialog mAddToFolderDialog;
    private NewFolderDialog mAddNewFolderDialog;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    @BindView(R.id.tv_note_text) EditText mNoteText;
    @BindView(R.id.tv_folder_text) TextView mFolderText;
    @BindView(R.id.iv_folder) ImageView mFolderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        setTitle(getString(R.string.new_note));

        mNote = new NoteEntity();
        mReminderDateTime = new LocalDateTime();
        mCalendar = Calendar.getInstance();
        mIsNewNote = true;

        setupViewModel();

        handleIntent();
    }

    @OnClick(R.id.btn_set_reminder)
    public void setNoteReminder(){
        if(mDatePickerDialog == null || mTimePickerDialog == null) {
            setupPickers();
        }
        mDatePickerDialog.show();
    }

    private void setupPickers() {
        mDatePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dateString = year + "-" + (month+1) + "-" + dayOfMonth;
                        mReminderDateTime = LocalDateTime.parse(dateString);
                        mTimePickerDialog.show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));

        mTimePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mReminderDateTime = mReminderDateTime.withHourOfDay(hourOfDay);
                        mReminderDateTime = mReminderDateTime.withMinuteOfHour(minute);
                        Log.i("kinga", mReminderDateTime.toString());
                        NoteReminder.scheduleSalariesReminder(EditNoteActivity.this,
                                mReminderDateTime, mNote.getText());
                    }
                }, mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE), true);
    }

    /**
     * Add note to folder click event. open folders dialog
     */
    @OnClick(R.id.cv_add_to_folder)
    public void addNoteToFolder(){
        if(mAddToFolderDialog == null){
            mAddToFolderDialog = new PickFolderDialog();
        }
        mAddToFolderDialog.setFolders(mFoldersList);
        mAddToFolderDialog.show(getSupportFragmentManager(), "folders_dialog");
    }

    /**
     * Handle folder picked from the dialog action
     * @param folder the folder that was picked
     */
    @Override
    public void onFolderPicked(FolderEntity folder) {
        mNote.setFolderId(folder.getId());
        setFolderText(folder.getName());
    }

    /**
     * Handle remove note from folder dialog action
     */
    @Override
    public void onRemoveFromFolder() {
        mNote.setFolderId(null);
        setNoFolder();
    }

    /**
     * Handle create new folder dialog action
     */
    @Override
    public void onCreateNewFolder() {
        if(mAddNewFolderDialog == null){
            mAddNewFolderDialog = new NewFolderDialog();
        }
        mAddNewFolderDialog.show(getSupportFragmentManager(), "new_folder");
    }

    /**
     * Handle new folder confirmed dialog action
     * @param input the new folder's name
     */
    @Override
    public void onFolderConfirmed(String input) {
        if(input.trim().length() > 0){
            mViewModel.inertNewFolder(input, EditNoteActivity.this);
        } else {
            Toast.makeText(this, R.string.folder_name_required_toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle the new folder inserted to db listener code
     * @param folder the new folder that was inserted to db
     */
    @Override
    public void onFolderInserted(final FolderEntity folder) {
        mNote.setFolderId(folder.getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFolderText(folder.getName());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        if(mIsNewNote) {
            MenuItem menuItem = menu.findItem(R.id.mi_delete_note);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.mi_share_note);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mi_confirm_note:
                saveNote();
                return true;
            case R.id.mi_delete_note:
                showAlertDialog();
                return true;
            case R.id.mi_share_note:
                shareNote();
                return true;
            case android.R.id.home:
                if (mIsInsideFolder) {
                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check for intent extras. If it is a new note or an existing
     * note and set title accordingly
     */
    private void handleIntent() {
        Intent intent = getIntent();
        if(intent != null){
            // check for note id extra
            if(intent.hasExtra(NOTE_ID_KEY)) {
                setTitle(getString(R.string.edit_note));
                long noteId = intent.getLongExtra(NOTE_ID_KEY, 0);
                mViewModel.loadNote(noteId);
                mIsNewNote = false;
            }
            // check for folder indicator extra
            if(intent.hasExtra(INSIDE_FOLDER)){
                mIsInsideFolder = true;
            }
        }
    }

    /**
     * Setup viewModel with observers and load note and folders data
     */
    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(EditNoteViewModel.class);
        // set observer on note
        mViewModel.getMutableLiveNote().observe(this, new Observer<NoteItem>() {
            @Override
            public void onChanged(@Nullable NoteItem noteItem) {
                if (noteItem != null) {
                    // check if the note is attached to a folder by the folder's id
                    if(noteItem.getFolderId() == null){
                        mNote.setId(noteItem.getId());
                        mFolderImage.setImageResource(R.mipmap.ic_no_folder);
                    } else {
                        setFolderText(noteItem.getFolderName());
                        mNote.setFolderId(noteItem.getFolderId());
                        mNote.setId(noteItem.getId());
                        mFolderImage.setImageResource(R.mipmap.ic_filled_folder);
                    }
                    mNote.setText(noteItem.getText());
                    mNoteText.setText(noteItem.getText());
                }
            }
        });
        // Load folders
        mViewModel.getFoldersLiveData().observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> folders) {
                if(folders != null){
                    mFoldersList = folders;
                }
            }
        });
        // Set observer for folder data loaded
        mViewModel.getMutableLiveFolder().observe(this, new Observer<FolderEntity>() {
            @Override
            public void onChanged(@Nullable FolderEntity folderEntity) {
                if(folderEntity != null) {
                    setFolderText(folderEntity.getName());
                    mNote.setFolderId(folderEntity.getId());
                }
            }
        });
    }

    /**
     * Show alert dialog for Delete note action
     */
    private void showAlertDialog() {
        if(mDeleteNoteDialog == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // set title
            alertDialogBuilder.setTitle(R.string.delete_note_dialog_title);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteNote();
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            mDeleteNoteDialog = alertDialogBuilder.create();
        }
        mDeleteNoteDialog.show();
    }

    /**
     * Share note action
     */
    private void shareNote() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNoteText.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * Delete note
     */
    private void deleteNote() {
        Toast.makeText(this, R.string.note_deleted_toast_message, Toast.LENGTH_SHORT).show();
        mViewModel.deleteNote();
        finish();
    }

    /**
     * Handle confirm note action
     */
    private void saveNote() {
        // Check if there is an input text to save
        if(mNoteText.getText().toString().trim().length() > 0) {
            if(mIsNewNote) {
                Toast.makeText(this, R.string.note_saved_toast_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.note_edited_toast_message, Toast.LENGTH_SHORT).show();
            }
            mNote.setText(mNoteText.getText().toString().trim());
            mNote.setDate(new LocalDate());
            mNote.setTime(new LocalTime());
            mViewModel.saveNote(mNote);
            finish();
        }
        // if note, show a toast message
        else {
            Toast.makeText(this, R.string.note_is_empty_toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the folder cardView on with the folder's name
     * @param folderName the folder's name
     */
    private void setFolderText(String folderName) {
        mFolderText.setText(folderName);
        mFolderText.setTextColor(Color.BLACK);
        mFolderText.setTypeface(null, Typeface.NORMAL);
        mFolderImage.setImageResource(R.mipmap.ic_filled_folder);
    }

    /**
     * Sets the folder cardView off
     */
    private void setNoFolder() {
        mFolderText.setText(R.string.add_note_to_folder_text);
        mFolderText.setTextColor(Color.GRAY);
        mFolderText.setTypeface(null, Typeface.ITALIC);
        mFolderImage.setImageResource(R.mipmap.ic_no_folder);
    }
}
