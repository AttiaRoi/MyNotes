package roiattia.com.mynotes.ui.noteslist;

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

import roiattia.com.mynotes.R;

public class FieldsDialog extends DialogFragment {

    private static final String TAG = SortDialog.class.getSimpleName();
    private String[] mFieldsStrings;
    private FieldsDialogListener mListener;
    private boolean[] mSelectedFieldsBoolean;
    private ArrayList<Integer> mSelectedItems;
    private String mTitle;

    public void setSelectedFieldsBoolean(boolean[] selectedFieldsBoolean) {
        mSelectedFieldsBoolean = selectedFieldsBoolean;
    }

    public interface FieldsDialogListener {
        /**
         * Sent selected fields representing integers back to the activity
         * @param selectedItems the integers array list
         */
        void onDialogFinishClick(ArrayList<Integer> selectedItems);
    }

    /**
     * Set the list of fields shown in the dialog
     * @param optionsList the strings array to show
     */
    public void setFields(String[] optionsList){
        mFieldsStrings = optionsList;
    }

    /**
     * Set the title string for the dialog
     * @param title the string to show in the title
     */
    public void setTitle(String title){
        mTitle = title;
    }

    /**
     * Sets the selected items in an integer array_list from the
     * boolean array
     */
    private void setSelectedItems() {
        for(int i = 0; i< mSelectedFieldsBoolean.length; i++){
            if(mSelectedFieldsBoolean[i]){
                mSelectedItems.add(i);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<>();
        setSelectedItems();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        builder.setCustomTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(mFieldsStrings, mSelectedFieldsBoolean,
                        new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.fields_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogFinishClick(mSelectedItems);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the FieldsDialogListener to send events to the host
            mListener = (FieldsDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement FieldsDialogListener");
        }
    }
}
