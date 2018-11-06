package com.pomohouse.library.manager;

import android.app.Activity;
import android.content.Context;

/**
 * Created by nuuneoi on 12/6/14 AD.
 */
public class ActivityContextor {

    private static ActivityContextor instance;

    private Activity mActivity;
    private Context mContext;
    public static ActivityContextor getInstance() {
        if (instance == null)
            instance = new ActivityContextor();
        return instance;
    }

    private ActivityContextor() {

    }

    public void initActivity(Activity activity) {
        mActivity = activity;
    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public Activity getActivity() {
        return mActivity;
    }
}
