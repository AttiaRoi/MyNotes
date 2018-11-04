package roiattia.com.mynotes.ui.folderslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.folder.FolderEntity;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.NotesViewHolder> {

    private Context mContext;
    private List<FolderEntity> mFoldersList;
    private OnFolderClick mClickListener;

    FoldersAdapter(Context context, OnFolderClick clickListener) {
        mContext = context;
        mClickListener = clickListener;
    }

    public interface OnFolderClick{
        /**
         * Pass the index of the note clicked back to the listener
         * @param index the clicked note index
         */
        void onFolderClick(int index);
    }

    /**
     * Set the notes list for the recycler view to show
     * @param foldersList items to show in recycler view
     */
    public void setFoldersList(List<FolderEntity> foldersList){
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
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, int position) {
        final FolderEntity folder = mFoldersList.get(position);
        holder.mName.setText(folder.getName());
        int notesCount = folder.getNotesCount();
        holder.mNotesCount.setText(String.format(Locale.getDefault(),
                "Number of notes: %d", notesCount));
        if(notesCount > 0) {
            holder.mImage.setImageResource(R.mipmap.ic_filled_folder);
        } else {
            holder.mImage.setImageResource(R.mipmap.ic_empty_folder);
        }
    }

    @Override
    public int getItemCount() {
        return mFoldersList == null ? 0 : mFoldersList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.tv_folder_name) TextView mName;
        @BindView(R.id.tv_number_of_notes) TextView mNotesCount;
        @BindView(R.id.iv_folder) ImageView mImage;

        NotesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onFolderClick(mFoldersList.get(position).getId());
        }
    }
}
