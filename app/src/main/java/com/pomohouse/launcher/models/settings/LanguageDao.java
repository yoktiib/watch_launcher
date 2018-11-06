package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class LanguageDao {


    /**
     * language : th,
     * country : TH
     */
    @SerializedName("keyLanguage")
    @Expose
    private String language = "en";
    @SerializedName("keyCountry")
    @Expose
    private String country = "EN";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
