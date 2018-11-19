package roiattia.com.mynotes.ui.folderslist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import roiattia.com.mynotes.R;

public class DeleteFolderDialog extends DialogFragment {

    private DeleteFolderDialogListener mListener;
    private String mTitle;
    private String mMessage;

    public interface DeleteFolderDialogListener {
        /**
         * Send back to the activity that confirm delete clicked
         */
        void onDeleteFolderConfirmed();
    }

    /**
     * Set the dialog's title
     * @param title the title to show
     */
    public void setTitle(String title){
        mTitle = title;
    }

    /**
     * Set the dialog's message
     * @param message the message to show
     */
    public void setMessage(String message){
        mMessage = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        builder.setCustomTitle(title)
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDeleteFolderConfirmed();
                        dismiss();

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NewFolderDialogListener so we can send events to the host
            mListener = (DeleteFolderDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DeleteFolderDialogListener");
        }
    }
}
