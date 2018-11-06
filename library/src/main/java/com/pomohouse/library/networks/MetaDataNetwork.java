package com.pomohouse.library.networks;

import com.pomohouse.library.BuildConfig;

import java.io.Serializable;

import retrofit2.adapter.rxjava.HttpException;
import timber.log.Timber;


/**
 * Created by Admin on 4/21/2016 AD.
 */
public class MetaDataNetwork implements Serializable, Cloneable {
    private static final int INTERNET_CONNECTION_LOSS = 101;
    private static final long serialVersionUID = 6028569087018726825L;

    public enum MetaType {ERROR, SUCCESS}

    private String resDesc = "";
    private int resCode = INTERNET_CONNECTION_LOSS;
    private MetaType metaType = MetaType.SUCCESS;


    public MetaDataNetwork() {
    }

    public MetaDataNetwork(int resCode, String resDesc, MetaType type) {
        this.metaType = type;
        this.resDesc = resDesc;
        this.resCode = resCode;
    }

    public MetaDataNetwork(int resCode, String resDesc) {
        this.resDesc = resDesc;
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public MetaDataNetwork setResDesc(String resDesc) {
        this.resDesc = resDesc;
        return this;
    }

    public int getResCode() {
        return resCode;
    }

    public MetaDataNetwork setResCode(int resCode) {
        this.resCode = resCode;
        return this;
    }

    public static MetaDataNetwork getError(Throwable e) {
        MetaDataNetwork instance = new MetaDataNetwork(INTERNET_CONNECTION_LOSS, e.getMessage());
        if (e instanceof HttpException) {
            try {
                HttpException response = (HttpException) e;
                if (BuildConfig.DEBUG)
                    Timber.d("Response Failure Code : %1$s & Message : %2$s", response.code(), response.message());
                return instance.setResCode(response.code()).setResDesc(response.message());
            } catch (NullPointerException ex) {
                return instance;
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return String.format("Response Failure Code : %1$s & Message : %2$s", resCode, resDesc);
    }

    public static MetaDataNetwork getSuccess(int resCode, String resDesc) {
        return new MetaDataNetwork(resCode, resDesc, MetaType.SUCCESS);
    }

    public static MetaDataNetwork getError(int resCode, String resDesc) {
        return new MetaDataNetwork(resCode, resDesc, MetaType.ERROR);
    }
}
