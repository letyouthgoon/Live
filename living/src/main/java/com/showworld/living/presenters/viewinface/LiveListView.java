package com.showworld.living.presenters.viewinface;

import com.showworld.living.model.LiveInfoJson;

import java.util.ArrayList;


/**
 *  列表页面回调
 */
public interface LiveListView extends MvpView{

    void showPage(ArrayList<LiveInfoJson> livelist, int mCurPageIndex);
}
