package com.showworld.living.presenters.viewinface;


/**
 * 登录回调
 */
public interface LoginView extends MvpView{

    void loginSucc();

    void loginFail();
}
