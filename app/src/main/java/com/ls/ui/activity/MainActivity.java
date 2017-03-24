package com.ls.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.ls.drupalcon.R;
import com.ls.ui.view.RoundedBackgroundSpan;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String nbspSpacing = "\u202F\u202F"; // none-breaking spaces
        String badgeText = nbspSpacing + "    TestTestTestTestTestTestTestTest UI   Session" + nbspSpacing;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append(badgeText);
        stringBuilder.setSpan(
                new RoundedBackgroundSpan( Color.parseColor("#ffffff"), Color.parseColor("#3d4760")),
                badgeText.length() -11,
                badgeText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        stringBuilder.setSpan(new RelativeSizeSpan(1.4f), 0,  badgeText.length() -11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        stringBuilder.append("  ");

        TextView viewById = (TextView)findViewById(R.id.textView12);
        viewById.setText(stringBuilder);


        TextView myTextView = (TextView)findViewById(R.id.textView1);
        viewById.setText(stringBuilder);

        String myString = "myString";
        Spannable spanna = new SpannableString(myString);
        spanna.setSpan(new BackgroundColorSpan(0xFFCCCCCC),3, myString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        myTextView.setText(spanna);

    }
}
