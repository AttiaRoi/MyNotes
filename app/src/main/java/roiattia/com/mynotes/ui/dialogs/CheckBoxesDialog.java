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

import java.util.ArrayList;

import roiattia.com.mynotes.R;

public class CheckBoxesDialog extends DialogFragment {

    private static final String TAG = ListDialog.class.getSimpleName();
    private String[] mCheckBoxesStrings;
    private CheckBoxesDialogListener mListener;
    private boolean[] mSelectedCheckBoxesBooleanArray;
    private ArrayList<Integer> mSelectedCheckBoxesList;
    private String mTitle;

    public interface CheckBoxesDialogListener {
        /**
         * Sent selected fields representing integers back to the activity
         * @param selectedItems the integers array list
         */
        void onConfirmSelection(ArrayList<Integer> selectedItems);
    }

    public void setSelectedCheckBoxesBooleanArray(boolean[] selectedFieldsBoolean) {
        mSelectedCheckBoxesBooleanArray = selectedFieldsBoolean;
    }

    /**
     * Set the list of fields shown in the dialog
     * @param optionsList the strings array to show
     */
    public void setCheckBoxesStrings(String[] optionsList){
        mCheckBoxesStrings = optionsList;
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
    private void setSelectedCheckBoxesList() {
        for(int i = 0; i< mSelectedCheckBoxesBooleanArray.length; i++){
            if(mSelectedCheckBoxesBooleanArray[i]){
                mSelectedCheckBoxesList.add(i);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedCheckBoxesList = new ArrayList<>();
        setSelectedCheckBoxesList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        builder.setCustomTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(mCheckBoxesStrings, mSelectedCheckBoxesBooleanArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedCheckBoxesList.add(which);
                        } else if (mSelectedCheckBoxesList.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedCheckBoxesList.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.fields_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onConfirmSelection(mSelectedCheckBoxesList);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the CheckBoxesDialogListener to send events to the host
            mListener = (CheckBoxesDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement CheckBoxesDialogListener");
        }
    }
}
