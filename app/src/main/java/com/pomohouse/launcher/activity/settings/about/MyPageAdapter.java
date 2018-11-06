package com.pomohouse.launcher.activity.settings.about;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pomohouse.component.viewpagerindicator.IconPagerAdapter;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.fragment.about.AboutInfoFragment;
import com.pomohouse.launcher.fragment.about.PinCodeFragment;
import com.pomohouse.launcher.fragment.about.QRCodeFragment;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class MyPageAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    private final int[] ICON_INDICATOR = {R.drawable.ic_launcher
            , R.drawable.ic_launcher};

    MyPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getIconResId(int index) {
        return ICON_INDICATOR[index];
    }

    public int getCount() {
        return 3;
    }

    public Fragment getItem(int position) {
        if (position == 0)
            return QRCodeFragment.newInstance();
        else if (position == 1)
            return PinCodeFragment.newInstance();
        else
            return AboutInfoFragment.newInstance();
    }
}
