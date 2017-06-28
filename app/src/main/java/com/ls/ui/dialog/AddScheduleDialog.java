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
import android.widget.EditText;
import android.widget.Toast;

import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.drupalcon.model.Listener;
import com.ls.drupalcon.model.Model;
import com.ls.drupalcon.model.managers.ToastManager;
import com.ls.http.base.ResponseData;

public class AddScheduleDialog extends DialogFragment {

    public static final String TAG = AddScheduleDialog.class.getName();
    public static final int RESULT_OK_CODE_IS_EXIST = 898;
    public static final String EXTRA_SCHEDULE_CODE = "extra_schedule_code";

    public static AddScheduleDialog newInstance() {

        Bundle args = new Bundle();

        AddScheduleDialog fragment = new AddScheduleDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_shedule, null);

        final EditText editTextId = (EditText) contentView.findViewById(R.id.uniqueCode);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.add_a_schedule);
        alertDialogBuilder.setView(contentView);
        alertDialogBuilder.setPositiveButton(getActivity().getString(R.string.add), null);

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
                if (TextUtils.isEmpty(text)) {
                    ToastManager.message(getContext(), "Please enter code");
                } else {
                    final long code = Long.parseLong(text);
                    if (Model.instance().getSharedScheduleManager().checkIfCodeIsExist(code)) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK_CODE_IS_EXIST, getActivity().getIntent());
                        dialog.dismiss();
                    } else {
                        if (Model.instance().getSharedScheduleManager().getMyScheduleCode() == code) {
                            ToastManager.message(getContext(), "Your own code was entered");
                        } else {
                            Model.instance().getSharedScheduleManager().fetchSharedEventsByCode(code, "Schedule " + code, new Listener<ResponseData, ResponseData>() {
                                @Override
                                public void onSucceeded(ResponseData result) {
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(EXTRA_SCHEDULE_CODE, code));
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailed(ResponseData result) {
                                    ToastManager.messageSync(App.getContext(), "Schedule not found. Please check your code");
                                }
                            });


                        }
                    }

                }
            }
        });

        return dialog;
    }
}


