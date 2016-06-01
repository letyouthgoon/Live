package com.showworld.live;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alex on 16/6/1.
 */
public abstract class TFragment extends Fragment {

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void onDestroy(){
        super.onDestroy();
    }

}
