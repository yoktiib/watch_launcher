package com.pomohouse.launcher.fragment.settings;
import android.os.Parcel;
import android.os.Parcelable;

public class MyPageItem implements Parcelable {
    private String title;
    private int position;

    public MyPageItem(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    protected MyPageItem(Parcel in) {
        title = in.readString();
        position = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(position);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyPageItem> CREATOR = new Parcelable.Creator<MyPageItem>() {
        @Override
        public MyPageItem createFromParcel(Parcel in) {
            return new MyPageItem(in);
        }

        @Override
        public MyPageItem[] newArray(int size) {
            return new MyPageItem[size];
        }
    };
}