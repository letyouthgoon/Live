package com.showworld.live.main.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.showworld.live.R;
import com.showworld.live.SWLApplication;
import com.showworld.live.base.ActionCallbackListener;
import com.showworld.live.base.ui.TActivity;
import com.showworld.live.base.util.NetworkUtil;
import com.showworld.live.main.Constants;
import com.showworld.live.main.HttpUtil;
import com.showworld.live.main.control.QavsdkControl;
import com.showworld.live.main.module.BasePojo;
import com.showworld.live.main.module.ChatEntity;
import com.showworld.live.main.module.MemberInfo;
import com.showworld.live.main.module.UserInfo;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVConstants;
import com.tencent.av.sdk.AVEndpoint;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.PhoneStatusTools;
import com.tencent.open.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by alex on 2016/6/1.
 * 直播界面
 */
public class LiveActivity extends TActivity implements View.OnClickListener {

    private static final String TAG = "AvActivity";
    private static final String UNREAD = "0";
    private static final int REFRESH_CHAT = 0x100;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = REFRESH_CHAT + 1;
    private static final int REMOVE_CHAT_ITEM_TIMER_TASK = UPDAT_WALL_TIME_TIMER_TASK + 1;
    private static final int UPDAT_MEMBER = REMOVE_CHAT_ITEM_TIMER_TASK + 1;
    private static final int MEMBER_EXIT_COMPLETE = UPDAT_MEMBER + 1;
    private static final int CLOSE_VIDEO = MEMBER_EXIT_COMPLETE + 1;
    private static final int START_RECORD = CLOSE_VIDEO + 1;
    private static final int IM_HOST_LEAVE = START_RECORD + 1;
    private static final int GET_ROOM_INFO = IM_HOST_LEAVE + 1;
    private static final int REFRESH_PRAISE = GET_ROOM_INFO + 1;
    private static final int PRIASE_MSG = 1;
    private static final int MEMBER_ENTER_MSG = 2;
    private static final int MEMBER_EXIT_MSG = 3;
    private static final int VIDEOCHAT_INVITE = 4;
    private static final int YES_I_JOIN = 5;
    private static final int NO_I_REFUSE = 6;
    private static final int MUTEVOICE = 7;
    private static final int UNMUTEVOICE = 8;
    private static final int MUTEVIDEO = 9;
    private static final int UNMUTEVIDEO = 10;
    private static final int CLOSEVIDEOSEND = 11;
    private static final int ERROR_MESSAGE_TOO_LONG = 0x1;
    private static final int ERROR_ACCOUNT_NOT_EXIT = ERROR_MESSAGE_TOO_LONG + 1;
    private static final int MAX_REQUEST_VIEW_COUNT = 3;//当前最大支持请求画面个数
    private static boolean LEVAE_MODE = false;
    private static boolean hasPullMemberList = false;

    private static final int DIALOG_INIT = 0;
    private static final int DIALOG_AT_ON_CAMERA = DIALOG_INIT + 1;
    private static final int DIALOG_ON_CAMERA_FAILED = DIALOG_AT_ON_CAMERA + 1;
    private static final int DIALOG_AT_OFF_CAMERA = DIALOG_ON_CAMERA_FAILED + 1;
    private static final int DIALOG_OFF_CAMERA_FAILED = DIALOG_AT_OFF_CAMERA + 1;
    private static final int DIALOG_AT_SWITCH_FRONT_CAMERA = DIALOG_OFF_CAMERA_FAILED + 1;
    private static final int DIALOG_SWITCH_FRONT_CAMERA_FAILED = DIALOG_AT_SWITCH_FRONT_CAMERA + 1;
    private static final int DIALOG_AT_SWITCH_BACK_CAMERA = DIALOG_SWITCH_FRONT_CAMERA_FAILED + 1;
    private static final int DIALOG_SWITCH_BACK_CAMERA_FAILED = DIALOG_AT_SWITCH_BACK_CAMERA + 1;
    private static final int DIALOG_DESTROY = DIALOG_SWITCH_BACK_CAMERA_FAILED + 1;

    private boolean mIsClicked = false;
    private TIMConversation mConversation;
    private PowerManager.WakeLock wakeLock;
    private ProgressDialog mDialogInit = null;
    private SWLApplication mQavsdkApplication;
    private QavsdkControl mQavsdkControl;
    private UserInfo mSelfUserInfo;
    //    private EditText mEditTextInputMsg;
    private boolean mIsSuccess = false;
    private Timer mVideoTimer;
    private VideoTimerTask mVideoTimerTask;
    private long second = 0;
    private Timer mHeartClickTimer;
    private int mOnOffCameraErrorCode = AVError.AV_OK;
    private int mSwitchCameraErrorCode = AVError.AV_OK;
    private String mHostIdentifier = "";
    private String mRecvIdentifier = "";
    private String selectIdentier = "";
    private TextView mButtonBeauty;
    private boolean currentCameraIsFront = true;
    private boolean mIsPaused = false;
    private Timer mChatTimer;
    private ChatTimerTask mChatTimerTask;
    private HashMap<String, Integer> viewIndex = new HashMap<String, Integer>();
    private String groupId;
    private int mMemberVideoCount = 0;
    private long time;
    private int praiseNum;
    private TIMConversation mSystemConversation, testConversation;
    ArrayList<MemberInfo> mMemberList, mVideoMemberList, mNormalMemberList;
    //    private ListView mListViewMsgItems;
    private AVView mRequestViewList[] = null;
    private String mRequestIdentifierList[] = null;
    //    private ImageButton mButtonPraise;
    private int roomNum;
    //    private TextView mPraiseNum;
//    private Button mButtonSendMsg;
    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;


    private Dialog dialog;
    private Boolean OpenVoice = false;
    private int mMaskViewCount = 0;
    private InputMethodManager mInputKeyBoard;
    private int groupForPush;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_PRAISE:
                    if (praiseNum == 0)
                        praiseNum++;
//                    mPraiseNum.setText("" + praiseNum);
                    break;
                case IM_HOST_LEAVE:
//                    onMemberExit();
                    onCloseVideo();
                    break;

