package com.ls.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ls.drupalcon.R;

public class ScheduleNameDialog extends DialogFragment {

    public static final String TAG = ScheduleNameDialog.class.getName();
    public static final String EXTRA_SCHEDULE_CODE = "extra_schedule_code";
    private DialogClickListener callback;

    public static ScheduleNameDialog newInstance() {

        Bundle args = new Bundle();

        ScheduleNameDialog fragment = new ScheduleNameDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_shedule, null);
        final EditText editTextId = (EditText)contentView.findViewById(R.id.uniqueCode);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.add_a_schedule);
        alertDialogBuilder.setView(contentView);
        alertDialogBuilder.setPositiveButton(getActivity().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               callback.onYesClick();

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(EXTRA_SCHEDULE_CODE, editTextId.getText().toString()));
            }
        });

        alertDialogBuilder.setNegativeButton(getActivity().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onYesClick();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        return dialog;
    }

    public interface DialogClickListener {
        void onYesClick();

        void onNoClick();
    }

}

