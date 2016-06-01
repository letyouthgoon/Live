package com.showworld.live.base.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by alex on 16/6/1.
 */
public abstract class TViewHolder {

    protected Context context;
    /**
     * list item view
     */
    protected View view;

    /**
     * index of item
     */
    protected int position;
    protected TAdapter adapter;


    public TViewHolder() {

    }


    public View getView(LayoutInflater inflater) {
        int resId = getResId();
        view = inflater.inflate(resId, null);
        inflate();
        return view;
    }

    protected abstract int getResId();

    protected abstract void inflate();

    public void setContext(Context context) {
        this.context = context;
    }

    protected void setAdapter(TAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
