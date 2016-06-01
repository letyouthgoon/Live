package com.showworld.live.main;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.showworld.live.R;
import com.showworld.live.base.ui.TActivity;

/**
 * Created by alex on 2016/6/1.
 */
public class MainActivity extends TActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onTvClick() {
        startActivity(new Intent(this, LiveListActivity.class));
    }

}
