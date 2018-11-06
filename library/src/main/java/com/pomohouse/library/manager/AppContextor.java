package com.pomohouse.library.manager;

import android.content.Context;

/**
 * Created by nuuneoi on 12/6/14 AD.
 */
public class AppContextor {

    private static AppContextor instance;

    public static AppContextor getInstance() {
        if (instance == null)
            instance = new AppContextor();
        return instance;
    }

    private Context mContext;
    private AppContextor() {

    }

    public void initContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

}
