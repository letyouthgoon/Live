package com.showworld.live.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.showworld.live.R;
import com.showworld.live.SWLApplication;
import com.showworld.live.base.ui.TActivity;
import com.showworld.live.base.util.NetworkUtil;
import com.showworld.live.main.Constants;
import com.showworld.live.main.control.QavsdkControl;
import com.showworld.live.main.module.LiveInfo;

/**
 * Created by alex on 2016/6/1.
 */
public class LiveListActivity extends TActivity implements AdapterView.OnItemClickListener {

    private SwipeRefreshLayout mLiveListSrl;
    private ListView mLiveListLv;
    private LiveAdapter mLiveAdapter = null;
    private QavsdkControl mQavsdkControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_list);
        initView();
        SWLApplication mQavsdkApplication = (SWLApplication) getApplication();
        mQavsdkControl = mQavsdkApplication.getQavsdkControl();
    }

    private void initView() {
        mLiveListSrl = (SwipeRefreshLayout) findViewById(R.id.srl_live_list);
        mLiveListLv = (ListView) findViewById(R.id.lv_live_list);
        mLiveListLv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LiveInfo item = (LiveInfo) parent.getAdapter().getItem(position);

        createRoom(item.getProgrammId());


        Intent i = new Intent();
        startActivity(new Intent(this, LiveActivity.class)
                .putExtra(Constants.EXTRA_ROOM_NUM, roomNum) //room id
                .putExtra(Constants.EXTRA_SELF_IDENTIFIER, mChoseLiveVideoInfo.getUserPhone())
                .putExtra(Constants.EXTRA_GROUP_ID, groupId) // chat converse id
                .putExtra(Constants.EXTRA_PRAISE_NUM, mChoseLiveVideoInfo.getLivePraiseCount()));

    }

    private void createRoom(int num) {
        if (NetworkUtil.isNetAvailable(getApplicationContext())) {
            if (num != 0) {
                int nums = num;
                mQavsdkControl.enterRoom(nums);
            }
        } else {
            Toast.makeText(this, getString(R.string.notify_no_network), Toast.LENGTH_SHORT).show();

        }

    }
}
