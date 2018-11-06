package com.pomohouse.launcher.fragment.menu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 10/13/2016 AD.
 */

class MenuModel implements Parcelable {

    static final int ACTION_TYPE_OPEN_OTHER_APP_ACTIVITY = 0;
    static final int ACTION_TYPE_OPEN_IN_APP_ACTIVITY = 1;
    static final int ACTION_TYPE_OPEN_C = 2;

    MenuModel() {
    }

    /**
     * &#36164;&#28304;id
     */
    private int drawable;
    private int position;
    private String packageApp;
    private int actionType;
    private int requestCode;
    private int backgroundColor;
    private boolean isAnimated = false;

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    int getActionType() {
        return actionType;
    }

    void setActionType(int actionType) {
        this.actionType = actionType;
    }

    String getPackageApp() {
        return packageApp;
    }

    void setPackageApp(String packageApp) {
        this.packageApp = packageApp;
    }

    /**
     * &#25968;&#25454;&#21517;&#31216;
     */
    private String name;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static Creator<MenuModel> getCREATOR() {
        return CREATOR;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int id) {
        this.drawable = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected MenuModel(Parcel in) {
        drawable = in.readInt();
        name = in.readString();
        position = in.readInt();
        actionType = in.readInt();
        packageApp = in.readString();
        isAnimated = in.readByte() != 0;//myBoolean == true if byte != 0
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(drawable);
        dest.writeString(name);
        dest.writeInt(position);
        dest.writeInt(actionType);
        dest.writeString(packageApp);
        dest.writeByte((byte) (isAnimated ? 1 : 0));//if myBoolean == true, byte == 1
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MenuModel> CREATOR = new Parcelable.Creator<MenuModel>() {
        @Override
        public MenuModel createFromParcel(Parcel in) {
            return new MenuModel(in);
        }

        @Override
        public MenuModel[] newArray(int size) {
            return new MenuModel[size];
        }
    };

    public void setAnimation() {
        isAnimated = true;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}