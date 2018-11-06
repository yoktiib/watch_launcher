package com.pomohouse.launcher.fragment.menu;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pomohouse.component.pager.VerticalViewPager;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.ActivityResultCode;
import com.pomohouse.launcher.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Fragment to manage the right page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class MenuFragment extends BaseFragment {
    private ArrayList<MenuModel> lstMenu;

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fragment_main_menu_pager)
    VerticalViewPager mVerticalViewPager;
    CustomPagerAdapter mAdapter;

    public MenuFragment() {
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CustomPagerAdapter(getContext(), mVerticalViewPager);
        mVerticalViewPager.setAdapter(mAdapter);
        if (lstMenu != null)
            mAdapter.setData(lstMenu);
        else
            mAdapter.setData(lstMenu = createMainMenu());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ArrayList<MenuModel> createMainMenu() {
        Timber.e("createMenuData");
        String[] mainMenuNameArr = getResources().getStringArray(R.array.mainMenuNameArr);
        String[] mainMenuPackageArr = getResources().getStringArray(R.array.mainMenuPackageArr);
        int[] mainMenuBackgroundArr = getResources().getIntArray(R.array.mainMenuBackgroundArr);
        TypedArray tArray = getResources().obtainTypedArray(
                R.array.mainMenuDrawableArr);
        int count = tArray.length();
        int[] mainMenuDrawableArr = new int[count];
        for (int i = 0; i < mainMenuDrawableArr.length; i++) {
            mainMenuDrawableArr[i] = tArray.getResourceId(i, 0);
        }
        tArray.recycle();
        int[] mainMenuActionArr = getResources().getIntArray(R.array.mainMenuActionArr);
        ArrayList<MenuModel> listMenu = new ArrayList<>();
        for (int i = 0; i < mainMenuNameArr.length; i++) {
            MenuModel item = new MenuModel();
            item.setDrawable(mainMenuDrawableArr[i]);
            item.setName(mainMenuNameArr[i]);
            item.setRequestCode(ActivityResultCode.RESULT_CODE_BFF);
            item.setActionType(mainMenuActionArr[i]);
            item.setBackgroundColor(mainMenuBackgroundArr[i]);
            item.setPackageApp(mainMenuPackageArr[i]);
            item.setPosition(i);
            Timber.e("-" + item.getName());
            listMenu.add(item);
        }
        return listMenu;
    }

    /*OnMenuItemClickListener menuItemClickListener = menuModel -> {
        Timber.e("Click Menu Data");
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
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        }
    };*/

    @Override
    protected List<Object> injectModules() {
        return null;
    }
}
