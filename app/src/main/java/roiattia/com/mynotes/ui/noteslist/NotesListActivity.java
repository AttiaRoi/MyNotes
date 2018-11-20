package roiattia.com.mynotes.ui.noteslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.ui.dialogs.CheckBoxesDialog;
import roiattia.com.mynotes.ui.dialogs.DeleteDialog;
import roiattia.com.mynotes.ui.folderslist.FoldersListActivity;
import roiattia.com.mynotes.ui.note.EditNoteActivity;
import roiattia.com.mynotes.utils.PreferencesUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.FOLDER_NAME_KEY;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;
import static roiattia.com.mynotes.utils.Constants.REQ_CODE_SPEECH_INPUT;

public class NotesListActivity extends AppCompatActivity
    implements NotesListAdapter.OnNoteClick, CheckBoxesDialog.CheckBoxesDialogListener,
    SharedPreferences.OnSharedPreferenceChangeListener,
    DeleteDialog.DeleteDialogListener{

    private static final String TAG = NotesListActivity.class.getSimpleName();

    private NotesListAdapter mNotesAdapter;
    private NotesListViewModel mViewModel;
    // the entire notes list
    private List<NoteEntity> mNotesList;
    // notes that meet a search query
    private List<NoteEntity> mSearchedNotes;
    // notes list that are checked for deletion
    private List<NoteEntity> mNotesForDeletion;
    // true - notes list of a specific folder. false - all notes
    private boolean mIsInsideFolder;
    // folder id for notes retrieval
    private long mFolderId;
    private CheckBoxesDialog mFieldsDialog;
    // array representing the fields dialog show options
    private boolean[] mSelectedFields;
    private DeleteDialog mDeleteNotesDialog;

    //    private ListDialog mNoteSortDialog;

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

//        setupAd();

        mNotesList = new ArrayList<>();
        mSelectedFields = new boolean[3];
        mViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);

        handleIntent();

        setupRecyclerView();

        setupViewModel();

        setupUI();

        loadDataFromPrefs();
    }

    /**
     * Handle record note action
     */
    @OnClick(R.id.fab_record_note)
    public void recordNote(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.record_note_message));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Open EditNoteActivity to add a new note
     */
    @OnClick(R.id.fab_add_note)
    public void addNote(){
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
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
        if(mNotesForDeletion == null){
            mNotesForDeletion = new ArrayList<>();
        }
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
        // Get the SearchView and set the searchable configuration
        MenuItem item = menu.findItem(R.id.mi_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchNotes(newText);
                return false;
            }
        });

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
//            case R.id.mi_sort:
//                showSortDialog();
//                return true;
            case R.id.mi_note_list:
                showFieldsDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void showSortDialog() {
//        if(mNoteSortDialog == null){
//            mNoteSortDialog = new ListDialog();
//            mNoteSortDialog.setListItemsStrings(getResources().getStringArray(R.array.fields_selection_options));
//        }
//        mNoteSortDialog.show(getSupportFragmentManager(), "sort_dialog");
//    }

    private void showFieldsDialog() {
        if(mFieldsDialog == null){
            mFieldsDialog = new CheckBoxesDialog();
            mFieldsDialog.setCheckBoxesStrings(getResources().getStringArray(R.array.fields_selection_options));
            mFieldsDialog.setTitle(getString(R.string.fields_dialog_title));
        }
        mFieldsDialog.setSelectedCheckBoxesBooleanArray(mSelectedFields);
        mFieldsDialog.show(getSupportFragmentManager(), "fields_dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mViewModel.insertNoteByRecord(result.get(0));
                }
                break;
            }

        }
    }

    /**
     * Search for notes containing the search query text
     * @param textQuery the text query to search on
     */
    private void searchNotes(String textQuery) {
        if(mSearchedNotes == null){
            mSearchedNotes = new ArrayList<>();
        } else {
            mSearchedNotes.clear();
        }
        for(NoteEntity note : mNotesList){
            if(note.getText().toLowerCase().contains(textQuery.toLowerCase())){
                mSearchedNotes.add(note);
            }
        }
        mNotesAdapter.setNotesList(mSearchedNotes);
    }

    /**
     * Check for an intent extra. if there is then this activity was opened by
     * a folder to show it's notes
     */
    private void handleIntent() {
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(FOLDER_ID_KEY)){
            mIsInsideFolder = true;
            mFolderId = intent.getLongExtra(FOLDER_ID_KEY, 0);
            // set the title to the folder's name
            setTitle(intent.getStringExtra(FOLDER_NAME_KEY));
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
                showDeleteDialog();
            }
        });
    }

    private void showDeleteDialog() {
        if(mDeleteNotesDialog == null){
            mDeleteNotesDialog = new DeleteDialog();
            mDeleteNotesDialog.setTitle("Delete Notes");
        }
        mDeleteNotesDialog.setMessage(mNotesForDeletion.size() + " notes will be deleted");
        mDeleteNotesDialog.show(getSupportFragmentManager(), "delete_notes_dialog");
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
                    mNotesList = noteEntities;
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

    /**
     * Load data from shared preferences
     */
    private void loadDataFromPrefs() {
        mSelectedFields[0] = PreferencesUtil.getShowCreationDate(this);
        mSelectedFields[1] = PreferencesUtil.getShowLastEditDate(this);
        mSelectedFields[2] = PreferencesUtil.getShowReminderDate(this);
        mNotesAdapter.setSelectedFields(mSelectedFields);
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
    public void onConfirmSelection(ArrayList<Integer> selectedItems) {
        PreferencesUtil.setFields(this, selectedItems);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadDataFromPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDeleteConfirmed() {
        mViewModel.deleteNotes(mNotesForDeletion);
        mNotesAdapter.notifyDataSetChanged();
        setupLayout(false);
    }
}
