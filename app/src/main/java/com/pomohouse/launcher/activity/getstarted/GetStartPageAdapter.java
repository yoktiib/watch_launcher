package com.pomohouse.launcher.activity.getstarted;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pomohouse.component.viewpagerindicator.IconPagerAdapter;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedInfoInstallAppFragment;
import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedInfoQRCodeFragment;
import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedPinCodeFragment;
import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedQRCodeFragment;
import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedToHomeFragment;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class GetStartPageAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    private final int[] ICON_INDICATOR = {R.drawable.ic_launcher
            , R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};

    GetStartPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getIconResId(int index) {
        return ICON_INDICATOR[index];
    }

    public int getCount() {
        return 5;
    }

    public Fragment getItem(int position) {
        if (position == 0)
            return GetStartedInfoInstallAppFragment.newInstance();
        else if (position == 1)
            return GetStartedInfoQRCodeFragment.newInstance();
        else if (position == 2)
            return GetStartedQRCodeFragment.newInstance();
        else if (position == 3)
            return GetStartedPinCodeFragment.newInstance();
        else
            return GetStartedToHomeFragment.newInstance();
    }
}
