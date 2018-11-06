package com.pomohouse.launcher.fragment.about;

import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.library.base.interfaces.IBaseRequestView;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface IPinCodeView extends IBaseRequestView {
    void onGetPinSuccess(PinCodeModel readyModel);

    void enableGetCodeButton();

    void disableGetCodeButton();
}
