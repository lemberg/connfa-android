package com.ls.drupalcon.model.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

public class ToastManager {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static Toast toast;
    private static CharSequence toastLabel;

    private ToastManager() { }

    private static void showText(Context context, CharSequence text, int duration) {
        if (toast != null && toast.getView().getWindowVisibility() == View.VISIBLE) {
            if(toastLabel != null && toastLabel.equals(text)){
                return;
            }else {
                toast.cancel();
            }
        }
        toastLabel = text;
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void message(@Nullable Context context, @Nullable String message) {
        if (context != null && message != null) {
            showText(context, message, Toast.LENGTH_LONG);
        }
    }
    public static void messageSync(@Nullable final Context context, @Nullable final String message) {
        if (context != null && message != null) {
            MAIN_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    message(context, message);
                }
            });
        }
    }

}
