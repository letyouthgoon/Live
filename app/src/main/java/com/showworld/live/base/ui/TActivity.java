package com.showworld.live.base.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.showworld.live.RequestManager;
import com.showworld.live.base.AppAction;
import com.showworld.live.base.AppActionImpl;

/**
 * Created by alex on 16/6/1.
 */
public abstract class TActivity extends AppCompatActivity {

    public AppAction appAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appAction = new AppActionImpl(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appAction.cancelAll(this);
    }

}
