package com.pomohouse.library.base.interfaces;

import android.support.annotation.CallSuper;

import com.pomohouse.library.networks.ProgressDialogLoader;


/**
 * Created by sirawit on 4/24/2016 AD.
 */
public interface IBaseRequestView {
    @CallSuper
    void displayLoadingDialog();

    @CallSuper
    void displayLoadingDialog(ProgressDialogLoader.OnCancelClickListener listener);

    @CallSuper
    void hideLoadingDialog();

    @CallSuper
    void hideKeyboard();
}
