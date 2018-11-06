package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class InClassDao implements Serializable {

    private static final long serialVersionUID = 1369257933232189793L;
    /**
     * autoAnswer : N
     */
    @SerializedName("inClass")
    @Expose
    private String inClass;

    public String getInClass() {
        return inClass;
    }

    public InClassDao setInClass(String inClass) {
        this.inClass = inClass;
        return this;
    }
}
