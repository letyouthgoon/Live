package com.showworld.live.main.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.showworld.live.R;
import com.showworld.live.SWLApplication;
import com.showworld.live.base.ActionCallbackListener;
import com.showworld.live.base.ui.TActivity;
import com.showworld.live.main.Constants;
import com.showworld.live.main.module.GetMemberInfoRet;
import com.showworld.live.main.module.UserInfo;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.bugly.imsdk.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSUserInfo;


/**
 * 登录界面
 */
public class LoginActivity extends TActivity implements TextWatcher, View.OnClickListener {
    public static String TAG = "LoginActivity";

    public final static int ERROR_CLEAN = 0;
    public final static int ERROR_LOGIN_FAILE = -1;
    public final static int ERROR_EMPTY_INPUT = -2;
    public final static int ERROR_INTERNET = -3;
    public final static int ERROR_EMPTY_SIG = -4;
    private SharedPreferences mSharedPrefer;
    private EditText mUserAccountEditText;
    private TextView mLoginTipsTextView;
    private Button mButtonLogin;
    private Button mButtonSmsLogin;
    private UserInfo mSelfUserInfo;
    //   private String userphone;
    private boolean isFormal = true;
    private int accType = Integer.parseInt(Constants.ACCOUNTTYPE);
    private int sdkAppid = Constants.APPID;
    private String appVer = "1.0";
    private TLSLoginHelper loginHelper;
    private TLSSmsLoginListener smsLoginListener;
    private MyCount mc;

    private Handler mErrorHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR_CLEAN:
                    mLoginTipsTextView.setText("");
                    break;
                case ERROR_LOGIN_FAILE:
                    mLoginTipsTextView.setText(getResources().getString(R.string.login_error_login_failed));
                    break;
                case ERROR_EMPTY_INPUT:
                    mLoginTipsTextView.setText(getResources().getString(R.string.login_error_empty_input));
                    break;
                case ERROR_INTERNET:
                    mLoginTipsTextView.setText(getResources().getString(R.string.login_net_error));
                    break;
                case ERROR_EMPTY_SIG:
                    mLoginTipsTextView.setText(getResources().getString(R.string.login_sms_password_empty));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelfUserInfo = ((SWLApplication) getApplication()).getMyselfUserInfo();
        mSelfUserInfo.setEnv(Constants.ENV_FORMAL);
        isFormal = true;
        SharedPreferences sharedata = getApplicationContext().getSharedPreferences(Constants.LOCAL_DATA, Context.MODE_APPEND);
        String cachePhone = sharedata.getString(Constants.LOCAL_PHONE, "");
        String cacheUserSig = sharedata.getString(Constants.LOCAL_USERSIG, "");


//        setAppId(DemoConstants.APPID, DemoConstants.ACCOUNTTYPE);
//        TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
//        TIMManager.getInstance().init(getApplicationContext());
        getlistener();
        accType = Integer.parseInt(Constants.ACCOUNTTYPE);
        sdkAppid = Constants.APPID;


//        Log.v("cola", "data=" + data);

        //如果已经登录了，直接跳过登录界面，否则显示登录界面
        if (!cachePhone.equals("")) {
            mSelfUserInfo.setUserPhone(cachePhone);
            String userName = sharedata.getString(Constants.LOCAL_USERNAME, "");
            CrashReport.setUserId(mSelfUserInfo.getUserPhone());

            int sex = sharedata.getInt(Constants.LOCAL_SEX, -1);
            String constellation = sharedata.getString(Constants.LOCAL_CONSTELLATION, "null");
            int praiseCount = sharedata.getInt(Constants.LOCAL_PRAISECOUNT, -1);
            String signature = sharedata.getString(Constants.LOCAL_SIGNATURE, "");
            String addr = sharedata.getString(Constants.LOCAL_ADDR, "");
            String headImagePath = sharedata.getString(Constants.LOCAL_HEAD_IMAGE_PATH, "");
            int env = sharedata.getInt(Constants.LOCAL_ENV, 0);
            mSelfUserInfo.setUserPhone(cachePhone);
            if (!cacheUserSig.equals("")) {
                Log.d(TAG, "onCreate cachePhone " + cachePhone + " cacheSig         " + cacheUserSig);
                Log.d(TAG, "onCreate sex " + sex + " userName   " + userName);
                Log.d(TAG, "onCreate headImagePath " + headImagePath + " userName   " + userName);
                Log.d(TAG, "onCreate headImagePath " + CrashReport.getUserId());

                mSelfUserInfo.setUsersig(cacheUserSig);
                mSelfUserInfo.setUserName(userName);
                CrashReport.setUserId(mSelfUserInfo.getUserPhone());
                mSelfUserInfo.setUserSex(sex);
                mSelfUserInfo.setUserConstellation(constellation);
                mSelfUserInfo.setPraiseCount(praiseCount);
                mSelfUserInfo.setUserSignature(signature);
                mSelfUserInfo.setUserAddr(addr);
                mSelfUserInfo.setHeadImagePath(headImagePath);
                mSelfUserInfo.setEnv(env);

            }
            Log.d(TAG, "onCreate env " + mSelfUserInfo.getEnv());
            TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
            TIMManager.getInstance().init(getApplicationContext());
            startActivity(new Intent(LoginActivity.this, LiveListActivity.class));
            finish();
        } else {
            setContentView(R.layout.login_activity);
//            isFormal = true;
            //默认正式环境
//            mSelfUserInfo.setEnv(Util.ENV_FORMAL);
            initViewUI();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                onLogin();
                break;
            case R.id.btn_register:
                TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
                TIMManager.getInstance().init(getApplicationContext());
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.smslogin:
                //获取验证短信
                //申请的APP_Id,类型
//                setAppId(DemoConstants.APPID, DemoConstants.ACCOUNTTYPE);
                TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
                TIMManager.getInstance().init(getApplicationContext());
                getlistener();
                accType = Integer.parseInt(Constants.ACCOUNTTYPE);
                sdkAppid = Constants.APPID;
                loginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), sdkAppid, accType, appVer);
                smsLogin();
                break;
            default:
                break;
        }
    }

