package roiattia.com.mynotes.ui.noteslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.NoteEntity;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private Context mContext;
    private List<NoteEntity> mNotesList;
    private OnNoteClick mClickListener;

    NotesAdapter(Context context, OnNoteClick clickListener) {
        mContext = context;
        mClickListener = clickListener;
    }

    public interface OnNoteClick{
        void onNoteClick(int index);
    }

    public void setData(List<NoteEntity> notesList){
        mNotesList = notesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_note, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.mNoteTextView.setText(mNotesList.get(position).getText());
        holder.mNoteDateView.setText(mNotesList.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return mNotesList == null ? 0 : mNotesList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.tv_note_text) TextView mNoteTextView;
        @BindView(R.id.tv_note_date) TextView mNoteDateView;

        NotesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onNoteClick(mNotesList.get(position).getId());
        }
    }
}
