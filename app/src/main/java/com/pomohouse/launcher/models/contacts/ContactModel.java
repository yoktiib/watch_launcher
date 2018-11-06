package com.pomohouse.launcher.models.contacts;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.launcher.content_provider.POMOContract;

/**
 * Created by Admin on 8/30/16 AD.
 */
public class ContactModel implements Parcelable {
    /**
     * name : beer_on_sky
     * phone : 0867244435
     * type : family
     */
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("contactType")
    @Expose
    private String contactType;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("contactId")
    @Expose
    private String contactId;
    @SerializedName("avatarType")
    @Expose
    private int avatarType;
    @SerializedName("contactRole")
    @Expose
    private int contactRole;
    @SerializedName("role")
    @Expose
    private int role;
    @SerializedName("callType")
    @Expose
    private String callType;

    public String getCallType() {
        if (callType == null)
            return "V";
        return callType;
    }

    public ContactModel setUpData(Cursor cursor){
        setName(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.NAME)));
        setAvatar(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.AVATAR)));
        setAvatarType(cursor.getInt(cursor.getColumnIndex(POMOContract.ContactEntry.AVATAR_TYPE)));
        setGender(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.GENDER)));
        setPhone(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.PHONE)));
        setContactType(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.CONTACT_TYPE)));
        setContactId(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.CONTACT_ID)));
        setContactRole(cursor.getInt(cursor.getColumnIndex(POMOContract.ContactEntry.CONTACT_ROLE)));
        setCallType(cursor.getString(cursor.getColumnIndex(POMOContract.ContactEntry.CALL_TYPE)));
        setRole(cursor.getInt(cursor.getColumnIndex(POMOContract.ContactEntry.ROLE)));
        return this;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getContactRole() {
        return contactRole;
    }

    public void setContactRole(int contactRole) {
        this.contactRole = contactRole;
    }

    public int getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public ContactModel() {
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    protected ContactModel(Parcel in) {
        name = in.readString();
        phone = in.readString();
        contactType = in.readString();
        avatar = in.readString();
        gender = in.readString();
        avatarType = in.readInt();
        contactId = in.readString();
        contactRole = in.readInt();
        role = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(contactType);
        dest.writeString(avatar);
        dest.writeString(gender);
        dest.writeInt(avatarType);
        dest.writeString(contactId);
        dest.writeInt(contactRole);
        dest.writeInt(role);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
        @Override
        public ContactModel createFromParcel(Parcel in) {
            return new ContactModel(in);
        }

        @Override
        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };
}