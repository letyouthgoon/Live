package com.showworld.live.main.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.showworld.live.R;
import com.showworld.live.SWLApplication;
import com.showworld.live.base.ActionCallbackListener;
import com.showworld.live.base.ui.TActivity;
import com.showworld.live.base.ui.TAdapterDelegate;
import com.showworld.live.base.ui.TViewHolder;
import com.showworld.live.base.util.NetworkUtil;
import com.showworld.live.main.Constants;
import com.showworld.live.main.control.QavsdkControl;
import com.showworld.live.main.module.BasePojo;
import com.showworld.live.main.module.LiveInfo;
import com.showworld.live.main.module.UserInfo;
import com.showworld.live.main.module.getLiveListRet;
import com.tencent.av.sdk.AVError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2016/6/1.
 */
public class LiveListActivity extends TActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "LiveListActivity";
    private int mLoginErrorCode = AVError.AV_OK;
    private SwipeRefreshLayout mLiveListSrl;
    private ListView mLiveListLv;
    private LiveAdapter mLiveAdapter = null;
    private QavsdkControl mQavsdkControl;
    private UserInfo mSelfUserInfo;
    private LiveInfo mLiveItem;

    private int mRoomNum;
    private List<LiveInfo> mLiveList = new ArrayList<LiveInfo>();
    private boolean isFirst = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_START_CONTEXT_COMPLETE)) {
                mLoginErrorCode = intent.getIntExtra(
                        Constants.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
//                refreshWaitingDialog();
                if (mLoginErrorCode != AVError.AV_OK) {
                    Log.e(TAG, "登录失败");
                }
                Log.d(TAG, "start context complete");
            } else if (action.equals(Constants.ACTION_CLOSE_CONTEXT_COMPLETE)) {
                mQavsdkControl.setIsInStopContext(false);
            }
            if (!mSelfUserInfo.isCreater() && action.equals(Constants.ACTION_ROOM_CREATE_COMPLETE)) {
                Log.d(TAG, "liveAcitivity onReceive ACTION_ROOM_CREATE_COMPLETE");
                int mCreateRoomErrorCode = intent.getIntExtra(
                        Constants.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                if (isFirst) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isFirst = false;
                }

                if (mCreateRoomErrorCode == AVError.AV_OK) {
                    if (mLiveItem == null) {
//                            Toast.makeText(LiveActivity.this, "mChoseLiveVideoInfo is null !!!! ", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "LiveActivity onReceive mChoseLiveVideoInfo is " + mLiveItem);
                        return;
                    }
                    startActivity(new Intent(LiveListActivity.this, LiveActivity.class)
                            .putExtra(Constants.EXTRA_ROOM_NUM, mLiveItem.getProgrammId()) //room id
                            .putExtra(Constants.EXTRA_SELF_IDENTIFIER, mLiveItem.getUserPhone())
                            .putExtra(Constants.EXTRA_GROUP_ID, mLiveItem.getLiveGroupId()) // chat converse id
                            .putExtra(Constants.EXTRA_PRAISE_NUM, mLiveItem.getLivePraiseCount()));
                    enterRoom();
                }
            } else if (action.equals(Constants.ACTION_CLOSE_ROOM_COMPLETE)) {

            }
        }
    };


    private void enterRoom() {
        appAction.enterRoom(mRoomNum, mSelfUserInfo.getUserPhone(), new ActionCallbackListener<BasePojo>() {
            @Override
            public void onSuccess(BasePojo bean) {
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_list);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_START_CONTEXT_COMPLETE);
        intentFilter.addAction(Constants.ACTION_CLOSE_CONTEXT_COMPLETE);
        intentFilter.addAction(Constants.ACTION_ROOM_CREATE_COMPLETE);
        intentFilter.addAction(Constants.ACTION_CLOSE_ROOM_COMPLETE);
        registerReceiver(mBroadcastReceiver, intentFilter);

        SWLApplication mQavsdkApplication = (SWLApplication) getApplication();
        mQavsdkControl = mQavsdkApplication.getQavsdkControl();
        mSelfUserInfo = mQavsdkApplication.getMyselfUserInfo();
        startContext();
        initView();
        getLiveVideoList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mQavsdkControl.stopContext();
        mQavsdkControl.setIsInStopContext(false);
    }

    private void startContext() {
        if (!mQavsdkControl.hasAVContext()) {
            //临时模拟个人信息
            String phone = "18511707565";
            phone = "86-" + phone;
            if (TextUtils.isEmpty((mSelfUserInfo.getUsersig()))) {
                finish();
            }
            mLoginErrorCode = mQavsdkControl.startContext(phone, mSelfUserInfo.getUsersig());
//            if (mLoginErrorCode == AVError.AV_OK) {
//                Toast.makeText(this, "login failed!", Toast.LENGTH_LONG).show();
//            }
        }


    }

    private void initView() {
        mLiveListSrl = (SwipeRefreshLayout) findViewById(R.id.srl_live_list);
        mLiveListLv = (ListView) findViewById(R.id.lv_live_list);
        mLiveAdapter = new LiveAdapter(getBaseContext(), R.layout.live_item, mLiveList, new TAdapterDelegate() {
            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
                return null;
            }
        });
        mLiveListLv.setAdapter(mLiveAdapter);

        mLiveListSrl.setOnRefreshListener(this);
        mLiveListLv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLiveItem = (LiveInfo) parent.getAdapter().getItem(position);
        mRoomNum = mLiveItem.getProgrammId();
        createRoom(mRoomNum);
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

    private void getLiveVideoList() {
        appAction.getLiveVideoList(new ActionCallbackListener<getLiveListRet>() {
            @Override
            public void onSuccess(getLiveListRet bean) {
                mLiveAdapter.clear();
                ArrayList<LiveInfo> array = new ArrayList<LiveInfo>();
                for (int i = 0; i < bean.data.size(); i++) {
                    getLiveListRet.Data retData = bean.data.get(i);
                    LiveInfo item = new LiveInfo(retData.programid,
                            retData.subject, R.drawable.user,
                            retData.viewernum, retData.praisenum,
                            new UserInfo(retData.userphone,
                                    retData.username,
                                    retData.headimagepath),
                            retData.groupid, "12345");
                    item.setCoverpath(retData.coverimagepath);
                    array.add(item);
                }
                mLiveAdapter.addAll(array);
                mLiveListSrl.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    @Override
    public void onRefresh() {
        getLiveVideoList();
    }
}
