package com.pomohouse.launcher.manager.in_class_mode;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.launcher.manager.DataManagerConstant;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class InClassModePrefManager extends AbstractSharedPreferences implements IInClassModePrefManager {
    private final String KEY_IN_CLASS_MODE = DataManagerConstant.KEY_IN_CLASS_MODE;

    public InClassModePrefManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addInClassMode(InClassModePrefModel inClassModePrefModel) {
        writeString(KEY_IN_CLASS_MODE, new Gson().toJson(inClassModePrefModel));
    }

    @Override
    public InClassModePrefModel getInClassMode() {
        InClassModePrefModel inClassModePrefModel = new Gson().fromJson(readString(KEY_IN_CLASS_MODE), InClassModePrefModel.class);
        if (inClassModePrefModel == null)
            addInClassMode(inClassModePrefModel = new InClassModePrefModel());
        return inClassModePrefModel;
    }

    @Override
    public void removeInClassMode() {
        removeKey(KEY_IN_CLASS_MODE);
    }
}
