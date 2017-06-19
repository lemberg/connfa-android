package com.ls.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ls.drupalcon.R;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.SharedScheduleManager;
import com.ls.drupalcon.model.managers.ToastManager;
import com.ls.utils.L;

public class CreateScheduleDialog extends DialogFragment {

    public static final String TAG = CreateScheduleDialog.class.getName();
    public static final String EXTRA_SCHEDULE_CODE = "extra_schedule_code";
    public static final String EXTRA_SCHEDULE_NAME = "extra_schedule_name";
    private static long code;


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
        code = getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_shedule_name, null);
        final EditText editTextId = (EditText) contentView.findViewById(R.id.scheduleName);

        editTextId.setText(getScheduleName());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.schedule_name);
        alertDialogBuilder.setView(contentView);
        alertDialogBuilder.setPositiveButton(getActivity().getString(android.R.string.ok), null);
        alertDialogBuilder.setNegativeButton(getActivity().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
            }
        });
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.favorite));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.favorite));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextId.getText().toString();
                if(TextUtils.isEmpty(text)){
                    ToastManager.messageSync(getContext(), "Please enter schedule name");
                }else {
                    Intent intent = getActivity().getIntent();
                    intent.putExtra(EXTRA_SCHEDULE_CODE, code);
                    intent.putExtra(EXTRA_SCHEDULE_NAME, text);
                    L.e("Name = " + text);
                    dialog.dismiss();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
            }
        });

        return dialog;
    }

    private String getScheduleName() {
        if (getArguments() != null) {
            return getString(R.string.schedule) + " " + getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
        } else {
            return "";
        }
    }

//    private long getScheduleCode() {
//            return  getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
//    }

}

