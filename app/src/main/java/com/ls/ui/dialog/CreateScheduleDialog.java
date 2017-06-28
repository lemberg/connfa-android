package com.ls.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.SharedScheduleManager;
import com.ls.drupalcon.model.managers.ToastManager;
import com.ls.utils.L;

public class CreateScheduleDialog extends DialogFragment {

    public static final String TAG = CreateScheduleDialog.class.getName();
    public static final String EXTRA_SCHEDULE_CODE = "extra_schedule_code";
    public static final String EXTRA_SCHEDULE_NAME = "extra_schedule_name";
    public static final String EXTRA_SCHEDULE_TITLE = "extra_dialog_title";
    private static long code;

    public static CreateScheduleDialog newCreateDialogInstance(long code) {
        String name = App.getContext().getString(R.string.schedule) + " " + code;
        String title = App.getContext().getString(R.string.schedule_name);
        return newInstance(code, name, title);
    }

    public static CreateScheduleDialog newEditDialogInstance(long code, String name) {
        return newInstance(code, name, "Schedule name");
    }

    public static CreateScheduleDialog newInstance(long code, @Nullable String currentName, @NonNull String dialogTitle) {
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_SCHEDULE_CODE, code);
        if (!TextUtils.isEmpty(currentName)) {
            bundle.putString(EXTRA_SCHEDULE_NAME, currentName);
        }
        bundle.putString(EXTRA_SCHEDULE_TITLE, dialogTitle);

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
        alertDialogBuilder.setTitle(getArguments().getString(EXTRA_SCHEDULE_TITLE));
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.favorite));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.favorite));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextId.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastManager.messageSync(getContext(), "Please enter schedule name");
                } else {
                    if (!Model.instance().getSharedScheduleManager().checkIfNameIsExist(text)) {
                        Intent intent = getActivity().getIntent();
                        intent.putExtra(EXTRA_SCHEDULE_CODE, code);
                        intent.putExtra(EXTRA_SCHEDULE_NAME, text);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        dialog.dismiss();
                    }
                }
            }
        });

        return dialog;
    }

    private String getScheduleName() {
        if (getArguments() != null) {
            return getArguments().getString(EXTRA_SCHEDULE_NAME);
        } else {
            return "";
        }
    }

//    private long getScheduleCode() {
//            return  getArguments().getLong(EXTRA_SCHEDULE_CODE, SharedScheduleManager.MY_DEFAULT_SCHEDULE_CODE);
//    }

}

