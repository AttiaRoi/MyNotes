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

import roiattia.com.mynotes.R;

public class SortDialog extends DialogFragment {

    private static final String TAG = SortDialog.class.getSimpleName();
    private String[] mOptionsList;
    private SortDialogListener mListener;

    public interface SortDialogListener {
        void onDialogFinishClick(int whichSelected);
    }

    public void setSortOptions(String[] optionsList){
        mOptionsList = optionsList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View title = inflater.inflate(R.layout.title_sort_note_by_dialog, null, false);

        dialog.setCustomTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(mOptionsList, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogFinishClick(which);
                        dismiss();
                    }
                });

        return dialog.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the FieldsDialogListener to send events to the host
            mListener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement FieldsDialogListener");
        }
    }
}
