package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class AutoAnswerDao implements Serializable {
    private static final long serialVersionUID = 3388737198058234655L;
    /**
     * autoAnswer : N
     */
    @SerializedName("autoAnswer")
    @Expose
    private String autoAnswer;

    public String getAutoAnswer() {
        return autoAnswer;
    }

    public AutoAnswerDao setAutoAnswer(String autoAnswer) {
        this.autoAnswer = autoAnswer;
        return this;
    }
}
