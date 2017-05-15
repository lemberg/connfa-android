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
import com.ls.drupalcon.model.managers.SharedScheduleManager;

public class CreateScheduleDialog extends DialogFragment {

    public static final String TAG = CreateScheduleDialog.class.getName();
    public static final String EXTRA_SCHEDULE_CODE = "extra_schedule_code";


    public static CreateScheduleDialog newInstance(long code) {
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_SCHEDULE_CODE, code);

        CreateScheduleDialog fragment = new CreateScheduleDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CreateScheduleDialog newInstance() {
        return new CreateScheduleDialog();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_shedule_name, null);
        final EditText editTextId = (EditText) contentView.findViewById(R.id.scheduleName);

        editTextId.setText(getScheduleName());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.add_a_schedule);
        alertDialogBuilder.setView(contentView);
        alertDialogBuilder.setPositiveButton(getActivity().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(EXTRA_SCHEDULE_CODE, getScheduleCode()));
            }
        });

        alertDialogBuilder.setNegativeButton(getActivity().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        return dialog;
    }

    private String getScheduleName() {
        if (getArguments() != null) {
            return getString(R.string.schedule) + " " + getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
        } else {
            return "";
        }
    }

    private long getScheduleCode() {
            return  getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
    }

}

