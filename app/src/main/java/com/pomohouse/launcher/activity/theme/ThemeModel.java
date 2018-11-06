package com.pomohouse.launcher.activity.theme;

/**
 * Created by Admin on 9/7/16 AD.
 */
public class ThemeModel {
    private int position;
    private ThemeType type;

    public ThemeType getType() {
        return type;
    }

    public void setType(ThemeType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public ThemeModel setPosition(int position) {
        this.position = position;
        return this;
    }
}
