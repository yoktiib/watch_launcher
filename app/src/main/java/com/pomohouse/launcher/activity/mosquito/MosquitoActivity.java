package com.pomohouse.launcher.activity.mosquito;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.AppCompatImageView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MosquitoActivity extends BaseActivity {
    private static boolean isMosquito = false, isBindMosquito = false;
    MosquitoService sMosquitoService;
    private ServiceConnection mMosquitoConnect = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            sMosquitoService = ((MosquitoService.MosquitoServiceBinder) service).getService();
            isBindMosquito = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            sMosquitoService = null;
            isBindMosquito = false;
        }
    };


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosquito);
        AppCompatImageView ivOnOffMosquito = (AppCompatImageView) findViewById(R.id.ivOnOffMosquito);
        Intent playIntent = new Intent(this, MosquitoService.class);
//        bindService(playIntent, mMosquitoConnect, Context.BIND_AUTO_CREATE);

        if (isMosquito)
            ivOnOffMosquito.setImageResource(R.drawable.mosquito_on);
        else
            ivOnOffMosquito.setImageResource(R.drawable.mosquito_off);
        ivOnOffMosquito.setOnClickListener(v -> {
            if (isMosquito) {
                isMosquito = false;
                ivOnOffMosquito.setImageResource(R.drawable.mosquito_off);
                /*if (sMosquitoService != null) {
                    unBindLocationService();*/
                stopService(playIntent);
               /* }*/
            } else {
                isMosquito = true;
                ivOnOffMosquito.setImageResource(R.drawable.mosquito_on);
                startService(playIntent);
               /* if (sMosquitoService != null)
                    sMosquitoService.startMosquito();*/
            }
        });
    }

    private void unBindLocationService() {
        if (isBindMosquito && mMosquitoConnect != null) {
            unbindService(mMosquitoConnect);
            isBindMosquito = false;
        }
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
