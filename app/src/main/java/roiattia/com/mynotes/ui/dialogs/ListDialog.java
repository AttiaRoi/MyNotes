package roiattia.com.mynotes.ui.dialogs;

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

import roiattia.com.mynotes.R;

public class ListDialog extends DialogFragment {

    private static final String TAG = ListDialog.class.getSimpleName();
    private String[] mListItemsStrings;
    private ListDialogListener mListener;
    private String mTitle;

    public interface ListDialogListener {
        /**
         * Send selected item position back to the activity
         * @param whichSelected the selected item position
         */
        void onItemSelected(int whichSelected);
    }

    /**
     * Set the list items strings
     * @param itemsStrings list items strings
     */
    public void setListItemsStrings(String[] itemsStrings){
        mListItemsStrings = itemsStrings;
    }

    /**
     * Set the title string for the dialog
     * @param title the string to show in the title
     */
    public void setTitle(String title){
        mTitle = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_sort_note_by_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        dialog.setCustomTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(mListItemsStrings, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onItemSelected(which);
                        dismiss();
                    }
                });

        return dialog.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the CheckBoxesDialogListener to send events to the host
            mListener = (ListDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ListDialogListener");
        }
    }
}
