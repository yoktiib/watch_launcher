package com.pomohouse.launcher.fragment.contacts;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pomohouse.launcher.models.contacts.ContactModel;

import java.util.ArrayList;

/**
 * Created by sirawit on 26/12/15.
 */
class ContactVerticalPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<ContactModel> data;
    private ContactFragment.OnContactItemClickListener itemClickListener;

    ContactVerticalPagerAdapter(FragmentManager fm, ArrayList<ContactModel> data, ContactFragment.OnContactItemClickListener itemClickListener) {
        super(fm);
        this.data = data;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ContactItemFragment getItem(int position) {
        return ContactItemFragment.newInstance(data.get(position), itemClickListener, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        return data.size();
    }
}