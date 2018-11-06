package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 6/9/2017 AD.
 */

public class StepRequest extends ImeiRequest {
    @SerializedName("step")
    @Expose
    private int step;

    public StepRequest(int stepForSync) {
        this.step = stepForSync;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
