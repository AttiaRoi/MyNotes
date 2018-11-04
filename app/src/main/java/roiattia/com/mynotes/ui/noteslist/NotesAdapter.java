package roiattia.com.mynotes.ui.noteslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.utils.TextFormat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private Context mContext;
    private List<NoteEntity> mNotesList;
    private OnNoteClick mClickListener;
    private boolean mShowCheckBoxes;

    NotesAdapter(Context context, OnNoteClick clickListener) {
        mContext = context;
        mClickListener = clickListener;
    }

    public interface OnNoteClick{
        /**
         * Pass the index of the note clicked back to the listener
         * @param index the clicked note index
         */
        void onNoteClick(int index);

        /**
         * Pass the note that was checked and if it was checked or unchecked for
         * deletion purposes
         * @param noteEntity the note item
         * @param addForDeletion the checkbox check status
         */
        void onCheckBoxChecked(NoteEntity noteEntity, boolean addForDeletion);
    }

    /**
     * Set the checkboxes visibility status
     * @param showCheckBoxes true to show, false to hide
     */
    public void setShowCheckBoxes(boolean showCheckBoxes){
        mShowCheckBoxes = showCheckBoxes;
        notifyDataSetChanged();
    }

    /**
     * Set the notes list for the recycler view to show
     * @param notesList items to show in recycler view
     */
    public void setNotesList(List<NoteEntity> notesList){
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
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, int position) {
        final NoteEntity note = mNotesList.get(position);
        holder.mNoteTextView.setText(note.getText());
        holder.mNoteDateView.setText(String.format("Last edited at: %s - %s",
                TextFormat.getDateStringFormat(note.getDate()),
                TextFormat.getTimeStringFormat(note.getTime())));
        // check mShowCheckBoxes, if true then show check boxes and uncheck them
        if(mShowCheckBoxes) {
            holder.mCheckBox.setVisibility(VISIBLE);
            holder.mCheckBox.setChecked(false);
        } else {
            holder.mCheckBox.setVisibility(GONE);
        }
        // set checkbox on check listener
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mClickListener.onCheckBoxChecked(note, true);
                } else {
                    mClickListener.onCheckBoxChecked(note, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotesList == null ? 0 : mNotesList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.tv_note_text) TextView mNoteTextView;
        @BindView(R.id.tv_note_date) TextView mNoteDateView;
        @BindView(R.id.checkBox) CheckBox mCheckBox;

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
