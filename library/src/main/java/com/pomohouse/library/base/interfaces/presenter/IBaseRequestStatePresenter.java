package com.pomohouse.library.base.interfaces.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Admin on 4/21/2016 AD.
 */
public interface IBaseRequestStatePresenter extends IBaseInteractorListener {

    void onSaveInstanceState(@NonNull Bundle outState);

    void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

}
