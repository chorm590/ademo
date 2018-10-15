package com.chorm.ademo.funClass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.chorm.ademo.R;
import com.chorm.ademo.tools.Logger;
import com.chorm.ademo.views.Watch;

public class CusWatchViewActivity extends Activity implements View.OnClickListener {

    Watch mWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_watch_view);
        mWatch = findViewById(R.id.watch);

        mWatch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Logger.debug("onClick:" + v);
        if(v == mWatch){
            mWatch.refresh();
            mWatch.invalidate();
        }
    }
}
