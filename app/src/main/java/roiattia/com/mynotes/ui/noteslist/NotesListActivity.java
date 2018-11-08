package roiattia.com.mynotes.ui.noteslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.ui.folderslist.FoldersListActivity;
import roiattia.com.mynotes.ui.note.EditNoteActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_NAME_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class NotesListActivity extends AppCompatActivity
    implements NotesListAdapter.OnNoteClick{

    private static final String TAG = NotesListActivity.class.getSimpleName();

    private NotesListAdapter mNotesAdapter;
    private NotesListViewModel mViewModel;
    // notes list that are checked for deletion
    private List<NoteEntity> mNotesForDeletion;
    // true - notes list of a specific folder. false - all notes
    private boolean mIsInsideFolder;
    // folder id for notes retrieval
    private long mFolderId;

    @BindView(R.id.rv_notes_list) RecyclerView mNotesRecyclerView;
    @BindView(R.id.cl_delete) ConstraintLayout mDeleteLayout;
    @BindView(R.id.fab_add_note) FloatingActionButton mAddNoteFab;
    @BindView(R.id.btn_delete) Button mDeleteButton;
    @BindView(R.id.btn_cancel) Button mCancelButton;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);

        mNotesForDeletion = new ArrayList<>();
        mViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);

        getIntentExtras();

        setupRecyclerView();

        setupViewModel();

        setupUI();
    }

    /**
     * Open EditNoteActivity to add a new note
     */
    @OnClick(R.id.fab_add_note)
    public void addNote(){
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
        // if inside a folder then send the folder's id as an extra to set the new note
        // to this folder
        if (mIsInsideFolder){
            intent.putExtra(FOLDER_ID_KEY, mFolderId);
        }
        startActivity(intent);
    }

    /**
     * Handle note click event
     * @param noteId the note's id
     */
    @Override
    public void onNoteClick(long noteId) {
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, noteId);
        startActivity(intent);
    }

    /**
     * Handle note delete checkbox check event
     * @param noteEntity the note item
     * @param addForDeletion the checkbox check status
     */
    @Override
    public void onCheckBoxChecked(NoteEntity noteEntity, boolean addForDeletion) {
        // if note checked then add to the mNotesForDeletion list
        if(addForDeletion){
            mNotesForDeletion.add(noteEntity);
        } else {
            mNotesForDeletion.remove(noteEntity);
        }
        // if mNotesForDeletion list > 0 then enable the delete button
        if(mNotesForDeletion.size() > 0){
            mDeleteButton.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // delete notes button
            case R.id.mi_delete_notes:
                setupLayout(true);
                return true;
            // folder button
            case R.id.mi_folders:
                Intent intent = new Intent(NotesListActivity.this, FoldersListActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check for an intent extra. if there is then this activity was opened by
     * a folder to show it's notes
     */
    private void getIntentExtras() {
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(FOLDER_ID_KEY)){
            mIsInsideFolder = true;
            mFolderId = intent.getLongExtra(FOLDER_ID_KEY, 0);
            // set the title to the folder's name
            setTitle(intent.getStringExtra(FOLDER_NAME_KEY));
        }
    }

    /**
     * Setup UI elements's with click listeners
     */
    private void setupUI() {
        // set delete layout cancel button click event
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLayout(false);
            }
        });
        // set delete layout delete button click event
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete all notes selected
                mViewModel.deleteNotes(mNotesForDeletion);
                mNotesAdapter.notifyDataSetChanged();
                setupLayout(false);
            }
        });
    }

    /**
     * Setup viewModel with observer on the notes list
     */
    private void setupViewModel() {
        Observer<List<NoteEntity>> observer = new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> noteEntities) {
                if(noteEntities != null) {
                    mNotesAdapter.setNotesList(noteEntities);
                    // if the list is empty then show an empty list text box
                    if(noteEntities.size() > 0){
                        mEmptyListMessage.setVisibility(GONE);
                    } else {
                        mEmptyListMessage.setVisibility(VISIBLE);
                    }
                }
            }
        };
        // if inside a folder then load the notes by the folder's id. if not
        // then load all the notes
        if(mIsInsideFolder){
            mViewModel.getNotesByFolderIdLiveData(mFolderId).observe(this, observer);
        } else {
            mViewModel.getNotesLiveData().observe(this, observer);
        }
    }

    /**
     * Setup notes list recyclerView
     */
    private void setupRecyclerView() {
        mNotesAdapter = new NotesListAdapter(this, this);
        mNotesRecyclerView.setAdapter(mNotesAdapter);
        mNotesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Setup delete layout
     * @param showLayout true - show layout. false - hide layout
     */
    private void setupLayout(boolean showLayout) {
        if(showLayout) {
            // check if there are notes in the list. if not then toast a message
            if (mNotesAdapter.getItemCount() > 0) {
                mNotesAdapter.setShowCheckBoxes(true);
                mDeleteLayout.setVisibility(VISIBLE);
                mAddNoteFab.setVisibility(GONE);
                // disable delete button. enable it only if there are notes checked
                // for deletion
                mDeleteButton.setEnabled(false);
            } else {
                Toast.makeText(this, R.string.no_notes_to_delete_message, Toast.LENGTH_SHORT).show();
            }
        } else {
            mDeleteLayout.setVisibility(GONE);
            mAddNoteFab.setVisibility(VISIBLE);
            mNotesAdapter.setShowCheckBoxes(false);
        }
    }
}