                case ERROR_MESSAGE_TOO_LONG:
                    Toast.makeText(getBaseContext(), "消息太长，发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_ACCOUNT_NOT_EXIT:
                    Toast.makeText(getBaseContext(), "对方账号不存在或未登陆过！", Toast.LENGTH_SHORT).show();
                    break;

                case UPDAT_WALL_TIME_TIMER_TASK:
//                    updateWallTime();
                    break;
                case REMOVE_CHAT_ITEM_TIMER_TASK:
//                    removeChatItem();
                    break;
                case UPDAT_MEMBER:
                    updateMemberView();
//                    mChatMsgListAdapter.refresh(hostMember);
                    break;
//                case REFRESH_HOST_INFO
//                    break;
                case MEMBER_EXIT_COMPLETE:
                    sendCloseMsg();
                    break;
                case CLOSE_VIDEO:
                    onCloseVideo();
                    break;
                case START_RECORD:
//                    startRecord();
                    break;
                case GET_ROOM_INFO:
                    getMemberInfo();
                    break;
                case REFRESH_CHAT:
//                    showTextMessage((TIMMessage) msg.obj);
                default:
                    break;
            }
            return false;
        }
    });

    private void handleCustomMsg(TIMElem elem) {
        Log.i(TAG, " inviteVC handleCustomMsg  ");
        try {
            String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
            Log.i(TAG, " inviteVC handleCustomMsg  :" + customText);
            String splitItems[] = customText.split("&");
//            if(splitItems.length<=3){
//                splitItems[3]="";
//            }
            int cmd = Integer.parseInt(splitItems[1]);
            for (int i = 0; i < splitItems.length; ++i) {
                Log.d(TAG, " splitItems :" + splitItems[i] + " loop " + i);
            }
            switch (cmd) {
//                case PRIASE_MSG:
//                    int num = Integer.parseInt(splitItems[2]);
//                    praiseNum += num;
//                    mPraiseNum.setText("" + praiseNum);
//                    break;
                //用户登录消息
                case MEMBER_ENTER_MSG:
                    boolean isExist = false;
                    //判断是否已经群组存在
                    for (int i = 0; i < mMemberList.size(); ++i) {

                        String userPhone = mMemberList.get(i).getUserPhone();
                        if (userPhone.equals(splitItems[0])) {
                            isExist = true;
                            Log.d(TAG, " willguo handleCustomMsg isExist = true  ");
                            break;
                        }
                    }
                    //不存在增加
                    if (!isExist) {
                        Log.d(TAG, "willguo handleCustomMsg  isExist = false");
                        MemberInfo member = null;
                        //包含完整信息
                        if (splitItems.length <= 3) {
                            member = new MemberInfo(splitItems[0], splitItems[2], "");
                        } else {
                            member = new MemberInfo(splitItems[0], splitItems[2], splitItems[3]);
                        }
                        if (!member.getUserPhone().equals(mSelfUserInfo.getUserPhone())) {
                            mMemberList.add(member);
                            mNormalMemberList.add(member);
//                            mMemberListButton.setText("" + mMemberList.size());
                        }
//                        updateMemberHeadImage();
                        mHandler.sendEmptyMessage(UPDAT_MEMBER);
                    }
                    break;
                //用户登出消息
                case MEMBER_EXIT_MSG:
                    for (int i = 0; i < mMemberList.size(); ++i) {
                        String userPhone = mMemberList.get(i).getUserPhone();
                        if (userPhone.equals(splitItems[0])) {
                            Log.d(TAG, "handleCustomMsg member leave userPhone " + userPhone);

                            mQavsdkControl.closeMemberView(userPhone);
                            mMemberList.remove(i);
                            viewIndexRemove(userPhone);
                            MemberInfo member = findMemberInfo(mVideoMemberList, userPhone);
                            if (member != null) {
                                Log.d(TAG, "before  mVideoMemberList remove   " + mVideoMemberList.size());
                                mVideoMemberList.remove(member);
                                Log.d(TAG, "after mVideoMemberList remove " + mVideoMemberList.size());

                            } else {
                                MemberInfo normalMember = findMemberInfo(mNormalMemberList, userPhone);
                                mNormalMemberList.remove(normalMember);
                            }
//                            mMemberListButton.setText("" + mMemberList.size());

                        }
                    }
                    updateMemberView();
                    break;
//                case VIDEOCHAT_INVITE:
//                    showInviteDialog();
//                    break;
//                case YES_I_JOIN:
//                    //对方答应加入
//                    String memberIdentifier = splitItems[0];
//                    Log.i(TAG, "handleCustomMsg YES_I_JOIN+ " + memberIdentifier);
//                    upMemberLevel(memberIdentifier);
////                    acceptHideMaskView(memberIdentifier);
//                    requestMultiView(memberIdentifier);
//                    acceptHideMaskView(memberIdentifier);
//                    break;
//                case NO_I_REFUSE:
//                    String memberIdentifier2 = splitItems[0];
//                    //
//                    refuseHideMaskView(memberIdentifier2);
//                    Toast.makeText(AvActivity.this, memberIdentifier2 + "memberIdentifier2 refuese !", Toast.LENGTH_SHORT).show();
//                    break;
                case MUTEVOICE:
                    AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();
                    if (!OpenVoice) {
                        Toast.makeText(LiveActivity.this, "host open your voice ", Toast.LENGTH_SHORT).show();
                        avAudioCtrl.enableMic(true);
                        OpenVoice = true;
                    } else {
                        Toast.makeText(LiveActivity.this, "host close your voice ", Toast.LENGTH_SHORT).show();
                        avAudioCtrl.enableMic(false);
                        OpenVoice = false;
                    }
                    //关闭Mic
                    break;
                case UNMUTEVOICE:
                    Toast.makeText(LiveActivity.this, "host allow your voice again ", Toast.LENGTH_SHORT).show();
                    //开启Mic
                    AVAudioCtrl avAudioCtrl2 = mQavsdkControl.getAVContext().getAudioCtrl();
                    avAudioCtrl2.enableMic(true);
                    break;
                case MUTEVIDEO:
                    if (!mQavsdkControl.getIsEnableCamera()) {
                        //打开你的视频
                        Toast.makeText(LiveActivity.this, "host open your camera  ", Toast.LENGTH_SHORT).show();
                        mQavsdkControl.toggleEnableCamera();
                    } else {
                        //关闭你的视频
                        Toast.makeText(LiveActivity.this, "host close your camera ", Toast.LENGTH_SHORT).show();
                        mQavsdkControl.toggleEnableCamera();
                    }
                    break;
                case UNMUTEVIDEO:
                    Toast.makeText(LiveActivity.this, "host allow your video again ", Toast.LENGTH_SHORT).show();
                    //开启Video
//                    if (mQavsdkControl.getIsInOnOffCamera() == false) {
                    mQavsdkControl.toggleEnableCamera();
//                    }
                    break;
                case CLOSEVIDEOSEND:
//                    if (inviteDialog != null && inviteDialog.isShowing())
//                        inviteDialog.dismiss();
                    Toast.makeText(LiveActivity.this, "host close your video  ", Toast.LENGTH_SHORT).show();

                    if (mQavsdkControl.getIsEnableCamera() == true) {
                        mQavsdkControl.toggleEnableCamera();
                    }
                    AVAudioCtrl avAudioCtrl3 = mQavsdkControl.getAVContext().getAudioCtrl();
                    avAudioCtrl3.enableMic(false);
                    OpenVoice = false;

                default:
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, " inviteVC handleCustomMsg  " + e.toString());
        }
    }

    private TIMMessageListener msgListener = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {

//            Log.d(TAG, "onNewMessagesGet  " + list.size());
//            if (isTopActivity()) {
//                //解析TIM推送消息
//                if (groupId != null) {
//                    refreshChat2(list);
//
//                }
//
//            }
            return false;
        }
    };
    private OrientationEventListener mOrientationEventListener = null;
    private int mRotationAngle = 0;

    @Override
    public void onClick(View v) {

    }

    private class VideoTimerTask extends TimerTask {
        public void run() {
            ++second;
            mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    private class ChatTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(REMOVE_CHAT_ITEM_TIMER_TASK);
        }
    }

    private TimerTask mHeartClickTask = new TimerTask() {
        @Override
        public void run() {
            heartClick();
        }
    };
    private AVEndpoint.RequestViewListCompleteCallback mRequestViewListCompleteCallback = new AVEndpoint.RequestViewListCompleteCallback() {
        protected void OnComplete(String identifierList[], int count, int result) {
            // TODO
            Log.d(TAG, "RequestViewListCompleteCallback.OnComplete");
        }
    };

    private void heartClick() {
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.e(TAG, "WL_DEBUG netinfo mobile = " + mobileInfo.isConnected() + ", wifi = " + wifiInfo.isConnected());

            int netType = NetworkUtil.getNetWorkType(LiveActivity.this);
            Log.e(TAG, "WL_DEBUG connectionReceiver getNetWorkType = " + netType);
            mQavsdkControl.setNetType(netType);

            if (!mobileInfo.isConnected() && !wifiInfo.isConnected()) {
                Log.e(TAG, "WL_DEBUG connectionReceiver no network = ");
                // unconnect network
                // 暂时不关闭
//				if (ctx instanceof Activity) {
//					Toast.makeText(getApplicationContext(), ctx.getString(R.string.notify_no_network), Toast.LENGTH_SHORT).show();
//					((Activity)ctx).finish();
//				}
            } else {
                // connect network
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action = " + action);
            if (action.equals(Constants.ACTION_SURFACE_CREATED)) {
                locateCameraPreview();
                wakeLock.acquire();
//                mQavsdkControl.toggleEnableCamera();
                if (mSelfUserInfo.isCreater()) {
                    initTIMGroup();
//                    mEditTextInputMsg.setClickable(true);
                    mIsSuccess = true;
                    mVideoTimer = new Timer(true);
                    mVideoTimerTask = new VideoTimerTask();
                    mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
                    mQavsdkControl.toggleEnableCamera();
                    boolean isEnable = mQavsdkControl.getIsEnableCamera();
                    refreshCameraUI();
                    if (mOnOffCameraErrorCode != AVError.AV_OK) {
//                        showDialog(isEnable ? DIALOG_OFF_CAMERA_FAILED : DIALOG_ON_CAMERA_FAILED);
                        mQavsdkControl.setIsInOnOffCamera(false);
                        refreshCameraUI();
                    }
//                    Log.d(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess());

//                    if (mQavsdkApplication.isHandleMemberRoomSuccess()) {
//                        Log.d(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " yes  do it normally ");
                    mHandler.sendEmptyMessageDelayed(GET_ROOM_INFO, 0);
//                    } else {
//                        Log.w(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " no wait for call");
//                    }
                    mQavsdkControl.setRequestCount(0);
                    //上报主播心跳
                    mHeartClickTimer.schedule(mHeartClickTask, 1000, 10000);
                } else {

                    hostRequestView(mHostIdentifier);
                }
            } else if (action.equals(Constants.ACTION_VIDEO_CLOSE)) {
                String identifier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
                if (!TextUtils.isEmpty(mRecvIdentifier)) {
                    mQavsdkControl.setRemoteHasVideo(false, mRecvIdentifier, 0);
                }

                mRecvIdentifier = identifier;
            } else if (action.equals(Constants.ACTION_VIDEO_SHOW)) {
                //成员模式加入视频聊天室
                String identifier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
                Log.d(TAG, "onReceive ACTION_VIDEO_SHOW  id " + identifier);
                mRecvIdentifier = identifier;
                mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier, 0);
                //IMSDk 加入聊天室
                joinGroup();
                initTIMGroup();
                mIsSuccess = true;
//                mEditTextInputMsg.setClickable(true);
                //获取群组成员信息
                getMemberInfo();
                //发消息通知大家 自己上线了
                onMemberEnter();
//                Util.switchWaitingDialog(ctx, mDialogInit, DIALOG_INIT, false);
            } else if (action.equals(Constants.ACTION_ENABLE_CAMERA_COMPLETE)) {
                Log.d(TAG, "onClick ACTION_ENABLE_CAMERA_COMPLETE   " + " status " + mQavsdkControl.getIsEnableCamera());
                //自己是主播才本地渲染摄像头界面

                boolean isbeauty = mQavsdkControl.getAVContext().getVideoCtrl().enableBeauty(true);
                //如果具备美颜能力 显示美颜接口
                if (isbeauty)
                    mButtonBeauty.setVisibility(View.VISIBLE);

                if (mSelfUserInfo.isCreater() == true) {
                    refreshCameraUI();
                    mOnOffCameraErrorCode = intent.getIntExtra(Constants.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                    boolean isEnable = intent.getBooleanExtra(Constants.EXTRA_IS_ENABLE, false);

                    if (mOnOffCameraErrorCode == AVError.AV_OK) {
                        if (!mIsPaused) {
                            Log.d(TAG, "ACTION_ENABLE_CAMERA_COMPLETE mHostIdentifier " + mHostIdentifier);
                            if (!mHostIdentifier.startsWith("86-")) {
                                mHostIdentifier = "86-" + mHostIdentifier;
                            }
                            mQavsdkControl.setSelfId(mHostIdentifier);
                            mQavsdkControl.setLocalHasVideo(isEnable, mHostIdentifier);
//                            startDefaultRecord();
                        }
                    } else {
//                        showDialog(isEnable ? DIALOG_ON_CAMERA_FAILED : DIALOG_OFF_CAMERA_FAILED);
                    }

                    if (currentCameraIsFront == false) {
                        Log.d(TAG, " onSwitchCamera!!ACTION_ENABLE_CAMERA_COMPLETE and lastTime is backCamera :  " + mQavsdkControl.getIsInOnOffCamera());
//                        onSwitchCamera();
                    }
                }
            } else if (action.equals(Constants.ACTION_SWITCH_CAMERA_COMPLETE)) {
                Log.d(TAG, " onSwitchCamera!! ACTION_SWITCH_CAMERA_COMPLETE  " + mQavsdkControl.getIsInOnOffCamera());
                refreshCameraUI();

                mSwitchCameraErrorCode = intent.getIntExtra(Constants.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
                boolean isFront = intent.getBooleanExtra(Constants.EXTRA_IS_FRONT, false);
                if (mSwitchCameraErrorCode != AVError.AV_OK) {
//                    showDialog(isFront ? DIALOG_SWITCH_FRONT_CAMERA_FAILED : DIALOG_SWITCH_BACK_CAMERA_FAILED);
                } else {
                    currentCameraIsFront = mQavsdkControl.getIsFrontCamera();
                    Log.d(TAG, "onSwitchCamera  " + currentCameraIsFront);
                }
            } else if (action.equals(Constants.ACTION_MEMBER_CHANGE)) {

            } else if (action.equals(Constants.ACTION_INSERT_ROOM_TO_SERVER_COMPLETE)) {
                Log.w(TAG, "getMemberInfo isHandleMemberRoomSuccess " + mQavsdkApplication.isHandleMemberRoomSuccess() + " now is time ");
                mHandler.sendEmptyMessageDelayed(GET_ROOM_INFO, 0);
            } else if (action.equals(Constants.ACTION_INVITE_MEMBER_VIDEOCHAT)) {
                //发起邀请消息
                selectIdentier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
                Log.d(TAG, "onReceive inviteVC selectIdentier " + selectIdentier);

                if (viewIndex != null) {
                    String id;
                    if (selectIdentier.startsWith("86-")) {
                        id = selectIdentier.substring(3);

                    } else {
                        id = selectIdentier;
                    }
                    if (viewIndex.containsKey(id)) {
                        Toast.makeText(LiveActivity.this, "you can't allowed to invite the same people", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //开始邀请信息
//                sendMaskViewStatus(selectIdentier);
//                sendVCInvitation(selectIdentier);

            } else if (action.equals(Constants.ACTION_MEMBER_VIDEO_SHOW)) {
                String identifier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
                mRecvIdentifier = identifier;
                //不在这个位置
//                int viewindex = viewIndex.get(identifier.substring(3));
                //第一个位置
                int locactionIndex = mQavsdkControl.getSmallVideoView();
                mMemberVideoCount = locactionIndex;
                Log.d(TAG, "onReceive ACTION_VIDEO_SHOW  id " + identifier + " viewindex " + locactionIndex);
                mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier, locactionIndex);

            } else if (action.equals(Constants.ACTION_SHOW_VIDEO_MEMBER_INFO)) {
                String identifier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
//                showVideoMemberInfo(identifier);
            } else if (action.equals(Constants.ACTION_CLOSE_MEMBER_VIDEOCHAT)) {
                String identifier = intent.getStringExtra(Constants.EXTRA_IDENTIFIER);
                closeVideoMemberByHost(identifier);
            } else if (action.equals(Constants.ACTION_CLOSE_ROOM_COMPLETE)) {
                closeActivity();
            }


        }
    };

    private void getMemberInfo() {
    }

    private void destroyTIM() {
        TIMManager.getInstance().removeMessageListener(msgListener);
        Log.d(TAG, "WL_DEBUG onDestroy");
        if (groupId != null && mIsSuccess) {
            if (mSelfUserInfo.isCreater()) {
                TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "quit group error " + i + " " + s);
                    }

                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "delete group success");
                        Log.d(TAG, "WL_DEBUG onDestroy");
                    }
                });
            } else {
                TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "quit group error " + i + " " + s);
                    }

                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "delete group success");
                        Log.i(TAG, "WL_DEBUG onDestroy");
                    }
                });
            }
            TIMManager.getInstance().deleteConversation(TIMConversationType.Group, groupId);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mIsClicked) {
                    mIsClicked = false;
                    break;
                }
                if (mSelfUserInfo.isCreater()) {
                }
