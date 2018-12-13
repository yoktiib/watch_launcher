package com.pomohouse.launcher.fragment.contacts.presenter;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.AllowCallingRequest;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.fragment.avatar.AvatarCollection;
import com.pomohouse.launcher.fragment.contacts.interactor.IContactInteractor;
import com.pomohouse.launcher.fragment.contacts.interactor.OnCheckAllowCallingListener;
import com.pomohouse.launcher.fragment.contacts.interactor.OnContactListener;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import timber.log.Timber;

import static com.pomohouse.launcher.fragment.contacts.ContactFragment.ACTION_OUTGOING_CALL;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;

/**
 * Created by Admin on 8/30/16 AD.
 */
public class ContactPresenterImpl extends BaseRetrofitPresenter implements IContactPresenter, OnContactListener, OnCheckAllowCallingListener {
    public static final String ACTION_SYNC_CONTACT = "ACTION_SYNC_CONTACT";
    public static final String ACTION_REGISTER_TWILIO_TOKEN = "ACTION_REGISTER_TWILIO_TOKEN";
    private static final int UPDATE_CONTACT = 2;
    private static final int DELETE_CONTACT = 3;
    private IContactInteractor interactor;
    private Context mContext;
    private ContactCollection currentContactCollection;

    public ContactPresenterImpl(IContactInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        try {
            if (param == null)
                return;
            if (param.length < 1)
                return;
            if (param[0] == null)
                return;
            mContext = (Context) param[0];
            currentContactCollection = CombineObjectConstance.getInstance().getContactEntity().getContactCollection();
        } catch (Exception ignore) {

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void requestContact() {
        interactor.callContactService(this, new ImeiRequest(WearerInfoUtils.getInstance().getImei(mContext)));
    }

    @Override
    public void onContactFailure(MetaDataNetwork error) {
        //CombineObjectConstance.getInstance().getContactEntity().setContactSynced(true);
    }

    @Override
    public void onContactSuccess(MetaDataNetwork metaData, ContactCollection data) {
        currentContactCollection = CombineObjectConstance.getInstance().getContactEntity().getContactCollection();
        CombineObjectConstance.getInstance().getContactEntity().setContactSynced(true);
        if (data != null) {
            if (data.getContactModelList().size() > 0) {
                _handler.sendMessage(_handler.obtainMessage(UPDATE_CONTACT, data));
            } else {
                _handler.sendMessage(_handler.obtainMessage(DELETE_CONTACT, data));
            }
            onSendEventToBroadcast(new Gson().toJson(data));
        }
    }

    private void onSendEventToBroadcast(String data) {
        Timber.e("Content : " + data);
        final Intent intentEvent = new Intent(ACTION_SYNC_CONTACT);
        intentEvent.putExtra(EVENT_EXTRA, data);
        //mContext.sendBroadcast(new Intent(ACTION_REGISTER_TWILIO_TOKEN));
        mContext.sendBroadcast(intentEvent);
    }

    /**
     * @param contact Contact For Delete
     *                Delete Contact From Content Provider is not want or Service is Delete Item will sync auto
     */
    @Override
    public void onDeleteContact(ContactModel contact) throws Exception {
        if (mContext == null)
            mContext = AppContextor.getInstance().getContext();
        if (mContext == null)
            return;
        mContext.getContentResolver().delete(POMOContract.ContactEntry.CONTENT_URI, POMOContract.ContactEntry.CONTACT_ID + " = ? ", new String[]{String.valueOf(contact.getContactId())});
    }

    /**
     * Delete All Contact
     */
    @Override
    public void onDeleteAllContact() {
        if (mContext == null)
            mContext = AppContextor.getInstance().getContext();
        if (mContext == null)
            return;
        mContext.getContentResolver().delete(POMOContract.ContactEntry.CONTENT_URI, null, new String[]{});
    }

    @Override
    public void requestCheckAllowCalling(AllowCallingRequest callingRequest) {
        interactor.callCheckAllowCalling(callingRequest, this);
    }

    /**
     * @param contact Data new contact for update
     */
    @Override
    public void onUpdateContact(ContactModel contact) {
        ContentValues values = new ContentValues();
        values.put(POMOContract.ContactEntry.CONTACT_ID, contact.getContactId());
        values.put(POMOContract.ContactEntry.NAME, contact.getName());
        values.put(POMOContract.ContactEntry.AVATAR, contact.getAvatar());
        values.put(POMOContract.ContactEntry.AVATAR_TYPE, contact.getAvatarType());
        values.put(POMOContract.ContactEntry.CONTACT_ROLE, contact.getContactRole());
        values.put(POMOContract.ContactEntry.ROLE, contact.getRole());
        values.put(POMOContract.ContactEntry.CALL_TYPE, contact.getCallType());
        values.put(POMOContract.ContactEntry.GENDER, contact.getGender());
        values.put(POMOContract.ContactEntry.PHONE, contact.getPhone());
        values.put(POMOContract.ContactEntry.CONTACT_TYPE, contact.getContactType());
        if (mContext == null)
            mContext = AppContextor.getInstance().getContext();
        if (mContext == null)
            return;
        mContext.getContentResolver().update(POMOContract.ContactEntry.CONTENT_URI, values, POMOContract.ContactEntry.CONTACT_ID + " = ? ", new String[]{contact.getContactId()});
    }

    /**
     * @param contact Data contact new for insert
     */
    @Override
    public void onInsertContact(ContactModel contact) {
        ContentValues values = new ContentValues();
        values.put(POMOContract.ContactEntry.CONTACT_ID, contact.getContactId());
        values.put(POMOContract.ContactEntry.NAME, contact.getName());
        values.put(POMOContract.ContactEntry.AVATAR, contact.getAvatar());
        values.put(POMOContract.ContactEntry.GENDER, contact.getGender());
        values.put(POMOContract.ContactEntry.CALL_TYPE, contact.getCallType());
        values.put(POMOContract.ContactEntry.AVATAR_TYPE, contact.getAvatarType());
        values.put(POMOContract.ContactEntry.CONTACT_ROLE, contact.getContactRole());
        values.put(POMOContract.ContactEntry.ROLE, contact.getRole());
        values.put(POMOContract.ContactEntry.PHONE, contact.getPhone());
        values.put(POMOContract.ContactEntry.CONTACT_TYPE, contact.getContactType());
        if (mContext == null)
            mContext = AppContextor.getInstance().getContext();
        if (mContext == null)
            return;
        mContext.getContentResolver().insert(POMOContract.ContactEntry.CONTENT_URI, values);
    }

    @Override
    public void onCheckAllowCallingFailure(MetaDataNetwork error) {
        /*view.failureCheckAllowCalling(error);*/
    }

    @Override
    public void onCheckAllowCallingSuccess(MetaDataNetwork metaData, ResultGenerator<CallContact> readyModel) {
        if (readyModel.getData().getIsAllowCalling().equalsIgnoreCase("Y"))
            sendCalling(readyModel.getData());
    }

    @Override
    public void sendCalling(CallContact content) {
        Timber.e("Check= " + content);
        final Intent intentEvent = new Intent(ACTION_OUTGOING_CALL);
        intentEvent.putExtra(EVENT_EXTRA, new Gson().toJson(content));
        Timber.e("To = sendBroadcast");
        AppContextor.getInstance().getContext().sendBroadcast(intentEvent);
    }

    private final Handler _handler = new Handler(msg -> {
        switch (msg.what) {
            case UPDATE_CONTACT:
                try {
                    if (mContext == null)
                        mContext = AppContextor.getInstance().getContext();
                    if (mContext != null)
                        mContext.getContentResolver().delete(ContactsContract.Data.CONTENT_URI, null, null);
                    Timber.e("Update : Contact ");
                    ContactCollection contactCorrectData = (ContactCollection) msg.obj;
                    if (contactCorrectData == null)
                        return true;
                    syncDataBeforeInsertOrUpdate(contactCorrectData);
                    for (ContactModel contact : contactCorrectData.getContactModelList()) {
                        boolean found = false;
                        if (currentContactCollection != null) {
                            for (ContactModel contactUpdate : currentContactCollection.getContactModelList())
                                if (contactUpdate.getContactId().equalsIgnoreCase(contact.getContactId())) {
                                    found = true;
                                }
                        }
                        insertContacts(contact.getName(), contact.getPhone(), contact.getAvatar(), contact.getAvatarType());
                        if (!found) {
                            onInsertContact(contact);
                        } else {
                            onUpdateContact(contact);
                        }
                    }
                    currentContactCollection = contactCorrectData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case DELETE_CONTACT:
                try {
                    Timber.e("DELETE_CONTACT : Contact ");
                    syncDataBeforeInsertOrUpdate(new ContactCollection());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    });

    /**
     * @param contactCorrectData List Correct Correct From System Service
     *                           Function compare data sync for delete
     */
    private void syncDataBeforeInsertOrUpdate(ContactCollection contactCorrectData) {
        if (currentContactCollection == null || currentContactCollection.getContactModelList() == null)
            return;
        for (ContactModel currentContact : currentContactCollection.getContactModelList()) {
            boolean found = false;
            if (contactCorrectData.getContactModelList() != null) {
                for (ContactModel correctContact : contactCorrectData.getContactModelList()) {
                    if (correctContact.getContactId().equalsIgnoreCase(currentContact.getContactId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) try {
                onDeleteContact(currentContact);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AvatarCollection avatarCollection = AvatarCollection.getInstance();

    private void insertContacts(String given_name, String mobile_number,
                                String avatar, int avatarType) {
        try {
            given_name = getAvailableName(given_name);

            final ArrayList<ContentProviderOperation> operationList = new ArrayList<>();
            ContentProviderOperation.Builder builder;
            int rawContactIndex = 0;

            builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
            builder.withValue(ContactsContract.RawContacts._ID, null);

            operationList.add(builder.build());

            // Add name.
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
            builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID,
                    rawContactIndex);

            builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, given_name);
            operationList.add(builder.build());

            // Add phone number.
            if (mobile_number != null && !TextUtils.isEmpty(mobile_number)) {
                builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
                builder.withValueBackReference(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                        rawContactIndex);
                builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile_number);
                builder.withValue(ContactsContract.Data.IS_PRIMARY, 1);
                operationList.add(builder.build());
            }
            //add avatar photo
            if (mContext == null)
                mContext = AppContextor.getInstance().getContext();
            mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
                    operationList);
            // Get raw_contact_i
            Thread.sleep(100);
            Cursor cc = mContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    null, "display_name=?", new String[]{given_name}, null);
            if (cc != null && cc.moveToFirst()) {
                //Insert photo for the contact.
                //Log.i("", "cc.getCount()=" + cc.getCount());
                if (avatarType == 0) {
                    Bitmap profilePhoto = getBitmapFromVectorDrawable(mContext, avatarCollection.getAvatarMap().get(avatar));
                    boolean result = saveUpdatedPhoto(cc.getLong(cc.getColumnIndex("_id")), profilePhoto);
                    if(!result){
                        saveUpdatePhotoForSpare(cc.getLong(cc.getColumnIndex("_id")), profilePhoto);
                    }
                    cc.close();
                } else {
                    Glide.with(mContext)
                            .load(avatar)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(100, 100) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    boolean result = saveUpdatedPhoto(cc.getLong(cc.getColumnIndex("_id")), resource);
                                    if(!result){
                                        saveUpdatePhotoForSpare(cc.getLong(cc.getColumnIndex("_id")), resource);
                                    }
                                    cc.close();
                                }
                            });
                }
            }
        } catch (InterruptedException | RemoteException | OperationApplicationException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveUpdatedPhoto(long rawContactId, Bitmap avatar) {
        final Uri outputUri = Uri.withAppendedPath(ContentUris.withAppendedId(
                ContactsContract.RawContacts.CONTENT_URI, rawContactId),
                ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY);
        boolean result;
        try {
            try (FileOutputStream outputStream = mContext.getContentResolver()
                    .openAssetFileDescriptor(outputUri, "rw")
                    .createOutputStream()) {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                // 将Bitmap压缩成PNG编码，质量为100%存储
                avatar.compress(Bitmap.CompressFormat.PNG, 100, os);
                byte[] avatarBytes = os.toByteArray();
                outputStream.write(avatarBytes);
                outputStream.close();
                os.close();
                result = true;
            }
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }


    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void saveUpdatePhotoForSpare(long rawContactId, Bitmap avatarBitmap){
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            ContentValues values = new ContentValues();
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar);
            mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            os.close();
        }catch(Exception ignored){

        }
    }

    private String getAvailableName(String org_name){
        String tempName = org_name.trim();
        StringBuilder nameBuilder = new StringBuilder();
        char[] namebytes = tempName.toCharArray();
        int spaceCount = 0;
        for(int i = 0 ; i < namebytes.length ; i++){
            if(Character.isWhitespace(namebytes[i])){
                spaceCount ++;
            }else{
                spaceCount = 0;
            }
            if(spaceCount < 2){
                nameBuilder.append(namebytes[i]);
            }
        }

        return nameBuilder.toString();
    }


}
