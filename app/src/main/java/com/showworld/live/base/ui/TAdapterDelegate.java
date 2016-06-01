package com.showworld.live.base.ui;

/**
 * Created by alex on 16/6/1.
 */
public interface TAdapterDelegate {

    public int getViewTypeCount();

    public Class<? extends TViewHolder> viewHolderAtPosition(int position);


}
