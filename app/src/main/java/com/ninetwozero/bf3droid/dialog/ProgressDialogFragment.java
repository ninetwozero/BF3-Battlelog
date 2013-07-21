package com.ninetwozero.bf3droid.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ninetwozero.bf3droid.R;

public class ProgressDialogFragment extends DialogFragment {
    private String message;
    private boolean finishActivityOnCancel;

    public ProgressDialogFragment() {
        checkForArguments();
    }

    public void checkForArguments() {
        Bundle arguments = getArguments();
        if( arguments == null ) {
            message = "Loading...";
            finishActivityOnCancel = false;
        } else {
            message = arguments.getString("message");
            finishActivityOnCancel = arguments.getBoolean("finish");
        }
    }
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        checkForArguments();
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(R.string.label_wait);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public void updateMessage(String message) {
        if( getDialog() == null ) return;
        ((ProgressDialog) getDialog()).setMessage(message);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(finishActivityOnCancel && getActivity() != null) {
            getActivity().finish();
        }
        return;
    }

    public static class Builder {
        private static ProgressDialogFragment dialogFragment;

        public static ProgressDialogFragment getInstance() {
            if( dialogFragment == null ) {
                Bundle bundle = new Bundle();
                bundle.putString("message", "Loading...");
                bundle.putBoolean("finish", false);

                dialogFragment = new ProgressDialogFragment();
                dialogFragment.setArguments(bundle);
            }
            return dialogFragment;
        }
    }
}