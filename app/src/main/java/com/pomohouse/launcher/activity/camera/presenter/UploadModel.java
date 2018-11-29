package com.pomohouse.launcher.activity.camera.presenter;

import com.pomohouse.library.networks.ResultModel;

public class UploadModel extends ResultModel {

    /**
     * resCode : 0
     * resDesc : Successful
     * data : {"imei":"A1000051998770","image":"cam_A1000051998770_1543510840395_128.png"}
     */

    private String imei;
    private String image;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
