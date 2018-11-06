package com.pomohouse.launcher.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.Settings;

import com.pomohouse.launcher.R;

import timber.log.Timber;

import static android.content.Context.AUDIO_SERVICE;

public class SoundPoolManager {

    private boolean playing = false;
    private boolean loaded = false;
    private float actualVolume;
    private float maxVolume;
    private float volume;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private int alarmSound;
    private int ringingStreamId;
    private int notificationSound;
    private static SoundPoolManager instance;


    private SoundPoolManager(Context context) {
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;
        // Load the sounds
        int maxStreams = 1;
        soundPool = new SoundPool.Builder()
                .setMaxStreams(maxStreams)
                .build();
        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> loaded = true);
        alarmSound = soundPool.load(context, R.raw.sound_alarm, 1);
        notificationSound = soundPool.load(context, R.raw.sound_notification, 1);
    }

    public void vibrateAndSound(Context context, int level) {
        Timber.i("soundpool vibrateAndSound level = "+level);
        if (audioManager == null)
            return;
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setMode(AudioManager.RINGER_MODE_VIBRATE);
        Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 1);
        speakerLevel(level);
    }

    public void silentMode(Context context) {
        Timber.i("soundpool silentMode  ");
        if (audioManager == null)
            return;
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setMode(AudioManager.RINGER_MODE_SILENT);
        Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 0);
        speakerLevel(0);
    }

    public void ringingMode(Context context, int level) {
        Timber.i("soundpool ringingMode  level="+level);
        if (audioManager == null)
            return;
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setMode(AudioManager.RINGER_MODE_SILENT);
        Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 0);
        speakerLevel(level);
    }

    public void vibrateOnly(Context context) {
        Timber.i("soundpool vibrateOnly  ");
        if (audioManager == null)
            return;
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setMode(AudioManager.RINGER_MODE_VIBRATE);
        Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 1);
        speakerLevel(0);
    }

    private void speakerLevel(int value) {
        if (audioManager == null)
            return;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, value, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, value, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, value, 0);
    }

    public static SoundPoolManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundPoolManager(context);
        }
        return instance;
    }

    public void playAlarm() {
        // if (loaded && !playing) {
        if (CombineObjectConstance.getInstance().isInClassTime() || CombineObjectConstance.getInstance().isSilentMode())
            return;
        if (soundPool != null) {
            soundPool.stop(notificationSound);
            ringingStreamId = soundPool.play(alarmSound, volume, volume, 1, 13, 1f);
            //     playing = true;
        }
        //
        //}
    }

    public void stopAlarm() {
        // if (playing) {
        if (soundPool != null)
            soundPool.stop(ringingStreamId);
        //playing = false;
        // }
    }

    public void playNotification() {
        if (CombineObjectConstance.getInstance().isInClassTime() || CombineObjectConstance.getInstance().isSilentMode())
            return;
        if (soundPool != null) {
            soundPool.stop(notificationSound);
            soundPool.play(notificationSound, volume, volume, 1, 0, 1f);
            //playing = true;
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.unload(alarmSound);
            soundPool.unload(notificationSound);
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }

}
