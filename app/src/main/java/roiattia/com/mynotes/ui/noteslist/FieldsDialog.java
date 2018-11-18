package roiattia.com.mynotes.ui.noteslist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class FieldsDialog extends DialogFragment {

    private static final String TAG = SortDialog.class.getSimpleName();
    private String[] mFieldsStrings;
    private FieldsDialogListener mListener;
    private boolean[] mSelectedFieldsBoolean;
    private ArrayList<Integer> mSelectedItems;

    public void setSelectedFieldsBoolean(boolean[] selectedFieldsBoolean) {
        mSelectedFieldsBoolean = selectedFieldsBoolean;
    }

    public interface FieldsDialogListener {
        void onDialogFinishClick(ArrayList<Integer> selectedItems);
    }

    public void setFields(String[] optionsList){
        mFieldsStrings = optionsList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        mSelectedItems = new ArrayList<>();
        setSelectedItems();
        dialog.setTitle("Chose fields to show")
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
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogFinishClick(mSelectedItems);
                    }
                });

        return dialog.create();
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
