package com.pomohouse.launcher.manager.theme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.pomohouse.launcher.activity.theme.ThemeType;

/**
 * Created by Admin on 9/12/16 AD.
 */
public class ThemePrefModel implements Parcelable {

    @SerializedName("themeId")
    private int themeId = 0;
    @SerializedName("themeType")
    private String themeType;
    @SerializedName("hourColor")
    private String hourColor;
    @SerializedName("minuteColor")
    private String minuteColor;
    @SerializedName("dayNameColor")
    private String dayNameColor;
    @SerializedName("dataColor")
    private String dataColor;
    @SerializedName("dosColor")
    private String dosColor;
    @SerializedName("background")
    private String background;
    @SerializedName("sample_theme")
    private String sample_theme;
    /**
     * position : 1
     */

    @SerializedName("position")
    private int position;
    private boolean changed = false;

    public String getSample_theme() {
        return sample_theme;
    }

    public void setSample_theme(String sample_theme) {
        this.sample_theme = sample_theme;
    }

    public ThemePrefModel() {
    }

    public String getBackground() {
        return background;
    }

    public ThemePrefModel setBackground(String background) {
        this.background = background;
        return this;
    }

    public String getHourColor() {
        return hourColor;
    }

    public ThemePrefModel setHourColor(String hourColor) {
        this.hourColor = hourColor;
        return this;
    }

    public String getMinuteColor() {
        return minuteColor;
    }

    public ThemePrefModel setMinuteColor(String minuteColor) {
        this.minuteColor = minuteColor;
        return this;
    }

    public String getDayNameColor() {
        return dayNameColor;
    }

    public ThemePrefModel setDayNameColor(String dayNameColor) {
        this.dayNameColor = dayNameColor;
        return this;
    }

    public String getDataColor() {
        return dataColor;
    }

    public ThemePrefModel setDataColor(String dataColor) {
        this.dataColor = dataColor;
        return this;
    }

    public String getDosColor() {
        return dosColor;
    }

    public ThemePrefModel setDosColor(String dosColor) {
        this.dosColor = dosColor;
        return this;
    }

    public ThemeType getThemeType() {
        if (themeType == null)
            return ThemeType.DIGITAL;
        return themeType.equalsIgnoreCase("analog") ? ThemeType.ANALOG : ThemeType.DIGITAL;
    }

    public void setThemeType(String themeType) {
        this.themeType = themeType;
    }

    public boolean isChanged() {
        return changed;
    }

    public ThemePrefModel setChanged(boolean changed) {
        this.changed = changed;
        return this;
    }

    public int getThemeId() {
        return themeId;
    }

    public ThemePrefModel setThemeId(int themeId) {
        this.themeId = themeId;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public ThemePrefModel setPosition(int position) {
        this.position = position;
        return this;
    }

    protected ThemePrefModel(Parcel in) {
        themeId = in.readInt();
        changed = in.readByte() != 0x00;
        themeType = in.readString();
        hourColor = in.readString();
        minuteColor = in.readString();
        dayNameColor = in.readString();
        dataColor = in.readString();
        dosColor = in.readString();
        background = in.readString();
        position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(themeId);
        dest.writeByte((byte) (changed ? 0x01 : 0x00));
        dest.writeString(themeType);
        dest.writeString(hourColor);
        dest.writeString(minuteColor);
        dest.writeString(dayNameColor);
        dest.writeString(dataColor);
        dest.writeString(dosColor);
        dest.writeString(background);
        dest.writeInt(position);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ThemePrefModel> CREATOR = new Parcelable.Creator<ThemePrefModel>() {
        @Override
        public ThemePrefModel createFromParcel(Parcel in) {
            return new ThemePrefModel(in);
        }

        @Override
        public ThemePrefModel[] newArray(int size) {
            return new ThemePrefModel[size];
        }
    };
}