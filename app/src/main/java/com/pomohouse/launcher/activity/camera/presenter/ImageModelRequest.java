package com.pomohouse.launcher.activity.camera.presenter;

import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.library.networks.RequestModel;

import java.io.File;

public class ImageModelRequest extends ImeiRequest {

    private byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
