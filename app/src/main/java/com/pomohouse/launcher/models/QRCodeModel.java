package com.pomohouse.launcher.models;

import com.pomohouse.library.networks.ResultModel;

public class QRCodeModel extends ResultModel {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