//                    hostCloseAlertDialog();
                else {
//                    memberCloseAlertDialog();
//                    onCloseVideo();
                    onMemberExit();
                }
                break;
        }

        return false; //这一句很关键
    }

    private void sendCloseMsg() {
        final String msg = "轻轻地“" + mSelfUserInfo.getUserName() + "”离开了";
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (msg.length() == 0)
                    return;
                try {
                    byte[] byte_num = msg.getBytes("utf8");
                    if (byte_num.length > 160) {
                        mHandler.sendEmptyMessage(ERROR_MESSAGE_TOO_LONG);
                        return;
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
                TIMMessage Nmsg = new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText(msg);
                if (Nmsg.addElement(elem) != 0) {
                    return;
                }
                mConversation.sendMessage(Nmsg, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        if (i == 85) {
                            mHandler.sendEmptyMessage(ERROR_MESSAGE_TOO_LONG);
                        } else if (i == 6011) {
                            mHandler.sendEmptyMessage(ERROR_ACCOUNT_NOT_EXIT);
                        }
                        Log.e(TAG, "send message failed. code: " + i + " errmsg: " + s);
                        mHandler.sendEmptyMessage(CLOSE_VIDEO);
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        Log.e(TAG, "Send text Msg ok");
                        mHandler.sendEmptyMessage(CLOSE_VIDEO);
                    }
                });
            }
        }).start();

    }
    /**
     * 发一条消息通知大家 自己下线了
     */
    private void onMemberExit() {
        mQavsdkApplication.exitPlusPlus();
        String message = mSelfUserInfo.getUserPhone() + "&"
                + MEMBER_EXIT_MSG + "&" + "inedex: " + mQavsdkApplication.getExitIndex() + "&";
        ;
        TIMMessage Tim = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(message.getBytes());
        elem.setDesc(UNREAD);
        if (1 == Tim.addElement(elem))
            Toast.makeText(getApplicationContext(), "exit error", Toast.LENGTH_SHORT).show();
        else {
            mConversation.sendMessage(Tim, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "exit error" + i + ": " + s);

                    mHandler.sendEmptyMessage(MEMBER_EXIT_COMPLETE);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {

                    TIMCustomElem elem = (TIMCustomElem) (timMessage.getElement(0));
                    try {
                        String text = new String(elem.getData(), "utf-8");
                        Log.i(TAG, "msgSystem send groupmsg exit  success :" + text);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(MEMBER_EXIT_COMPLETE);
                }
            });
        }
        mMemberList.clear();
    }

    private void onCloseVideo() {
//        if (mSelfUserInfo.isCreater() == true)
//            stopRecord();
//        stopOrientationListener();
//		showDialog(DIALOG_DESTROY);
        if (mSelfUserInfo.isCreater() != true) {
//            Util.switchWaitingDialog(ctx, mDialogAtDestroy, DIALOG_DESTROY, true);
        }
        if (mIsSuccess) {
            mChatTimer.cancel();
            mVideoTimer.cancel();
//            timer.cancel();
            mHeartClickTimer.cancel();
        }
        //退出IM聊天室
//        destroyTIM();
        //退出AV那边群
        mQavsdkControl.exitRoom();

//        if (wakeLock.isHeld())
//            wakeLock.release();
//
//        if (mSelfUserInfo.isCreater() == true) {
//            closeLive();
//            setResult(Util.SHOW_RESULT_CODE);
//            Util.switchWaitingDialog(ctx, mDialogAtDestroy, DIALOG_DESTROY, true);
////            startActivity(new Intent(AvActivity.this, GameOverActivity.class)
////                    .putExtra(Util.EXTRA_ROOM_NUM, roomNum)
////                    .putExtra(Util.EXTRA_LEAVE_MODE,false)
////                    );
//        } else {
//            leaveLive();
//            setResult(Util.VIEW_RESULT_CODE);
//        }
//
//        startActivity(new Intent(AvActivity.this, GameOverActivity.class)
//                        .putExtra(Util.EXTRA_ROOM_NUM, roomNum)
//                        .putExtra(Util.EXTRA_LEAVE_MODE, LEVAE_MODE)
//        );
//        finish();
    }

    private void leaveLive() {
        appAction.leaveLive(roomNum, mSelfUserInfo.getUserPhone(), new ActionCallbackListener<BasePojo>() {
            @Override
            public void onSuccess(BasePojo data) {

            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void closeLive() {

        appAction.closeLive(roomNum, new ActionCallbackListener<BasePojo>() {
            @Override
            public void onSuccess(BasePojo data) {

            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void closeActivity() {
        destroyTIM();
        if (wakeLock.isHeld())
            wakeLock.release();
        if (mSelfUserInfo.isCreater() == true) {
            closeLive();
            setResult(Constants.SHOW_RESULT_CODE);
//            Util.switchWaitingDialog(ctx, mDialogAtDestroy, DIALOG_DESTROY, true);
//            startActivity(new Intent(AvActivity.this, GameOverActivity.class)
//                    .putExtra(Util.EXTRA_ROOM_NUM, roomNum)
//                    .putExtra(Util.EXTRA_LEAVE_MODE,false)
//                    );
        } else {
            leaveLive();
            setResult(Constants.VIEW_RESULT_CODE);
        }
        startActivity(new Intent(LiveActivity.this, GameOverActivity.class)
                .putExtra(Constants.EXTRA_ROOM_NUM, roomNum)
                .putExtra(Constants.EXTRA_LEAVE_MODE, LEVAE_MODE)
        );
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live);
        registerBroadcastReceiver();
        mQavsdkApplication = (SWLApplication) getApplication();
        mQavsdkControl = mQavsdkApplication.getQavsdkControl();
        if (NetworkUtil.getNetWorkType(this) != AVConstants.NETTYPE_NONE) {
            mQavsdkControl.setNetType(NetworkUtil.getNetType(this));
        }
        if (mQavsdkControl.getAVContext() != null) {
            mQavsdkControl.onCreate((SWLApplication) getApplication(), findViewById(android.R.id.content));
        } else {
            finish();
        }
        mQavsdkControl.setRequestCount(0);
        mSelfUserInfo = mQavsdkApplication.getMyselfUserInfo();
        mMemberList = mQavsdkControl.getMemberList();
        mNormalMemberList = copyToNormalMember();
        mVideoMemberList = new ArrayList<MemberInfo>();
        roomNum = getIntent().getExtras().getInt(Constants.EXTRA_ROOM_NUM);
        groupForPush = roomNum;
        mRecvIdentifier = "" + roomNum;
        mHostIdentifier = getIntent().getExtras().getString(Constants.EXTRA_SELF_IDENTIFIER);
        groupId = getIntent().getExtras().getString(Constants.EXTRA_GROUP_ID);
        if (!mSelfUserInfo.isCreater()) {
            praiseNum = getIntent().getExtras().getInt(Constants.EXTRA_PRAISE_NUM);
        }
        mIsSuccess = false;
        mRequestIdentifierList = new String[MAX_REQUEST_VIEW_COUNT];
        mRequestViewList = new AVView[MAX_REQUEST_VIEW_COUNT];
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
        initView();

//        initShowTips();
        registerOrientationListener();
    }

    private void initView() {
//        ImageButton mButtonSwitchCamera;
//        hostHead = (CircularImageButton) findViewById(R.id.host_head);
//        mButtonMute = (ImageButton) findViewById(R.id.mic_btn);
//        mButtonBeauty = (TextView) findViewById(R.id.beauty_btn);
//        mButtonBeauty.setOnClickListener(this);
//        mButtonSwitchCamera = (ImageButton) findViewById(R.id.qav_topbar_switchcamera);
//        mListViewMsgItems = (ListView) findViewById(R.id.im_msg_items);
//        mEditTextInputMsg = (EditText) findViewById(R.id.qav_bottombar_msg_input);
//        mBeautySettings = (LinearLayout) findViewById(R.id.qav_beauty_setting);
//        mBeautyConfirm = (TextView) findViewById(R.id.qav_beauty_setting_finish);
//        mBeautyConfirm.setOnClickListener(this);
//        mBottomBar = (FrameLayout) findViewById(R.id.qav_bottom_bar);
//        mBeautyBar = (SeekBar) (findViewById(R.id.qav_beauty_progress));
//        mBeautyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//                Log.d("SeekBar", "onStopTrackingTouch");
//                Toast.makeText(AvActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//                Log.d("SeekBar", "onStartTrackingTouch");
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,
//                                          boolean fromUser) {
//                // TODO Auto-generated method stub
//                mBeautyRate = progress;
//                mQavsdkControl.getAVContext().getVideoCtrl().inputBeautyParam(getBeautyProgress(progress));
//
//            }
//        });


//        mInviteMastk1 = (FrameLayout) findViewById(R.id.inviteMaskItem1);
//        mInviteMastk2 = (FrameLayout) findViewById(R.id.inviteMaskItem2);
//        mInviteMastk3 = (FrameLayout) findViewById(R.id.inviteMaskItem3);
//        mVideoHead1 = (ImageView) findViewById(R.id.inviteMaskHead1);
//        mVideoHead2 = (ImageView) findViewById(R.id.inviteMaskHead2);
//        mVideoHead3 = (ImageView) findViewById(R.id.inviteMaskHead3);
//        mInviteMastk1.setVisibility(View.GONE);
//        mInviteMastk2.setVisibility(View.GONE);
//        mInviteMastk3.setVisibility(View.GONE);

//        mEditTextInputMsg.setOnClickListener(this);

//        findViewById(R.id.qav_topbar_hangup).setOnClickListener(this);
        findViewById(R.id.qav_topbar_push).setOnClickListener(this);
        findViewById(R.id.qav_topbar_record).setOnClickListener(this);
        findViewById(R.id.qav_topbar_streamtype).setOnClickListener(this);
        if (!mSelfUserInfo.isCreater()) {
            findViewById(R.id.qav_topbar_push).setVisibility(View.GONE);
            findViewById(R.id.qav_topbar_streamtype).setVisibility(View.GONE);
            findViewById(R.id.qav_topbar_record).setVisibility(View.GONE);
        }
//        praiseLayout = (LinearLayout) findViewById(R.id.praise_layout);
//        mButtonSendMsg = (Button) findViewById(R.id.qav_bottombar_send_msg);
//        mButtonSendMsg.setOnClickListener(this);
//        mClockTextView = (TextView) findViewById(R.id.qav_timer);
//        mPraiseNum = (TextView) findViewById(R.id.text_view_live_praise);
//        mMemberListButton = (TextView) findViewById(R.id.btn_member_list);
//        mMemberListButton.setOnClickListener(this);
//        mButtonPraise = (ImageButton) findViewById(R.id.image_btn_praise);
//        mButtonPraise.setOnClickListener(this);

        if (mSelfUserInfo.isCreater()) {
//            mButtonMute.setOnClickListener(this);
//            mButtonSwitchCamera.setOnClickListener(this);
            AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();
//            avAudioCtrl.enableMic(false);
            avAudioCtrl.enableMic(true);
//            mButtonPraise.setEnabled(false);
        } else {
//            mButtonSwitchCamera.setOnClickListener(this);
            mQavsdkControl.getAVContext().getAudioCtrl().enableMic(false);
//            mPraiseNum.setText("" + praiseNum);
//            mButtonMute.setVisibility(View.GONE);
//            mButtonSwitchCamera.setVisibility(View.GONE);
        }

        //不熄屏
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TAG");

        //默认不显示键盘
        mInputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        findViewById(R.id.av_screen_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideMsgIputKeyboard();
//                mEditTextInputMsg.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mVideoTimer = new Timer(true);
        mHeartClickTimer = new Timer(true);


//        tvTipsMsg = (TextView) findViewById(R.id.qav_tips_msg);
//        tvTipsMsg.setTextColor(Color.GREEN);
//        tvShowTips = (TextView) findViewById(R.id.param_video);
//        tvShowTips.setOnClickListener(this);
//        timer.schedule(task, TIMER_INTERVAL, TIMER_INTERVAL);
    }

    public boolean hideMsgIputKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                mInputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                return true;
            }
        }

        return false;
    }

    class VideoOrientationEventListener extends OrientationEventListener {
        boolean mbIsTablet = false;

        public VideoOrientationEventListener(Context context, int rate) {
            super(context, rate);
            mbIsTablet = PhoneStatusTools.isTablet(context);
        }

        int mLastOrientation = -25;

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                mLastOrientation = orientation;
                return;
            }

            if (mLastOrientation < 0) {
                mLastOrientation = 0;
            }

            if (((orientation - mLastOrientation) < 20)
                    && ((orientation - mLastOrientation) > -20)) {
                return;
            }

            if (mbIsTablet) {
                orientation -= 90;
                if (orientation < 0) {
                    orientation += 360;
                }
            }
            mLastOrientation = orientation;

            if (orientation > 314 || orientation < 45) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(0);
                }
                mRotationAngle = 0;
            } else if (orientation > 44 && orientation < 135) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(90);
                }
                mRotationAngle = 90;
            } else if (orientation > 134 && orientation < 225) {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(180);
                }
                mRotationAngle = 180;
            } else {
                if (mQavsdkControl != null) {
                    mQavsdkControl.setRotation(270);
                }
                mRotationAngle = 270;
            }
        }
    }

    void registerOrientationListener() {
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new VideoOrientationEventListener(super.getApplicationContext(), SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_SURFACE_CREATED);
        intentFilter.addAction(Constants.ACTION_VIDEO_SHOW);
        intentFilter.addAction(Constants.ACTION_MEMBER_VIDEO_SHOW);
        intentFilter.addAction(Constants.ACTION_VIDEO_CLOSE);
        intentFilter.addAction(Constants.ACTION_ENABLE_CAMERA_COMPLETE);
        intentFilter.addAction(Constants.ACTION_SWITCH_CAMERA_COMPLETE);
        intentFilter.addAction(Constants.ACTION_INSERT_ROOM_TO_SERVER_COMPLETE);
        intentFilter.addAction(Constants.ACTION_INVITE_MEMBER_VIDEOCHAT);
        intentFilter.addAction(Constants.ACTION_MEMBER_CHANGE);
        intentFilter.addAction(Constants.ACTION_SHOW_VIDEO_MEMBER_INFO);
        intentFilter.addAction(Constants.ACTION_CLOSE_MEMBER_VIDEOCHAT);
        intentFilter.addAction(Constants.ACTION_CLOSE_ROOM_COMPLETE);
        registerReceiver(mBroadcastReceiver, intentFilter);

        IntentFilter netIntentFilter = new IntentFilter();
        netIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, netIntentFilter);
    }

    private void initTIMGroup() {
        Log.d(TAG, "initTIMGroup groupId" + groupId);
        if (groupId != null) {
            mConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);

            Log.d(TAG, "initTIMGroup mConversation" + mConversation);
        } else {

        }
        mSystemConversation = TIMManager.getInstance().getConversation(TIMConversationType.System, "");
