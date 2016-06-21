package com.showworld.living.presenters.viewinface;

import com.showworld.living.model.MemberInfo;

import java.util.ArrayList;


/**
 * 成员列表回调
 */
public interface MembersDialogView extends MvpView {

    void showMembersList(ArrayList<MemberInfo> data);

}
