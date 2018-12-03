package com.pomohouse.launcher.utils;

import android.content.Context;
import android.os.Handler;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pomohouse.launcher.utils.callbacks.SignalInfoListener;
import com.pomohouse.library.WearerInfoUtils;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class TelephoneState {
    private TelephonyManager telephonyManager;
    private int lastSignalLevel = 0;
    private boolean isSimCardChecked = false;
    private Context mContext;

    public TelephoneState(Context mContext) {
        this.mContext = mContext;
        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public void init(SignalInfoListener signalListener) {
        try {
           /* if (!isSimCardChecked) {
                new Handler().postDelayed(() -> {
                    isSimCardChecked = true;
                    if (WearerInfoUtils.getInstance().isHaveSimCard(mContext))
                        signalListener.onSimCardReady();
                    else signalListener.onNoSimCard();
                }, 10000);
            }*/
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onSignalStrengthsChanged(SignalStrength signal) {
                    try {
                        int cell_strength = getCellStrength();
                        if (signalListener != null && lastSignalLevel != cell_strength)
                            signalListener.onSignalChanged(lastSignalLevel = cell_strength);
                    } catch (Exception ignored) {
                    }
                }
            };
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            int cell_strength = getCellStrength();
            if (signalListener != null)
                signalListener.onSignalChanged(cell_strength);
        } catch (Exception ignore) {
        }
    }

    private int getCellStrength() {
        int signal_strength = -1;
        try {
            if (telephonyManager.getAllCellInfo() == null || telephonyManager.getAllCellInfo().isEmpty() || telephonyManager.getAllCellInfo().size() == 0)
                return 0;
            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_LTE:
                    CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                    signal_strength = cellSignalStrengthLte.getLevel();// - 140;
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_GSM:
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                    signal_strength = cellSignalStrengthGsm.getLevel();// * 2 - 113;
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                    CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                    signal_strength = cellSignalStrengthWcdma.getLevel();// - 116;
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) telephonyManager.getAllCellInfo().get(0);
                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                    signal_strength = cellSignalStrengthCdma.getLevel();
                    break;
            }
        } catch (ClassCastException ignored) {
            return 0;
        }
        return signal_strength;
    }

    public String getCarrierName() {
        return telephonyManager.getNetworkOperatorName();
    }

}
