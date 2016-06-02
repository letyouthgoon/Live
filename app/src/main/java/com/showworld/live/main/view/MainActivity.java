package com.showworld.live.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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


    public void onTvClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_jump:
                startActivity(new Intent(this, LiveListActivity.class));
                break;
        }
    }

}