//        testConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, "18602833226");
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mArrayListChatEntity, mMemberList, mSelfUserInfo);
//        mListViewMsgItems.setAdapter(mChatMsgListAdapter);
//        if (mListViewMsgItems.getCount() > 1)
//            mListViewMsgItems.setSelection(mListViewMsgItems.getCount() - 1);
//        mListViewMsgItems.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                hideMsgIputKeyboard();
//                mEditTextInputMsg.setVisibility(View.VISIBLE);
////                return false;
////            }
////        });
////
////        mListViewMsgItems.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
////                        if (view.getFirstVisiblePosition() == 0 && !mIsLoading && bMore) {
////                            bNeverLoadMore = false;
////                            mIsLoading = true;
////                            mLoadMsgNum += MAX_PAGE_NUM;
//////							getMessage();
////                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });
//        getMessage();

        TIMManager.getInstance().addMessageListener(msgListener);

        mChatTimer = new Timer(true);
        time = System.currentTimeMillis() / 1000;
        mChatTimerTask = new ChatTimerTask();
        mChatTimer.schedule(mChatTimerTask, 8000, 2000);
    }

    private void refreshCameraUI() {
        boolean isEnable = mQavsdkControl.getIsEnableCamera();
        boolean isFront = mQavsdkControl.getIsFrontCamera();
        boolean isInOnOffCamera = mQavsdkControl.getIsInOnOffCamera();
        boolean isInSwitchCamera = mQavsdkControl.getIsInSwitchCamera();


//        if (isInOnOffCamera) {
//            if (isEnable) {
//                Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, true);
//                Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, false);
//            } else {
//                Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, false);
////                Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, true);
//                Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, false);
//
//            }
//        } else {
//            Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, false);
//            Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, false);
//        }
//
//        if (isInSwitchCamera) {
//            if (isFront) {
//                Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, true);
//                Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, false);
//            } else {
//                Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, false);
//                Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, true);
//            }
//        } else {
//            Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, false);
//            Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, false);
//        }
    }

    private void locateCameraPreview() {
        if (mDialogInit != null && mDialogInit.isShowing()) {
            mDialogInit.dismiss();
        }
    }

    public void hostRequestView(String identifier) {
        Log.d(TAG, "request " + identifier);
        identifier = "86-" + identifier;
        AVEndpoint endpoint = null;
        if (mQavsdkControl != null && mQavsdkControl.getAVContext() != null && mQavsdkControl.getAVContext().getRoom() != null) {
            endpoint = ((AVRoomMulti) mQavsdkControl.getAVContext().getRoom()).getEndpointById(identifier);
        }
        Log.d(TAG, "hostRequestView identifier " + identifier + " endpoint " + endpoint);
        if (endpoint != null) {
            mVideoTimer = new Timer(true);
            mVideoTimerTask = new VideoTimerTask();
            mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
            AVView view = new AVView();
            view.videoSrcType = AVView.VIDEO_SRC_TYPE_CAMERA;//SDK1.2版本只支持摄像头视频源，所以当前只能设置为VIDEO_SRC_TYPE_CAMERA。
            view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;


            //界面数
            mRequestViewList[0] = view;
            mRequestIdentifierList[0] = identifier;
            mRequestViewList[0].viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
            AVEndpoint.requestViewList(mRequestIdentifierList, mRequestViewList, 1, mRequestViewListCompleteCallback);
            //成员模式请求界面
            sendBroadcast(new Intent(Constants.ACTION_VIDEO_SHOW)
                    .putExtra(Constants.EXTRA_IDENTIFIER, identifier)
                    .putExtra(Constants.EXTRA_VIDEO_SRC_TYPE, view.videoSrcType));

        } else {
//            mEditTextInputMsg.setVisibility(View.GONE);
//            mButtonSendMsg.setVisibility(View.GONE);
//            mPraiseNum.setVisibility(View.GONE);
//            mButtonPraise.setVisibility(View.GONE);

//            dialog = new Dialog(this, R.style.dialog);
//            dialog.setContentView(R.layout.alert_dialog);
//            ((TextView) dialog.findViewById(R.id.dialog_title)).setText("温馨提示");
//            ((TextView) dialog.findViewById(R.id.dialog_message)).setText("此直播已结束，请观看其他直播！");
//            ((Button) dialog.findViewById(R.id.close_dialog)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onCloseVideo();
//                    dialog.dismiss();
//                }
//            });
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        }
    }

    public void requestMultiView(String identifier) {
        Log.d(TAG, "requestMultiView " + identifier + "  mMemberVideoCount  ");
        int mMemberVideoCount = mQavsdkControl.getSmallVideoView();
        identifier = "86-" + identifier;
        AVEndpoint endpoint = ((AVRoomMulti) mQavsdkControl.getAVContext().getRoom()).getEndpointById(identifier);
        if (endpoint != null) {
            //界面参数
            AVView view = new AVView();
            view.videoSrcType = AVView.VIDEO_SRC_TYPE_CAMERA;
            view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;

//            Log.d(TAG, "requestMultiView  " + identifier + "  mMemberVideoCount  " + mMemberVideoCount);
//            for (int i = 0; i < mMemberVideoCount; i++) {
//                mRequestViewList[i].viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
//            }

            //界面参数
            mRequestViewList[mMemberVideoCount] = view;
            mRequestIdentifierList[mMemberVideoCount] = identifier;

            //请求次数
            mMemberVideoCount++;
            if (mMemberVideoCount > 3) {
                Toast.makeText(this, "requestCount cannot pass  4", Toast.LENGTH_LONG);
                return;
            }
            mQavsdkControl.setRequestCount(mMemberVideoCount);


            Log.d(TAG, "requestMultiView identifier " + identifier + " mMemberVideoCount " + mMemberVideoCount);
            AVEndpoint.requestViewList(mRequestIdentifierList, mRequestViewList, mMemberVideoCount, mRequestViewListCompleteCallback);


//            endpoint.hostRequestView(view, mRequestViewCompleteCallback);
            //成员模式请求界面
            this.sendBroadcast(new Intent(Constants.ACTION_MEMBER_VIDEO_SHOW)
                    .putExtra(Constants.EXTRA_IDENTIFIER, identifier)
                    .putExtra(Constants.EXTRA_VIDEO_SRC_TYPE, view.videoSrcType));
        } else {
//            mEditTextInputMsg.setVisibility(View.GONE);
//            mButtonSendMsg.setVisibility(View.GONE);
//            mPraiseNum.setVisibility(View.GONE);
//            mButtonPraise.setVisibility(View.GONE);

//            dialog = new Dialog(this, R.style.dialog);
//            dialog.setContentView(R.layout.alert_dialog);
//            ((TextView) dialog.findViewById(R.id.dialog_title)).setText("温馨提示");
//            ((TextView) dialog.findViewById(R.id.dialog_message)).setText("此直播已结束，请观看其他直播！");
//            ((Button) dialog.findViewById(R.id.close_dialog)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onCloseVideo();
//                    dialog.dismiss();
//                }
//            });
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        }
    }


    private void joinGroup() {
        TIMGroupManager.getInstance().applyJoinGroup(groupId, "申请加入" + groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
//                TIMManager.getInstance().logout();
                Toast.makeText(LiveActivity.this, "加群失败,失败原因：" + i + ":" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "applyJpoinGroup success");
            }
        });
    }


    /**
     * 发一条消息告诉大家 自己上线了
     */
    private void onMemberEnter() {
        mQavsdkApplication.enterPlusPlus();
        if (mSelfUserInfo.getUserName() == null) {
            mSelfUserInfo.setUserName("null");
        }
        if (mSelfUserInfo.getHeadImagePath().equals("")) {
            mSelfUserInfo.setHeadImagePath("null");
        }
        String message = mSelfUserInfo.getUserPhone() + "&"
                + MEMBER_ENTER_MSG + "&"
                + mSelfUserInfo.getUserName() + "&"
                + mSelfUserInfo.getHeadImagePath() + "&" + "inedex: " + mQavsdkApplication.getEnterIndex() + "&";


        Log.d(TAG, "onMemberEnter " + message);
        TIMMessage Tim = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(message.getBytes());
        elem.setDesc(UNREAD);
        if (1 == Tim.addElement(elem))
            Toast.makeText(getApplicationContext(), "enter error", Toast.LENGTH_SHORT).show();
        else {

            mConversation.sendMessage(Tim, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "enter error" + i + ": " + s);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    TIMCustomElem elem = (TIMCustomElem) (timMessage.getElement(0));
                    try {
                        String text = new String(elem.getData(), "utf-8");
                        Log.i(TAG, "msgSystem send groupmsg enter  success :" + text);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        final String msg = "轻轻地“" + mSelfUserInfo.getUserName() + "”来了";
        if (msg.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendText(msg);
                }
            }).start();
        }
    }

    private void sendText(String msg) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                mHandler.sendEmptyMessage(ERROR_MESSAGE_TOO_LONG);
                return;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        TIMMessage Nmsg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(msg);
        if (Nmsg.addElement(elem) != 0) {
            return;
        }
        mConversation.sendMessage(Nmsg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (i == 85) {
                    mHandler.sendEmptyMessage(ERROR_MESSAGE_TOO_LONG);
                } else if (i == 6011) {
                    mHandler.sendEmptyMessage(ERROR_ACCOUNT_NOT_EXIT);
                }
                Log.e(TAG, "send message failed. code: " + i + " errmsg: " + s);
//                getMessage();
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.i(TAG, "Send text Msg ok");
                Message msg = new Message();
                msg.what = REFRESH_CHAT;
                msg.obj = timMessage;
                mHandler.sendMessage(msg);

//                showTextMessage(timMessage);
//                getMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mSelfUserInfo.isCreater() == true)
//            if (mQavsdkControl.getIsEnableCamera() == false) {
////                mQavsdkControl.toggleEnableCamera();
//                OpenVideo = true;
//
//            }
        AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();
        avAudioCtrl.enableMic(true);
        OpenVoice = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPaused = false;
        LEVAE_MODE = false;
        mQavsdkControl.onResume();
        Log.i(TAG, "onResume switchCamera!! ");
        refreshCameraUI();

        if (mOnOffCameraErrorCode != AVError.AV_OK) {
            showDialog(DIALOG_ON_CAMERA_FAILED);
        }
//        startOrientationListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
        mQavsdkControl.onPause();
        Log.i(TAG, "onPause switchCamera!! ");
        refreshCameraUI();
        if (mOnOffCameraErrorCode != AVError.AV_OK) {
            showDialog(DIALOG_OFF_CAMERA_FAILED);
        }
//        stopOrientationListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭摄像头
//        if (mQavsdkControl.getIsEnableCamera() == true) {
////            mQavsdkControl.toggleEnableCamera();
////            OpenVideo = false;
//        }
        //关闭Mic
//        mQavsdkControl.setRequestCount(0);
        AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();
        avAudioCtrl.enableMic(false);
        OpenVoice = false;
        mQavsdkApplication.setHandleMemberRoomSuccess(false);
        hasPullMemberList = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMaskViewCount = 0;
        mQavsdkControl.onDestroy();
        // 注销广播
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }

        Log.d(TAG, "WL_DEBUG onDestroy");
