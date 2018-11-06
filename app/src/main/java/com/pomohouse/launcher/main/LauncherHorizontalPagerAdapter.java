package com.pomohouse.launcher.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pomohouse.launcher.fragment.contacts.ContactFragment;
import com.pomohouse.launcher.fragment.main.MainFragment;
import com.pomohouse.launcher.fragment.menu.MenuFragment;

/**
 * Created by anupamchugh on 26/12/15.
 */
public class LauncherHorizontalPagerAdapter extends FragmentPagerAdapter {
    private ContactFragment contactFragment;

    private MainFragment mainFragment;

    public ContactFragment getContactFragment() {
        return contactFragment;
    }
    public MainFragment getMainFragment() {
        return mainFragment;
    }

    public LauncherHorizontalPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return contactFragment = ContactFragment.newInstance();
            case 1:
               mainFragment =  MainFragment.newInstance();
//                return MainFragment.newInstance();
                return mainFragment;
            case 2:
                return MenuFragment.newInstance();
        }
        //BusProvider.getInstance().post(position);
        return MainFragment.newInstance();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 3;
    }
}