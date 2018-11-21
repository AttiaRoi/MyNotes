package roiattia.com.mynotes.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.ui.noteslist.NotesListAdapter;
import roiattia.com.mynotes.ui.noteslist.NotesListViewModel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FolderActivity extends AppCompatActivity
    implements NotesListAdapter.OnNoteClick{

    @BindView(R.id.rv_notes_list) RecyclerView mNotesRecyclerView;
    @BindView(R.id.tv_empty_list) TextView mEmptyListMessage;

    private NotesListAdapter mNotesAdapter;
    private NotesListViewModel mViewModel;
    private List<NoteEntity> mNotesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        ButterKnife.bind(this);

        mNotesList = new ArrayList<>();
        mViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        setupRecyclerView();
//        setupViewModel();
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
//        mViewModel.getNotesLiveData().observe(this, observer);

    }

    @Override
    public void onNoteClick(long noteId) {

    }

    @Override
    public void onCheckBoxChecked(NoteEntity noteEntity, boolean addForDeletion) {

    }
}
