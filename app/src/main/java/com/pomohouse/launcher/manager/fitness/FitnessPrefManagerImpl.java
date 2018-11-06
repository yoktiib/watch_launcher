package com.pomohouse.launcher.manager.fitness;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class FitnessPrefManagerImpl extends AbstractSharedPreferences implements IFitnessPrefManager {
    private final String FITNESS = "KEY_FITNESS";

    public FitnessPrefManagerImpl(Context mContext) {
        super(mContext);
    }

    @Override
    public void addFitness(FitnessPrefModel fitnessPref) {
        writeString(FITNESS, new Gson().toJson(fitnessPref));
    }

    @Override
    public FitnessPrefModel getFitness() {
        FitnessPrefModel fitnessPref = new Gson().fromJson(readString(FITNESS), FitnessPrefModel.class);
        if (fitnessPref == null)
            fitnessPref = new FitnessPrefModel();
        return fitnessPref;
    }

    @Override
    public void removeFitness() {
        removeKey(FITNESS);
    }
}
