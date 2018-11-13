package com.pomohouse.launcher.fragment.menu;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomohouse.component.pager.VerticalViewPager;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.R;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.languages.LanguageSetting;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Admin on 2/28/2017 AD.
 */

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MenuModel> data;
    private VerticalViewPager mVerticalViewPager;

    public void setData(ArrayList<MenuModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    CustomPagerAdapter(Context context, VerticalViewPager mVerticalViewPager) {
        this.mContext = context;
        /*this.mVerticalViewPager = mVerticalViewPager;*/
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    // Returns true if a particular object (page) is from a particular page
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // This method should create the page for the given position passed to it as an argument.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate the layout for the page
        View itemView = mLayoutInflater.inflate(R.layout.item_menu, container, false);
        final MenuModel menuModel = data.get(position);
        TextView tvMenuName = (TextView) itemView.findViewById(R.id.tvMenuName);
        AppCompatImageView ivMenuItem = (AppCompatImageView) itemView.findViewById(R.id.ivMenuItem);
        tvMenuName.setText(menuModel.getName());
        ivMenuItem.setImageResource(menuModel.getDrawable());
        itemView.findViewById(R.id.container).setBackgroundColor(menuModel.getBackgroundColor());
        tvMenuName.setOnClickListener(v -> intentClick(menuModel));
        itemView.findViewById(R.id.ivMenuItem).setOnClickListener(v -> intentClick(menuModel));
        container.addView(itemView);
        return itemView;
    }

    private void intentClick(MenuModel menuModel) {
        Timber.e("Click Menu");
        if (menuModel.getActionType() == MenuModel.ACTION_TYPE_OPEN_OTHER_APP_ACTIVITY) {
            openApp(menuModel.getPackageApp());
        } else if (menuModel.getActionType() == MenuModel.ACTION_TYPE_OPEN_IN_APP_ACTIVITY) {
            Class<?> c = null;
            if (menuModel.getPackageApp() != null) {
                try {
                    c = Class.forName(menuModel.getPackageApp());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            openActivity(c);
        }/* else {
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        }*/
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        mContext.startActivity(intent);
    }

    private boolean openApp(String packageName) {
        PackageManager manager = mContext.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
        }
        i.putExtra("LANGUAGE", LanguageSetting.getLanguage());
        i.putExtra("IMEI", WearerInfoUtils.getInstance().getImei(mContext));
        i.putExtra("POMO_VERSION", WearerInfoUtils.getInstance().getPomoVersion());
        i.putExtra("PLATFORM", WearerInfoUtils.getInstance().getPlatform());
        if (packageName.contains("takemehome") && POMOWatchApplication.mLocation!=null){
            i.putExtra("LATITUDE", POMOWatchApplication.mLocation.getLatitude());
            i.putExtra("LONGITUDE", POMOWatchApplication.mLocation.getLongitude());
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.addCategory(Intent.ACTION_MAIN);
        mContext.startActivity(i);
        return true;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    // Removes the page from the container for the given position.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
