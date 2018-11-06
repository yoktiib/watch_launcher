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

import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.manager.ActivityContextor;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class OutGoingCallReceiver extends BroadcastReceiver {

    AudioManager audioManager;
    TelephonyManager telephonyManager;
    public static boolean isSOS = false, outGoing = false;
    public static MakeCallSOSListener sosListener;

    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null || intent.getAction() == null)
                return;
            if (intent.getAction().equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if (outGoing && isSOS) {
                        outGoing = false;
                        new Handler().postDelayed(() -> {
                            Timber.e("EXTRA_STATE_IDLE OutGoing");
                            //Do something after 100ms
                            if (sosListener != null)
                                sosListener.onEndCallState();
                        }, 3000);
                    }
                }
            } else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
                Timber.e("ACTION_NEW_OUTGOING_CALL OutGoing");
                outGoing = true;
            }
        } catch (Exception ignore) {
        }
    }

    public interface MakeCallSOSListener {
        void onEndCallState();
    }
}
