package roiattia.com.mynotes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.ui.noteslist.NotesListAdapter;
import roiattia.com.mynotes.utils.PreferencesUtil;

public abstract class NotesListBaseActivity extends AppCompatActivity
    implements NotesListAdapter.OnNoteClick {

    @BindView(R.id.rv_notes_list) RecyclerView mNotesRecyclerView;

    public NotesListAdapter mNotesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        setupRecyclerView();
    }

    public void setupRecyclerView() {
        mNotesAdapter = new NotesListAdapter(this, this);
        mNotesAdapter.setSelectedFields(PreferencesUtil.getFields(this));
        mNotesRecyclerView.setAdapter(mNotesAdapter);
        mNotesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(layoutManager);
    }

    protected abstract int getLayoutId();
}
