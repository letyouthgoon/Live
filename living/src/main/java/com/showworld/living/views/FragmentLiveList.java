package com.showworld.living.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.showworld.living.R;
import com.showworld.living.adapters.LiveShowAdapter;
import com.showworld.living.model.CurLiveInfo;
import com.showworld.living.model.LiveInfoJson;
import com.showworld.living.model.MySelfInfo;
import com.showworld.living.presenters.LiveListViewHelper;
import com.showworld.living.presenters.viewinface.LiveListView;
import com.showworld.living.utils.Constants;
import com.showworld.living.utils.SwlLog;

import java.util.ArrayList;


/**
 * 直播列表页面
 */
public class FragmentLiveList extends Fragment implements View.OnClickListener, LiveListView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FragmentLiveList";
    private ListView mLiveList;
    private ArrayList<LiveInfoJson> liveList = new ArrayList<LiveInfoJson>();
    private LiveShowAdapter adapter;
    private LiveListViewHelper mLiveListViewHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mCurPageIndex = 0;

    public FragmentLiveList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLiveListViewHelper = new LiveListViewHelper(this);
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
        mLiveList = (ListView) view.findViewById(R.id.live_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        adapter = new LiveShowAdapter(getActivity(), R.layout.item_liveshow, liveList);
        mLiveList.setAdapter(adapter);

        mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                LiveInfoJson item = liveList.get(i);
                //如果是自己
                if (item.getHost().getUid().equals(MySelfInfo.getInstance().getId())) {
                    Toast.makeText(getActivity(), "this room don't exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                CurLiveInfo.setHostID(item.getHost().getUid());
                CurLiveInfo.setHostName(item.getHost().getUsername());
                CurLiveInfo.setHostAvator(item.getHost().getAvatar());
                CurLiveInfo.setRoomNum(item.getAvRoomId());
                CurLiveInfo.setMembers(item.getWatchCount() + 1); // 添加自己
                CurLiveInfo.setAdmires(item.getAdmireCount());
                CurLiveInfo.setAddress(item.getLbs().getAddress());
                LiveActivity.start(getActivity(), Constants.MEMBER);
                SwlLog.i(TAG, "PerformanceTest  join Live     " + SwlLog.getTime());
            }
        });
        mLiveList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                if (adapter.getState() == CmConstant.STATE_IDLE
//                        && totalItemCount != 0) {
//
//                    boolean closeToEnd = (totalItemCount - firstVisibleItem - visibleItemCount) < 3; // threshold
//                    boolean isEmpty = (adapter.getCount() == 0);
//
//                    if (closeToEnd && !isEmpty) {
//                        mAdapter.setState(CmConstant.STATE_LOADING);
//                        mLiveListViewHelper.getPageData(mCurPageIndex);
//                    }
//                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        mLiveListViewHelper.getPageData(mCurPageIndex);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mLiveListViewHelper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void showPage(ArrayList<LiveInfoJson> result, int pageIndex) {

        mCurPageIndex = pageIndex;
        if (0 == mCurPageIndex) {
            liveList.clear();
        }
        mSwipeRefreshLayout.setRefreshing(false);
        if (null != result) {
            for (LiveInfoJson item : result) {
                liveList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mLiveListViewHelper.getPageData(mCurPageIndex);
    }
}
