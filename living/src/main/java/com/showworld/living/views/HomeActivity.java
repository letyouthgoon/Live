package com.showworld.living.views;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.showworld.living.avcontrollers.SwlavsdkControl;
import com.showworld.living.utils.SwlLog;
import com.showworld.living.views.customviews.BaseActivity;
import com.tencent.TIMUserProfile;
import com.showworld.living.R;
import com.showworld.living.model.MySelfInfo;
import com.showworld.living.presenters.InitBusinessHelper;
import com.showworld.living.presenters.LoginHelper;
import com.showworld.living.presenters.ProfileInfoHelper;
import com.showworld.living.presenters.viewinface.ProfileView;
import com.showworld.living.views.customviews.BaseFragmentActivity;

import java.util.List;

/**
 * 主界面
 */
public class HomeActivity extends BaseFragmentActivity implements ProfileView {
    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private ProfileInfoHelper infoHelper;
    private LoginHelper mLoginHelper;
    private final Class fragmentArray[] = {FragmentLiveList.class, FragmentPublish.class, FragmentProfile.class};
    private int mImageViewArray[] = {R.drawable.tab_live, R.drawable.icon_publish, R.drawable.tab_profile};
    private String mTextviewArray[] = {"live", "publish", "profile"};
    private static final String TAG = HomeActivity.class.getSimpleName();


    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        SwlLog.i(TAG, "HomeActivity onStart");
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        layoutInflater = LayoutInflater.from(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);

        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);

        }
        mTabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                DialogFragment newFragment = InputDialog.newInstance();
//                newFragment.show(ft, "dialog");

                startActivity(new Intent(HomeActivity.this, PublishLiveActivity.class));

            }
        });

        // 检测是否需要获取头像
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())) {
            infoHelper = new ProfileInfoHelper(this);
            infoHelper.getMyProfile();
        }
    }

    @Override
    protected void onStart() {
        SwlLog.i(TAG, "HomeActivity onStart");
        super.onStart();
        if (SwlavsdkControl.getInstance().getAVContext() == null) {//retry
            InitBusinessHelper.initApp(getApplicationContext());
            SwlLog.i(TAG, "HomeActivity retry login");
            mLoginHelper = new LoginHelper(this);
            mLoginHelper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_content, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

    @Override
    protected void onDestroy() {
        if (mLoginHelper != null)
            mLoginHelper.onDestory();
        SwlLog.i(TAG, "HomeActivity onDestroy");
        SwlavsdkControl.getInstance().stopContext();
        super.onDestroy();
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        SwlLog.i(TAG, "updateProfileInfo");
        if (null != profile) {
            MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
            if (!TextUtils.isEmpty(profile.getNickName())) {
                MySelfInfo.getInstance().setNickName(profile.getNickName());
            } else {
                MySelfInfo.getInstance().setNickName(profile.getIdentifier());
            }
        }
    }

    @Override
    public void updateUserInfo(int reqid, List<TIMUserProfile> profiles) {
    }
}
