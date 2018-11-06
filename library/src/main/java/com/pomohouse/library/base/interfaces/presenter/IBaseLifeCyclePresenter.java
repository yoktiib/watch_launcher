package com.pomohouse.library.base.interfaces.presenter;

/**
 * Created by sirawit on 4/23/2016 AD.
 */
public interface IBaseLifeCyclePresenter {

    void onInitial(Object... param);

    void onDestroy();
}
