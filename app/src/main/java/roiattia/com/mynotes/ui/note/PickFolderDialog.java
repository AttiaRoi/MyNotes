package roiattia.com.mynotes.ui.note;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roiattia.com.mynotes.R;
import roiattia.com.mynotes.database.folder.FolderEntity;

public class PickFolderDialog extends DialogFragment {

    private static final String TAG = PickFolderDialog.class.getSimpleName();
    private String[] mFoldersNames;
    private List<FolderEntity> mFolders;
    private FoldersDialogListener mListener;
    private String mTitle;

    public interface FoldersDialogListener {
        void onFolderPicked(FolderEntity folder);
        void removeFromFolder();
        void onCreateNewFolder();
    }

    public void setFolders(List<FolderEntity> folders){
        mFolders = folders;
        List<String> foldersNames = new ArrayList<>();
        for(FolderEntity folder : mFolders)
            foldersNames.add(folder.getName());
        mFoldersNames = foldersNames.toArray(new String[foldersNames.size()]);
    }

    /**
     * Set the dialog's title
     * @param title the title to show
     */
    public void setTitle(String title){
        mTitle = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        builder.setCustomTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setItems(mFoldersNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFolderPicked(mFolders.get(which));
                    }
                })
                .setPositiveButton("new folder", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onCreateNewFolder();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .setNeutralButton("remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.removeFromFolder();
                    }
                });
        if(mFolders.size() == 0){
            builder.setMessage("Click \"New Folder\" to add folders");
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the FoldersDialogListener to send events to the host
            mListener = (FoldersDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement FoldersDialogListener");
        }
    }
}
