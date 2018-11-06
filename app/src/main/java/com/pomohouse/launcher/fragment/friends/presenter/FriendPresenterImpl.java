package com.pomohouse.launcher.fragment.friends.presenter;

import com.pomohouse.launcher.fragment.friends.IFriendView;
import com.pomohouse.launcher.fragment.friends.interactor.IFriendInteractor;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class FriendPresenterImpl implements IFriendPresenter {
    IFriendView view;
    IFriendInteractor interactor;

    public FriendPresenterImpl(IFriendView view, IFriendInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }
}
