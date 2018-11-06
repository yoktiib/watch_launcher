package com.pomohouse.launcher.activity.mosquito;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.pomohouse.launcher.R;

public class MosquitoService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private MediaPlayer mp;

    public MosquitoService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        unMuteAudio();
        startMosquito();
    }

    private AudioManager audioManager;

    public void initMusicPlayer() {
        //set player properties
        mp.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    public void unMuteAudio() {
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 10, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 10, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 10, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
        }
    }

    private final IBinder mBinder = new MosquitoService.MosquitoServiceBinder();

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public void startMosquito() {
        try {
            mp = MediaPlayer.create(this, R.raw.a18000);
            mp.setLooping(true); // Set looping
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        initMusicPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
        if (mp != null)
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        super.onDestroy();
    }

    public class MosquitoServiceBinder extends Binder {
        public MosquitoService getService() {
            return MosquitoService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mp != null)
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
