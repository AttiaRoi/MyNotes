package roiattia.com.mynotes.ui.noteslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.NoteEntity;
import roiattia.com.mynotes.ui.editnote.EditNoteActivity;
import roiattia.com.mynotes.utils.DummyData;

import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class NotesListActivity extends AppCompatActivity
    implements NotesAdapter.OnNoteClick{

    private static final String TAG = NotesListActivity.class.getSimpleName();
    @BindView(R.id.rv_notes_list) RecyclerView mNotesRecyclerView;
    private NotesAdapter mNotesAdapter;
    private NotesListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);

        setupRecyclerView();

        setupViewModel();
    }

    private void insertData(List<NoteEntity> notes){
        mViewModel.insertData(notes);
    }

    private void setupViewModel() {
        Observer<List<NoteEntity>> observer = new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> noteEntities) {
                for(NoteEntity noteEntity: noteEntities)
                    Log.i(TAG, noteEntity.toString());
                mNotesAdapter.setData(noteEntities);
            }
        };

        mViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);
        mViewModel.getNotesList().observe(this, observer);
    }

    private void setupRecyclerView() {
        mNotesAdapter = new NotesAdapter(this, this);
        mNotesRecyclerView.setAdapter(mNotesAdapter);
        mNotesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mi_add_note:
                Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
                startActivity(intent);
                return true;
            case R.id.mi_delete_notes:
                Toast.makeText(this, "delete notes", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mi_insert_dummy_data:
                insertData(DummyData.getDummyData());
                return true;
            case R.id.mi_delete_all_notes:
                mViewModel.deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteClick(int noteIndex) {
        Log.i(TAG, "note index: " + noteIndex);
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, noteIndex);
        startActivity(intent);
    }

    //TODO: 2 - delete notes in notes list activity
    //TODO: 3 - share note
}
