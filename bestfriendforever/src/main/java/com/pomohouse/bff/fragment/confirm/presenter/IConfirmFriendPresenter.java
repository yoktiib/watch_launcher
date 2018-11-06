package com.pomohouse.bff.fragment.confirm.presenter;

import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 8/29/16 AD.
 */
public interface IConfirmFriendPresenter extends IBaseRequestStatePresenter{
    void requestAcceptFriend();

    void requestCancelFriend();
}
