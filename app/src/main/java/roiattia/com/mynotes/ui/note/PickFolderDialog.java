package roiattia.com.mynotes.ui.note;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import roiattia.com.mynotes.database.folder.FolderEntity;

public class PickFolderDialog extends DialogFragment {

    private static final String TAG = PickFolderDialog.class.getSimpleName();
    private String[] mFoldersNames;
    private List<FolderEntity> mFolders;
    private FoldersDialogListener mListener;

    public interface FoldersDialogListener {
        void onFolderPicked(FolderEntity folder);
        void onRemoveFromFolder();
        void onCreateNewFolder();
    }

    public void setFolders(List<FolderEntity> folders){
        mFolders = folders;
        List<String> foldersNames = new ArrayList<>();
        for(FolderEntity folder : mFolders)
            foldersNames.add(folder.getName());
        mFoldersNames = foldersNames.toArray(new String[foldersNames.size()]);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Pick a folder")
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
                .setNegativeButton("remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onRemoveFromFolder();
                    }
                })
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        return dialog.create();
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
