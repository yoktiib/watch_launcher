package com.pomohouse.launcher.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibrateManager {

    private static VibrateManager instance;
    private Vibrator vibration;

    private VibrateManager(Context context) {
        vibration = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static VibrateManager getInstance(Context context) {
        if (instance == null) {
            instance = new VibrateManager(context);
        }
        return instance;
    }

    public void notificationVibration() {
        if (CombineObjectConstance.getInstance().isInClassTime() || CombineObjectConstance.getInstance().isSilentMode())
            return;
        if (vibration != null) {
            long[] pattern = {100, 150, 200};
            vibration.vibrate(pattern, -1);
        }
    }

    public void alarmVibration() {
        if (CombineObjectConstance.getInstance().isInClassTime() || CombineObjectConstance.getInstance().isSilentMode())
            return;
        if (vibration != null) {
            long[] pattern = {0, 500, 1500, 500, 1500, 500, 1500, 500, 1500, 500, 1500, 500, 1500, 500, 1500, 500, 1500, 500, 1500 , 500, 1500};
            vibration.vibrate(pattern, -1);
        }
    }

    public  void sosVibration() {
        if (vibration != null) {
            long[] pattern = {0, 500, 1500, 500, 1500, 500, 1500};
            vibration.vibrate(pattern, -1);
        }
    }

    public  void release() {
        if (vibration != null)
            vibration.cancel();
    }

    public  void unlockScreen() {
        if (vibration != null) {
            vibration.vibrate(40);
        }
    }
}
