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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
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
import roiattia.com.mynotes.reminder.NoteReminder;
import roiattia.com.mynotes.ui.dialogs.DeleteDialog;
import roiattia.com.mynotes.ui.dialogs.ListDialog;
import roiattia.com.mynotes.ui.dialogs.TextInputDialog;
import roiattia.com.mynotes.utils.TextFormat;

import static roiattia.com.mynotes.utils.Constants.FOLDER;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.REMINDER;

public class EditNoteActivity extends AppCompatActivity
    implements ListDialog.ListDialogListener,
        TextInputDialog.TextInputDialogListener, FoldersRepository.FoldersRepositoryListener,
        DeleteDialog.DeleteDialogListener {

    private static final String TAG = EditNoteActivity.class.getSimpleName();

    private EditNoteViewModel mViewModel;
    private boolean mIsNewNote, mIsInsideFolder;
    private List<FolderEntity> mFoldersList;
    private NoteEntity mNote;
    // save the date and time of reminder
    private LocalDateTime mReminderDateTime;
    private Calendar mCalendar;
    // Dialogs
    private DeleteDialog mDeleteNoteDialog;
    private ListDialog mAddToFolderDialog;
    private TextInputDialog mAddNewFolderDialog;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    @BindView(R.id.tv_note_text) EditText mNoteText;
    @BindView(R.id.tv_creation_date) TextView mCreatedText;
    @BindView(R.id.tv_edit_date) TextView mEditedText;
    @BindView(R.id.tv_folder_text) TextView mFolderText;
    @BindView(R.id.tv_reminder) TextView mReminderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

//        setupAd();

        mNote = new NoteEntity();
        mReminderDateTime = new LocalDateTime();
        mCalendar = Calendar.getInstance();

        setupViewModel();

        handleIntent();
    }

    @OnClick(R.id.cv_set_reminder)
    public void setNoteReminder(){
        if(mDatePickerDialog == null || mTimePickerDialog == null) {
            setupPickers();
        }
        mDatePickerDialog.show();
    }



    public void cancelReminder(){
        if(mNote.getReminderDate() != null){
            NoteReminder.cancelReminder(this, mNote.getId());
            mNote.setReminderDate(null);
            setCardViewPanelOff(REMINDER);
            Toast.makeText(this, R.string.reminder_canceled_toast, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.reminder_not_set_toast, Toast.LENGTH_SHORT).show();
        }
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
        mDatePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.date_picker_dialog_remove),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelReminder();
            }
        });

        mTimePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mReminderDateTime = mReminderDateTime.withHourOfDay(hourOfDay);
                        mReminderDateTime = mReminderDateTime.withMinuteOfHour(minute);
                        mNote.setReminderDate(mReminderDateTime);
                        setCardViewPanelOn(REMINDER, TextFormat.getDateTimeStringFormat(mReminderDateTime));
                        NoteReminder.scheduleSalariesReminder(EditNoteActivity.this, mNote);
                    }
                }, mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE), true);
    }

    /**
     * Add note to folder click event. open folders dialog
     */
    @OnClick(R.id.cv_add_to_folder)
    public void addNoteToFolder(){
        if(mAddToFolderDialog == null){
            mAddToFolderDialog = new ListDialog();
            mAddToFolderDialog.setTitle(getString(R.string.add_note_to_folder_dialog_title));
            mAddToFolderDialog.setButtons("new folder", "cancel", "remove");
        }
        if(mFoldersList.size() > 0) {
            List<String> foldersNames = new ArrayList<>();
            for (FolderEntity folder : mFoldersList)
                foldersNames.add(folder.getName());
            mAddToFolderDialog.setListItemsStrings(foldersNames.toArray(new String[foldersNames.size()]));
        } else {
            mAddToFolderDialog.setMessage("Click \"new folder\" to add new folder");
        }
        mAddToFolderDialog.show(getSupportFragmentManager(), "folders_dialog");
    }



    /**
     * Handle new folder confirmed dialog action
     * @param input the new folder's name
     */
    @Override
    public void onInputConfirmed(String input) {
        if(input.trim().length() > 0){
            mViewModel.inertNewFolder(input, EditNoteActivity.this);
        } else {
            Toast.makeText(this, R.string.folder_name_required_toast_message, Toast.LENGTH_SHORT)
                    .show();
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
                setCardViewPanelOn(FOLDER, folder.getName());
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
            } else {
                setupNewNote();
            }
        }
    }

    private void setupNewNote() {
        mIsNewNote = true;
        setTitle(getString(R.string.new_note));
        mNote.setCreationDate(new LocalDateTime());
        mNote.setLastEditDate(new LocalDateTime());
        mCreatedText.setText(String.format("%s %s",
                getString(R.string.created),
                TextFormat.getDateTimeStringFormat(mNote.getCreationDate())));
        mEditedText.setText(String.format("%s %s",
                getString(R.string.edited),
                TextFormat.getDateTimeStringFormat(mNote.getLastEditDate())));
        setCardViewPanelOff(FOLDER);
        setCardViewPanelOff(REMINDER);
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
                        setCardViewPanelOff(FOLDER);
                    } else {
                        mIsInsideFolder = true;
                        mViewModel.loadFolder(noteItem.getFolderId());
                        setCardViewPanelOn(FOLDER, noteItem.getFolderName());
                    }
                    mNoteText.setText(noteItem.getNoteText());
                    loadDataToNote(noteItem);
                }
            }
        });
        // Load folders
        mViewModel.getFoldersLiveData().observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> folders) {
                if(folders != null){
                    Log.i(TAG, "onChanged");
                    mFoldersList = folders;
                }
            }
        });
        // Set observer for folder data loaded
        mViewModel.getMutableLiveFolder().observe(this, new Observer<FolderEntity>() {
            @Override
            public void onChanged(@Nullable FolderEntity folderEntity) {
                if(folderEntity != null) {
                    setCardViewPanelOn(FOLDER, folderEntity.getName());
                    mNote.setFolderId(folderEntity.getId());
                }
            }
        });
    }

    private void loadDataToNote(NoteItem noteItem) {
        mNote.setId(noteItem.getNoteId());
        mNote.setFolderId(noteItem.getFolderId());
        mNote.setText(noteItem.getNoteText());
        mNote.setCreationDate(noteItem.getCreationDate());
        mNote.setLastEditDate(noteItem.getLastEditDate());
        mNote.setReminderDate(noteItem.getReminderDate());
        mCreatedText.setText(String.format("%s %s",
                getString(R.string.created),
                TextFormat.getDateTimeStringFormat(mNote.getCreationDate())));
        mEditedText.setText(String.format("%s %s",
                getString(R.string.edited),
                TextFormat.getDateTimeStringFormat(mNote.getLastEditDate())));
        // check for reminder date
        if(mNote.getReminderDate() != null){
            LocalDateTime localDateTime = new LocalDateTime();
            // check if reminder date has passed and if so then set to null and
            // save it to db. else show it in card view
            if(mNote.getReminderDate().isBefore(localDateTime)){
                mNote.setReminderDate(null);
                setCardViewPanelOff(REMINDER);
                mViewModel.saveNote(mNote);
            } else{
                setCardViewPanelOn(REMINDER,
                        TextFormat.getDateTimeStringFormat(mNote.getReminderDate()));
            }
        } else {
            setCardViewPanelOff(REMINDER);
        }
    }

    /**
     * Show alert dialog for Delete note action
     */
    private void showAlertDialog() {
        if(mDeleteNoteDialog == null) {
            mDeleteNoteDialog = new DeleteDialog();
            mDeleteNoteDialog.setTitle("Delete Note");
        }
        mDeleteNoteDialog.show(getSupportFragmentManager(), "delete_note_dialog");
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
                mNote.setCreationDate(new LocalDateTime());
                mNote.setLastEditDate(new LocalDateTime());
                Toast.makeText(this, R.string.note_saved_toast_message, Toast.LENGTH_SHORT).show();
            } else {
                mNote.setLastEditDate(new LocalDateTime());
                Toast.makeText(this, R.string.note_edited_toast_message, Toast.LENGTH_SHORT).show();
            }
            mNote.setText(mNoteText.getText().toString().trim());
            mViewModel.saveNote(mNote);
            finish();
        }
        // if note text is empty, show a toast message
        else {
            Toast.makeText(this, R.string.note_is_empty_toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the folder cardView on with the folder's name
     * @param text the folder's name
     */
    private void setCardViewPanelOn(String type, String text) {
        if(type.equals(FOLDER)) {
            mFolderText.setText(text);
            mFolderText.setTextColor(Color.BLACK);
            mFolderText.setTypeface(null, Typeface.NORMAL);
            mFolderText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_folder_orange_24dp,
                    0, 0, 0);
        } else if(type.equals(REMINDER)){
            mReminderText.setText(text);
            mReminderText.setTextColor(Color.BLACK);
            mReminderText.setTypeface(null, Typeface.NORMAL);
            mReminderText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_alarm_orange_24dp,
                    0, 0, 0);
        }
    }

    /**
     * Sets the folder cardView off
     */
    private void setCardViewPanelOff(String type) {
        if(type.equals(FOLDER)) {
            mFolderText.setText(R.string.add_note_to_folder_text);
            mFolderText.setTextColor(Color.GRAY);
            mFolderText.setTypeface(null, Typeface.ITALIC);
            mFolderText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_folder_gray_24dp,
                    0, 0, 0);
        } else if(type.equals(REMINDER)){
            mReminderText.setText(R.string.add_reminder_text);
            mReminderText.setTextColor(Color.GRAY);
            mReminderText.setTypeface(null, Typeface.ITALIC);
            mReminderText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_alarm_grey_24dp,
                    0, 0, 0);
        }
    }

    /**
     * Load ad
     */
    private void setupAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onDeleteConfirmed() {
        mViewModel.deleteNote();
        if(mNote.getReminderDate() != null){
            NoteReminder.cancelReminder(this, mNote.getId());
        }
        finish();
    }


    /**
     * Handle folder picked from the dialog action
     * @param whichSelected the index of the selected folder
     */
    @Override
    public void onItemSelected(int whichSelected) {
        FolderEntity folder = mFoldersList.get(whichSelected);
        // update FolderEntity with last note inserted date
        folder.setLastNoteInsertedDate(new LocalDateTime());
        // update folder in db
        mViewModel.saveFolder(folder);
        mNote.setFolderId(folder.getId());
        setCardViewPanelOn(FOLDER, folder.getName());
    }

    /**
     * Handle create new folder dialog action
     */
    @Override
    public void onPositiveSelected() {
        if(mAddNewFolderDialog == null){
            mAddNewFolderDialog = new TextInputDialog();
            mAddNewFolderDialog.setTitle(getString(R.string.create_new_folder_dialog_title));
        }
        mAddNewFolderDialog.show(getSupportFragmentManager(), "new_folder");
    }

    /**
     * Handle remove note from folder dialog action
     */
    @Override
    public void onNeutralSelected() {
        if(mNote.getFolderId() != null) {
            mNote.setFolderId(null);
            setCardViewPanelOff(FOLDER);
            Toast.makeText(this, R.string.note_removed_from_folder_toast,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.note_not_in_folder_toast,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle cancel dialog action
     */
    @Override
    public void onNegativeSelected() { }
}
