package com.pomohouse.launcher.manager.theme;

import java.util.ArrayList;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface IThemePrefManager {

    void addCurrentTheme(ThemePrefModel wearerModel);

    ThemePrefModel getCurrentTheme();

    void removeCurrentTheme();

    void addDataTheme(String theme);

    ArrayList<ThemePrefModel> getDataTheme();

    void removeDataTheme();
}
