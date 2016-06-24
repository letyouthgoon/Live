package com.showworld.living.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.showworld.living.R;
import com.showworld.living.model.MySelfInfo;
import com.showworld.living.presenters.LoginHelper;
import com.showworld.living.presenters.viewinface.LoginView;
import com.showworld.living.utils.SwlLog;
import com.showworld.living.views.customviews.BaseActivity;

/**
 * 登录类
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginView {
    TextView mBtnLogin, mBtnRegister;
    EditText mPassWord, mUserName;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginHelper mLoginHeloper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwlLog.i(TAG, "LoginActivity onCreate");
        mLoginHeloper = new LoginHelper(this, this);
        //获取个人数据本地缓存
        MySelfInfo.getInstance().getCache(getApplicationContext());
        if (MySelfInfo.getInstance().needLogin()) {
            //本地没有账户需要登录
            setContentView(R.layout.activity_independent_login);
            mBtnLogin = (TextView) findViewById(R.id.btn_login);
            mUserName = (EditText) findViewById(R.id.username);
            mPassWord = (EditText) findViewById(R.id.password);
            mBtnRegister = (TextView) findViewById(R.id.registerNewUser);
            mBtnRegister.setOnClickListener(this);
            mBtnLogin.setOnClickListener(this);
        } else {
            //有账户登录直接IM登录
            SwlLog.i(TAG, "LoginActivity onCreate");
            mLoginHeloper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    @Override
    protected void onDestroy() {
        mLoginHeloper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerNewUser) {
            RegisterActivity.start(this);
            finish();
        }
        if (view.getId() == R.id.btn_login) {
            //登录账号系统TLS
            if (TextUtils.isEmpty(mUserName.getText())) {
                Toast.makeText(LoginActivity.this, "name can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPassWord.getText())) {
                Toast.makeText(LoginActivity.this, "password can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            mLoginHeloper.tlsLogin(mUserName.getText().toString(), mPassWord.getText().toString());
        }
    }

    @Override
    public void loginSucc() {
        Toast.makeText(LoginActivity.this, "" + MySelfInfo.getInstance().getId() + " login ", Toast.LENGTH_SHORT).show();
        HomeActivity.start(this);
        finish();
    }

    @Override
    public void loginFail() {

    }
}
