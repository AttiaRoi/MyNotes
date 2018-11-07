package roiattia.com.mynotes.ui.editnote;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.model.NoteItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.folder.FolderEntity;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.database.repositories.FoldersRepository;
import roiattia.com.mynotes.ui.dialogs.EditTextDialog;

import static roiattia.com.mynotes.utils.Constants.EDITING_MODE_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class EditNoteActivity extends AppCompatActivity
    implements FoldersDialog.FoldersDialogCallback,
    EditTextDialog.EditTextDialogListener, FoldersRepository.FoldersRepositoryListener{

    private static final String TAG = EditNoteActivity.class.getSimpleName();

    private EditNoteViewModel mViewModel;
    private boolean mIsNewNote, mIsEditing;
    private List<FolderEntity> mFoldersList;
    private AlertDialog mDeleteNoteDialog;
    private FoldersDialog mAddToFolderDialog;
    private EditTextDialog mAddNewFolderDialog;
    private NoteEntity mNote;

    @BindView(R.id.tv_note_text) EditText mNoteText;
    @BindView(R.id.tv_folder_text) TextView mFolderText;
    @BindView(R.id.iv_folder) ImageView mFolderImage;

    @OnClick(R.id.cv_add_to_folder)
    public void addNoteToFolder(){
        if(mAddToFolderDialog == null){
            mAddToFolderDialog = new FoldersDialog();
        }
        mAddToFolderDialog.setFolders(mFoldersList);
        mAddToFolderDialog.show(getSupportFragmentManager(), "add to folder");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        mNote = new NoteEntity();

        if(savedInstanceState != null){
            mIsEditing = savedInstanceState.getBoolean(EDITING_MODE_KEY);
        }

        setupViewModel();

        checkIntentForExtra();
    }

    private void checkIntentForExtra() {
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(NOTE_ID_KEY)){
            setTitle(getString(R.string.edit_note));
            int noteId = intent.getIntExtra(NOTE_ID_KEY, 0);
            long folderId = intent.getLongExtra(FOLDER_ID_KEY, -1);
            if(folderId == -1){
                mViewModel.loadNote(noteId);
            } else {
                mViewModel.loadNote(noteId, folderId);
            }
        } else {
            setTitle(getString(R.string.new_note));
            mIsNewNote = true;
        }
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(EditNoteViewModel.class);
        // Load note item - in case there is a folder attached
        mViewModel.getMutableLiveNote().observe(this, new Observer<NoteItem>() {
            @Override
            public void onChanged(@Nullable NoteItem noteItem) {
                if (noteItem != null && !mIsEditing) {
                    mNoteText.setText(noteItem.getText());
                    setFolderText(noteItem.getFolderName());
                    mNote.setFolderId(noteItem.getFolderId());
                    mNote.setId(noteItem.getId());
                }
            }
        });
        // Load folders names
        mViewModel.getFoldersLiveData().observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> folders) {
                if(folders != null){
                    mFoldersList = folders;
                }
            }
        });
        // Load note entity - if there is no folder attached
        mViewModel.getMutableLiveNoteEntity().observe(this, new Observer<NoteEntity>() {
            @Override
            public void onChanged(@Nullable NoteEntity noteEntity) {
                if (noteEntity != null && !mIsEditing) {
                    mNoteText.setText(noteEntity.getText());
                    mNote.setId(noteEntity.getId());
                    mFolderImage.setImageResource(R.mipmap.ic_no_folder);
                }
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show alert dialog for Delete note action
     */
    private void showAlertDialog() {
        if(mDeleteNoteDialog == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // set title
            alertDialogBuilder.setTitle("Delete Note?");
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
        Toast.makeText(this, "Note deleted successfully!", Toast.LENGTH_SHORT).show();
        mViewModel.deleteNote();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_MODE_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveNote() {
        // Check if there is an input text to save
        if(mNoteText.getText().toString().trim().length() > 0) {
            if(mIsNewNote) {
                Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note edited successfully!", Toast.LENGTH_SHORT).show();
            }
            mNote.setText(mNoteText.getText().toString().trim());
            mNote.setDate(new LocalDate());
            mNote.setTime(new LocalTime());
            mViewModel.saveNote(mNote);
            finish();
        }
        // if note, show a toast message
        else {
            Toast.makeText(this, "Note is empty...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFolderPicked(FolderEntity folder) {
        mNote.setFolderId(folder.getId());
        setFolderText(folder.getName());
    }

    @Override
    public void onRemoveFromFolder() {
        mNote.setFolderId(null);
        setNoFolder();
    }

    @Override
    public void onCreateNewFolder() {
        if(mAddNewFolderDialog == null){
            mAddNewFolderDialog = new EditTextDialog();
        }
        mAddNewFolderDialog.show(getSupportFragmentManager(), "new folder");
    }

    @Override
    public void onDialogFinishClick(String input) {
        if(input.trim().length() > 0){
            mViewModel.inertNewFolder(input, EditNoteActivity.this);
        } else {
            Toast.makeText(this, "Folder name required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFolderInserted(long id) {
        Log.i(TAG, "onFolderInserted id: " + id);
        mNote.setFolderId(id);
    }

    private void setFolderText(String folderName) {
        mFolderText.setText(folderName);
        mFolderText.setTextColor(Color.BLACK);
        mFolderText.setTypeface(null, Typeface.NORMAL);
        mFolderImage.setImageResource(R.mipmap.ic_filled_folder);
    }

    private void setNoFolder() {
        mFolderText.setText("Add note to folder");
        mFolderText.setTextColor(Color.GRAY);
        mFolderText.setTypeface(null, Typeface.ITALIC);
        mFolderImage.setImageResource(R.mipmap.ic_no_folder);
    }

}
