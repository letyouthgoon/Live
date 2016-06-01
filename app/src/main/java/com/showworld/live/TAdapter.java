package com.showworld.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by alex on 16/6/1.
 */
public class TAdapter<T> extends BaseAdapter {

    protected final Context context;
    private final List<T> items;
    private final LayoutInflater inflater;
    private final TAdapterDelegate delegate;

    public TAdapter(Context context, List<T> items, TAdapterDelegate delegate) {
        this.context = context;
        this.items = items;
        this.delegate = delegate;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    public T getItem(int position) {
        return position < getCount() ? items.get(position) : null;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }

    public View getView(final int position, View convertView, ViewGroup parent, boolean needRefresh) {
        if (convertView == null) {
            convertView = viewAtPosition(position);
        }

        TViewHolder holder = (TViewHolder) convertView.getTag();
        holder.setPosition(position);
        return convertView;
    }


    public View viewAtPosition(int position) {
        TViewHolder holder = null;
        View view = null;
        try {
            Class<?> viewHolder = delegate.viewHolderAtPosition(position);
            holder = (TViewHolder) viewHolder.newInstance();
            holder.setAdapter(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view = holder.getView(inflater);
        view.setTag(holder);
        holder.setContext(view.getContext());
        return view;
    }


}
