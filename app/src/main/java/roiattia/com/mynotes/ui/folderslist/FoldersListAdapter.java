package roiattia.com.mynotes.ui.folderslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.database.note.NoteEntity;
import roiattia.com.mynotes.model.FolderListItem;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.utils.TextFormat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FoldersListAdapter extends RecyclerView.Adapter<FoldersListAdapter.NotesViewHolder> {

    private Context mContext;
    private List<FolderListItem> mFoldersList;
    private OnFolderClick mClickListener;
    private boolean mShowCheckBoxes;

    public FoldersListAdapter(Context context, OnFolderClick clickListener) {
        mContext = context;
        mClickListener = clickListener;
    }

    public void setShowCheckBoxes(boolean showCheckBoxes) {
        mShowCheckBoxes = showCheckBoxes;
        notifyDataSetChanged();
    }

    public interface OnFolderClick{
        /**
         * Pass the index of the note clicked back to the listener
         * @param index the clicked note index
         */
        void onFolderClick(int index);
        /**
         * Pass the folder that was checked and if it was checked or unchecked for
         * deletion purposes
         * @param folder the folder item
         * @param addForDeletion the checkbox check status
         */
        void onCheckBoxChecked(FolderListItem folder, boolean addForDeletion);
    }

    /**
     * Set the notes list for the recycler view to show
     * @param foldersList items to show in recycler view
     */
    public void setFoldersList(List<FolderListItem> foldersList){
        mFoldersList = foldersList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_folder, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, final int position) {
        final FolderListItem folder = mFoldersList.get(position);
        holder.itemView.setTag((int)folder.getId());
        holder.mName.setText(folder.getName());
        int notesCount = folder.getNotesCount();
        if(notesCount > 0) {
            holder.mNotesCount.setVisibility(View.VISIBLE);
            holder.mNotesCount.setText(TextFormat.getStringFormatFromInt(notesCount));
            holder.mFolderImage.setImageResource(R.mipmap.ic_filled_folder);
        } else {
            holder.mNotesCount.setVisibility(View.GONE);
            holder.mFolderImage.setImageResource(R.mipmap.ic_empty_folder);
        }
        holder.mLastEdited.setText(String.format("Last edited: %s",
                TextFormat.getDateTimeStringFormat(folder.getLastEditedDate())));
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
                    mClickListener.onCheckBoxChecked(folder, true);
                } else {
                    mClickListener.onCheckBoxChecked(folder, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFoldersList == null ? 0 : mFoldersList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.tv_folder_name) TextView mName;
        @BindView(R.id.tv_number_of_notes) TextView mNotesCount;
        @BindView(R.id.tv_folder_last_edited) TextView mLastEdited;
        @BindView(R.id.iv_folder) ImageView mFolderImage;
        @BindView(R.id.checkBox) CheckBox mCheckBox;

        NotesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onFolderClick(position);
        }
    }
}
