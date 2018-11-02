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

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.NoteEntity;
import roiattia.com.mynotes.ui.editnote.EditNoteActivity;
import roiattia.com.mynotes.utils.TextFormat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static roiattia.com.mynotes.utils.Constants.NOTE_ID_KEY;

public class NotesListActivity extends AppCompatActivity
    implements NotesAdapter.OnNoteClick{

    private static final String TAG = NotesListActivity.class.getSimpleName();

    private NotesAdapter mNotesAdapter;
    private NotesListViewModel mViewModel;
    private List<NoteEntity> mNotesForDeletion;

    @BindView(R.id.rv_notes_list) RecyclerView mNotesRecyclerView;
    @BindView(R.id.cl_delete) ConstraintLayout mDeleteLayout;
    @BindView(R.id.fab_add_note) FloatingActionButton mAddNoteFab;
    @BindView(R.id.btn_delete) Button mDeleteButton;
    @BindView(R.id.btn_cancel) Button mCancelButton;

    @OnClick(R.id.fab_add_note)
    public void addNote(){
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);

        mNotesForDeletion = new ArrayList<>();

        setupRecyclerView();

        setupViewModel();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteLayout.setVisibility(GONE);
                mAddNoteFab.setVisibility(VISIBLE);
                mNotesAdapter.setShowCheckBoxes(false);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteNotes(mNotesForDeletion);
                mNotesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupViewModel() {
        Observer<List<NoteEntity>> observer = new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> noteEntities) {
                if(noteEntities != null) {
                    mNotesAdapter.setNotesList(noteEntities);
                }
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
            case R.id.mi_delete_notes:
                setupLayout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupLayout() {
        mNotesAdapter.setShowCheckBoxes(true);
        mDeleteLayout.setVisibility(VISIBLE);
        mAddNoteFab.setVisibility(GONE);
        mDeleteButton.setEnabled(false);
    }

    @Override
    public void onNoteClick(int noteIndex) {
        Intent intent = new Intent(NotesListActivity.this, EditNoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, noteIndex);
        startActivity(intent);
    }

    @Override
    public void onCheckBoxChecked(NoteEntity noteEntity, boolean addForDeletion) {
        if(addForDeletion){
            mNotesForDeletion.add(noteEntity);
        } else {
            mNotesForDeletion.remove(noteEntity);
        }
        if(mNotesForDeletion.size() > 0){
            mDeleteButton.setEnabled(true);
        }
    }
}
