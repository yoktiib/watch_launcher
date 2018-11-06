package com.pomohouse.launcher.fragment.contacts;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.launcher.models.contacts.ContactModel;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class IncomingCallReceiver extends BroadcastReceiver {

    AudioManager audioManager;
    TelephonyManager telephonyManager;
    public static boolean ring = false, callReceived = false;
    private static String incomingNumber;

    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null || intent.getAction() == null)
                return;
            if (intent.getAction().equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (CombineObjectConstance.getInstance().isInClassTime() && !CombineObjectConstance.getInstance().isAutoAnswer()) {
                    rejectCall();
                } else {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    List<ContactModel> contactModelList = CombineObjectConstance.getInstance().getContactEntity().getContactCollection().getContactModelList();
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        ring = true;
                        callReceived = false;
                        incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Timber.e("Phone In : " + incomingNumber);
                        if (contactModelList != null && contactModelList.size() > 0) {
                            if (!validateNumber(contactModelList, incomingNumber))
                                rejectCall();
                            else if (!CombineObjectConstance.getInstance().isAutoAnswer())
                                createWakeLocks(context);
                        } else
                            rejectCall();
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        Timber.e("EXTRA_STATE_OFFHOOK");
                        callReceived = true;
                    }
                    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        if (ring && !callReceived) {
                            Timber.e("EXTRA_STATE_IDLE : Check " + incomingNumber);
                            if (incomingNumber != null && !incomingNumber.isEmpty()) {
                                String contactID = validateNumberContact(contactModelList, incomingNumber);
                                if (contactID != null && !contactID.isEmpty())
                                    sendCallInfo(POMOContract.CallEntry.MISSED_TYPE, contactID, "");
                            }
                        }
                        ring = false;
                        callReceived = false;
                    }
                }
            }
        } catch (Exception ignore) {
        }
    }

    public String validateNumberContact(List<ContactModel> contactModelList, String incomingNumber) {
        try {
            for (ContactModel contact : contactModelList)
                if (PhoneNumberUtils.compare(incomingNumber, contact.getPhone()))
                    return contact.getContactId();
        } catch (Exception ignore) {
            return "";
        }
        return "";
    }

    private void sendCallInfo(int callType, String contactID, String duration) {
        Timber.e("Contact ID" + contactID);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Uri pathUri = Uri.parse("content://com.pomohouse").buildUpon().appendPath("calls").build();
        ContentValues values = new ContentValues();
        values.put("TYPE", callType); // number => {1=INCOMING_TYPE,2=OUTGOING_TYPE,3=MISSED_TYPE}
        values.put("NUMBER", contactID);//imei or memberID
        values.put("DURATION", duration);//second
        values.put("IS_READ", "0"); // fix 0
        values.put("DATE", String.valueOf(sf.format(new Date()))); // 2017-01-01 => yyyy-mm-dd
        if (ActivityContextor.getInstance().getActivity() != null)
            ActivityContextor.getInstance().getActivity().getContentResolver().insert(pathUri, values);
        Timber.e("sendCallInfo: type: " + callType + " ID: " + contactID + " Time: " + duration);
    }

    public boolean validateNumber(List<ContactModel> contactModelList, String incomingNumber) {
        try {
            for (ContactModel contact : contactModelList)
                if (contact.getPhone() != null)
                    if (PhoneNumberUtils.compare(incomingNumber, contact.getPhone()) && contact.getContactRole() == 0)
                        return true;
            return false;
        } catch (Exception ignore) {
            return false;
        }
    }

    protected void createWakeLocks(Context context) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Lone worker - FULL WAKE LOCK");
            if (!powerManager.isInteractive())
                fullWakeLock.acquire();
            context.sendBroadcast(new Intent("com.pomohouse.service.EVENT_UNLOCK_SCREEN"));

        } catch (Exception ignore) {
        }
    }

    private void rejectCall() {
        try {
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            Object telephonyInterface = method.invoke(telephonyManager);
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
