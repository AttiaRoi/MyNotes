package roiattia.com.mynotes.ui.editnote;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.NoteEntity;

import static roiattia.com.mynotes.utils.Constants.EDITING_MODE_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = EditNoteActivity.class.getSimpleName();
    @BindView(R.id.tv_note_text) EditText mNoteText;
    private EditNoteViewModel mViewModel;
    private boolean mIsNewNote, mIsEditing;
    private AlertDialog mDeleteNoteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

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
            mViewModel.loadNote(noteId);
        } else {
            setTitle(getString(R.string.new_note));
            mIsNewNote = true;
        }
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(EditNoteViewModel.class);
        mViewModel. getMutableLiveNote().observe(this, new Observer<NoteEntity>() {
            @Override
            public void onChanged(@Nullable NoteEntity noteEntity) {
                if (noteEntity != null && !mIsEditing) {
                    mNoteText.setText(noteEntity.getText());
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

    private void shareNote() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNoteText.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

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
        if(mNoteText.getText().toString().trim().length() > 0) {
            if(mIsNewNote) {
                Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note edited successfully!", Toast.LENGTH_SHORT).show();
            }
            mViewModel.saveNote(mNoteText.getText().toString());
            finish();
        } else {
            Toast.makeText(this, "Note is empty...", Toast.LENGTH_SHORT).show();
        }
    }
}