//        Util.switchWaitingDialog(ctx, mDialogAtDestroy, DIALOG_DESTROY, false);
    }

    public MemberInfo findMemberInfo(ArrayList<MemberInfo> list, String id) {
        Log.d(TAG, "findMemberInfo id" + id);
        String identifier = "";
        if (id.startsWith("86-")) {
            identifier = id.substring(3);
        } else {
            identifier = id;
        }
        Log.d(TAG, "findMemberInfo identifier " + identifier);
        for (MemberInfo member : list) {

            if (member.getUserPhone().equals(identifier))
                return member;
        }
        return null;
    }

    public void upMemberLevel(String identifier) {
        MemberInfo upMember = findMemberInfo(mNormalMemberList, identifier);
        mVideoMemberList.add(upMember);
        mNormalMemberList.remove(upMember);
//        mMemberListDialog.refreshMemberData(mNormalMemberList, mVideoMemberList);
    }

    public void downMemberLevel(String identifier) {
        MemberInfo upMember = findMemberInfo(mVideoMemberList, identifier);
        mNormalMemberList.add(upMember);
        mVideoMemberList.remove(upMember);
//        mMemberListDialog.refreshMemberData(mNormalMemberList, mVideoMemberList);
    }

    public ArrayList<MemberInfo> copyToNormalMember() {
        mNormalMemberList = new ArrayList<MemberInfo>();
        for (MemberInfo member : mMemberList) {
            mNormalMemberList.add(member);
        }
        return mNormalMemberList;
    }

    private void closeVideoMemberByHost(String identifer) {
        viewIndexRemove(identifer);
        sendCloseVideoMsg(identifer);
        mQavsdkControl.closeMemberView(identifer);
        downMemberLevel(identifer);
    }

    private void viewIndexRemove(String identifer) {
        if (viewIndex != null) {
            String id;
            if (identifer.startsWith("86-")) {
                id = identifer.substring(3);

            } else {
                id = identifer;
            }
            if (viewIndex.containsKey(id)) {
                viewIndex.remove(id);
            }

        }

    }

    private void sendCloseVideoMsg(String inviteIdentifier) {

        mQavsdkApplication.enterPlusPlus();
        if (mSelfUserInfo.getUserName() == null) {
            mSelfUserInfo.setUserName("null");
        }
        if (mSelfUserInfo.getHeadImagePath().equals("")) {
            mSelfUserInfo.setHeadImagePath("null");
        }
        String message = mSelfUserInfo.getUserPhone() + "&"
                + CLOSEVIDEOSEND + "&"
                + mSelfUserInfo.getUserName() + "&"
                + inviteIdentifier + "&";


        Log.d(TAG, "inviteVC sendVCInvitation " + message);
        TIMMessage Tim = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(message.getBytes());
        elem.setDesc(UNREAD);
        if (1 == Tim.addElement(elem))
            Toast.makeText(getApplicationContext(), "enter error", Toast.LENGTH_SHORT).show();
        else {
            if (!inviteIdentifier.startsWith("86-"))
                inviteIdentifier = "86-" + inviteIdentifier;
            testConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, inviteIdentifier);
            testConversation.sendMessage(Tim, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "enter error" + i + ": " + s);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    TIMCustomElem elem = (TIMCustomElem) (timMessage.getElement(0));
                    try {
                        String text = new String(elem.getData(), "utf-8");
                        Log.i(TAG, "inviteVC sendVCInvitation send groupmsg enter  success :" + text);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateMemberView() {
//        Log.d(TAG, "IMGroupSystem updateMemberView memberNum " + mVideoMemberList.size());
//        if (mMemberListDialog != null)
//            mMemberListDialog.refreshMemberData(mNormalMemberList, mVideoMemberList);
//        mMemberListButton.setText("" + mMemberList.size());
//        if (hostMember != null) {
//            String headurl = HttpUtil.rootUrl + "?imagepath=" + hostMember.getHeadImagePath() + "&width=0&height=0";
//            imageLoader.displayImage(headurl, hostHead);
//        }

    }
}
