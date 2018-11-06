package com.pomohouse.launcher.manager.theme;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pomohouse.launcher.manager.DataManagerConstant;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

import java.util.ArrayList;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class ThemePrefManager extends AbstractSharedPreferences implements IThemePrefManager {
    private final String KEY_THEME = DataManagerConstant.KEY_THEME;
    private final String KEY_DATA_THEME = DataManagerConstant.KEY_DATA_THEME;

    public ThemePrefManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addCurrentTheme(ThemePrefModel backgroundPref) {
        writeString(KEY_THEME, new Gson().toJson(backgroundPref));
    }

    @Override
    public ThemePrefModel getCurrentTheme() {
        ThemePrefModel bgMenu = new Gson().fromJson(readString(KEY_THEME), ThemePrefModel.class);
        if (bgMenu == null)
            addCurrentTheme(bgMenu = new ThemePrefModel());
        return bgMenu;
    }

    @Override
    public void removeCurrentTheme() {
        removeKey(KEY_THEME);
    }

    @Override
    public void addDataTheme(String theme) {
        writeString(KEY_DATA_THEME, theme);
    }

    @Override
    public ArrayList<ThemePrefModel> getDataTheme() {
        ThemePrefList themePrefModels = new Gson().fromJson(readString(KEY_DATA_THEME), new TypeToken<ThemePrefList>() {
        }.getType());
        if (themePrefModels == null)
            themePrefModels = new ThemePrefList();
        return themePrefModels.getDataThemePrefModels();
    }

    @Override
    public void removeDataTheme() {
        removeKey(KEY_DATA_THEME);
    }
}
