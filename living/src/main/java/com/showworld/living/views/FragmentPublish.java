package com.showworld.living.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.showworld.living.R;


/**
 * empty 4 occupy position
 */
public class FragmentPublish extends Fragment {

    public FragmentPublish() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.publishfragment_layout, container, false);
        return view;
    }

}
