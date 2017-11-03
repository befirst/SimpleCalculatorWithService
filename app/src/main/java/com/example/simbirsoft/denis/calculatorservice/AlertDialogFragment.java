package com.example.simbirsoft.denis.calculatorservice;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AlertDialogFragment extends DialogFragment {

    static final String CODE_MESSAGE = "CODE_MESSAGE";

    static AlertDialogFragment newInstance(String message){
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(CODE_MESSAGE, message);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error)
                .setMessage(getArguments().getString(CODE_MESSAGE))
                .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
