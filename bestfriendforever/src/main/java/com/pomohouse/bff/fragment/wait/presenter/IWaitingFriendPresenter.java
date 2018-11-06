package com.pomohouse.bff.fragment.wait.presenter;

import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 8/29/16 AD.
 */
public interface IWaitingFriendPresenter extends IBaseRequestStatePresenter{
    void requestStatusAnswerFriend(String IMEI);
    void stopRequestResponseFriend();
}
