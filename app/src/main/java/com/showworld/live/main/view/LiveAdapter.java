package com.showworld.live.main.view;

import android.content.Context;

import com.showworld.live.base.ui.TAdapter;
import com.showworld.live.base.ui.TAdapterDelegate;
import com.showworld.live.main.module.LiveInfo;

import java.util.List;

/**
 * Created by alex on 16/6/1.
 */
public class LiveAdapter extends TAdapter<LiveInfo> {

    public LiveAdapter(Context context, List<LiveInfo> items, TAdapterDelegate delegate) {
        super(context, items, delegate);
    }

}