//    public void setAppId(int id, String type) {
//        Util.APP_ID_TEXT = id;
//        Util.UID_TYPE = type;
//    }

    public void onEnv(View v) {
        if (!isFormal) {
            mSelfUserInfo.setEnv(Constants.ENV_FORMAL);
            ((Button) v).setText(getResources().getString(R.string.formal_environment));
            isFormal = true;
        } else {
            mSelfUserInfo.setEnv(Constants.ENV_TEST);
            ((Button) v).setText(getResources().getString(R.string.test_environment));
            isFormal = false;
        }
//        TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
//        TIMManager.getInstance().init(getApplicationContext());
    }


    /**
     * 短信获取验证码
     */
    public void smsLogin() {
        String telphone = "86-" + mUserAccountEditText.getText().toString().trim();
        loginHelper.TLSSmsLoginAskCode(telphone, smsLoginListener);
    }


    /**
     * 登录
     */
    public void onLogin() {
        String sig = ((TextView) findViewById(R.id.login_smssig)).getText().toString().trim();
        if (sig.length() == 0) {
            mErrorHandler.sendEmptyMessage(ERROR_EMPTY_SIG);
            return;
        }
        Log.d(TAG, "TLS sms 提交验证短信 ");
        loginHelper.TLSSmsLoginVerifyCode(sig, smsLoginListener);
    }


    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mErrorHandler.sendEmptyMessage(ERROR_CLEAN);
            findViewById(R.id.btn_login).setClickable(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_SHORT).show();
                mUserAccountEditText.setText(data.getExtras().getString(Constants.EXTRA_USER_PHONE));
                String telphone = "86-" + mUserAccountEditText.getText().toString().trim();

                if (loginHelper == null) {
                    loginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), sdkAppid, accType, appVer);
                }
                loginHelper.TLSSmsLogin(telphone, smsLoginListener);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化UI
     */
    private void initViewUI() {
        mUserAccountEditText = (EditText) findViewById(R.id.login_account);
        mLoginTipsTextView = (TextView) findViewById(R.id.login_tips);
        mButtonLogin = (Button) findViewById(R.id.btn_login);
        mButtonLogin.setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        mButtonSmsLogin = (Button) findViewById(R.id.smslogin);
        mButtonSmsLogin.setOnClickListener(this);
        mSharedPrefer = getSharedPreferences("hjlogin", Context.MODE_PRIVATE);
        mUserAccountEditText.setText(mSharedPrefer.getString(Constants.EXTRA_USER_PHONE, ""));
        ((EditText) findViewById(R.id.login_smssig)).addTextChangedListener(this);
    }

    private void setUserInfo(JSONObject jobject, String phone, String sig) {
        JSONObject userInfo = null;
        try {
            //if (mSelfUserInfo.getLoginType() == Util.INTEGERATE)
            //userInfo = jobject.getJSONObject(Util.JSON_KEY_USER_INFO);
            //else
            userInfo = jobject;
            String name = userInfo.getString(Constants.EXTRA_USER_NAME);


            mSelfUserInfo.setUserName(userInfo.getString(Constants.EXTRA_USER_NAME));
            mSelfUserInfo.setUserSex(userInfo.getInt(Constants.EXTRA_SEX));
            mSelfUserInfo.setUserConstellation(userInfo.getString(Constants.EXTRA_CONSTELLATION));
            mSelfUserInfo.setPraiseCount(userInfo.getInt(Constants.EXTRA_PRAISE_NUM));
            mSelfUserInfo.setUserSignature(userInfo.getString(Constants.EXTRA_SIGNATURE));
            mSelfUserInfo.setUserAddr(userInfo.getString(Constants.EXTRA_ADDRESS));

            mSelfUserInfo.setHeadImagePath(userInfo.getString(Constants.EXTRA_HEAD_IMAGE_PATH));

            SharedPreferences.Editor sharedata = getApplicationContext().getSharedPreferences(Constants.LOCAL_DATA, Context.MODE_APPEND).edit();
            sharedata.putString(Constants.LOCAL_PHONE, phone);
            sharedata.putString(Constants.LOCAL_USERSIG, sig);
            sharedata.putString(Constants.LOCAL_USERNAME, name);
            sharedata.putInt(Constants.LOCAL_SEX, userInfo.getInt(Constants.EXTRA_SEX));
            sharedata.putString(Constants.LOCAL_CONSTELLATION, userInfo.getString(Constants.EXTRA_CONSTELLATION));
            sharedata.putInt(Constants.LOCAL_PRAISECOUNT, userInfo.getInt(Constants.EXTRA_PRAISE_NUM));
            sharedata.putString(Constants.LOCAL_SIGNATURE, userInfo.getString(Constants.EXTRA_SIGNATURE));
            sharedata.putString(Constants.LOCAL_ADDR, userInfo.getString(Constants.EXTRA_ADDRESS));
            sharedata.putString(Constants.LOCAL_HEAD_IMAGE_PATH, userInfo.getString(Constants.EXTRA_HEAD_IMAGE_PATH));
            sharedata.putInt(Constants.LOCAL_ENV, mSelfUserInfo.getEnv());
            sharedata.commit();

            Log.e(TAG, "setUserInfo username : " + name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initImageDir() {
        File sd = Environment.getExternalStorageDirectory();
        String image = sd.getPath() + "/image";
        File file = new File(image);
        if (!file.exists())
            file.mkdir();
    }

    /**
     * 短信回调接口
     */
    public void getlistener() {
        smsLoginListener = new TLSSmsLoginListener() {
            @Override
            public void OnSmsLoginAskCodeSuccess(int i, int i1) {
                mc = new MyCount(i * 1000 * 2, 1000);
                mc.start();
                Toast.makeText(LoginActivity.this, "成功获取验证码", Toast.LENGTH_SHORT).show();
                mButtonSmsLogin.setClickable(false);/**/
                mButtonLogin.setEnabled(true);
                mButtonLogin.setClickable(false);
            }

            @Override
            public void OnSmsLoginReaskCodeSuccess(int i, int i1) {
            }

            @Override
            public void OnSmsLoginVerifyCodeSuccess() {
                String telphone = "86-" + mUserAccountEditText.getText().toString().trim();
                Toast.makeText(LoginActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                Log.d(TAG, "OnSmsLoginVerifyCodeSuccess TLS login");
                loginHelper.TLSSmsLogin(telphone, smsLoginListener);
            }

            @Override
            public void OnSmsLoginSuccess(TLSUserInfo tlsUserInfo) {
                //获取个人ID
                String UserSig = loginHelper.getUserSig(tlsUserInfo.identifier);
                mSelfUserInfo.setUsersig(UserSig);
                getUserInfo(UserSig);
            }

            @Override
            public void OnSmsLoginFail(TLSErrInfo tlsErrInfo) {
                Toast.makeText(LoginActivity.this, "fail " + tlsErrInfo.ErrCode + ":" + tlsErrInfo.Title + "  " + tlsErrInfo.Msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnSmsLoginTimeout(TLSErrInfo tlsErrInfo) {
                Toast.makeText(LoginActivity.this, "timeout " + tlsErrInfo.ErrCode + ":" + tlsErrInfo.Title + "  " + tlsErrInfo.Msg, Toast.LENGTH_LONG).show();
            }
        };
    }

    /**
     * 同步用户信息到服务器
     *
     * @param Usersig
     */
    public void getUserInfo(final String Usersig) {
        appAction.login(mUserAccountEditText.getText().toString(), new ActionCallbackListener<GetMemberInfoRet>() {
            @Override
            public void onSuccess(GetMemberInfoRet bean) {
                mSelfUserInfo.setUserPhone(mUserAccountEditText.getText().toString().trim());
                mSelfUserInfo.login(getApplicationContext(), mSelfUserInfo.getUserPhone());
                CrashReport.setUserId(mSelfUserInfo.getUserPhone());
                JSONObject sigobj = new JSONObject();
                try {
                    sigobj.put("userphone", bean.data.userphone);
                    sigobj.put("username", bean.data.username);
                    sigobj.put("sex", "0");
                    sigobj.put("constellation", "");
                    sigobj.put("headimagepath", "");
                    sigobj.put("address", "");
                    sigobj.put("signature", "");
                    sigobj.put("praisenum", 106);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSelfUserInfo.setUsersig(Usersig);
                setUserInfo(sigobj, mUserAccountEditText.getText().toString().trim(), Usersig);
                TIMManager.getInstance().setEnv(mSelfUserInfo.getEnv());
                TIMManager.getInstance().setLogLevel(TIMLogLevel.DEBUG);
                TIMManager.getInstance().init(getApplicationContext());
                startActivity(new Intent(LoginActivity.this, LiveListActivity.class));
                initImageDir();
                finish();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                mErrorHandler.sendEmptyMessage(ERROR_LOGIN_FAILE);
            }
        });
    }

    /**
     * 倒计时控件
     */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mButtonSmsLogin.setClickable(true);
            mButtonLogin.setEnabled(false);
            ((Button) findViewById(R.id.smslogin)).setText(getResources().getString(R.string.btn_get_sms_password));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mButtonSmsLogin.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
