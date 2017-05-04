package com.ls.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ls.drupalcon.R;
import com.ls.ui.view.expandablelayout.ExpandableRelativeLayout;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        findViewById(R.id.testButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(expandableLayout.isExpanded()){
                   expandableLayout.collapse();
               }else {
                   expandableLayout.expand();
               }
            }
        });

// toggle expand, collapse
//        expandableLayout.toggle();
// expand
//        expandableLayout.expand();
// collapse
//        expandableLayout.collapse();

// move position of child view
//        expandableLayout.moveChild(0);
// move optional position
//        expandableLayout.move(500);

// set base position which is close position
//        expandableLayout.setClosePosition(500);
    }
}
