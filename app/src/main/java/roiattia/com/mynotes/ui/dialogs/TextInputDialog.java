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
import android.widget.EditText;
import android.widget.TextView;

import roiattia.com.mynotes.R;

public class TextInputDialog extends DialogFragment {

    private TextInputDialogListener mListener;
    private String mTitle;

    public interface TextInputDialogListener {
        void onInputConfirmed(String input);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_edit_text, null, false);
        final View title = inflater.inflate(R.layout.title_dialog, null, false);
        TextView titleTextView = title.findViewById(R.id.tv_dialog_title);
        titleTextView.setText(mTitle);

        builder.setView(view)
                .setCustomTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = view.findViewById(R.id.et_user_input);
                        mListener.onInputConfirmed(editText.getText().toString());
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
            // Instantiate the TextInputDialogListener so we can send events to the host
            mListener = (TextInputDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement TextInputDialogListener");
        }
    }
}
