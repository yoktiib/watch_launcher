package com.pomohouse.library.base;


import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;

/**
 * Created by Admin on 4/26/2016 AD.
 */
public abstract class BaseInteractor {
    protected MetaDataNetwork metaData = null;

    protected MetaDataNetwork buildMetadata(ResultGenerator result) {
        return MetaDataNetwork.getSuccess(result.getResCode(), result.getResDesc());
    }

    protected MetaDataNetwork buildMetadata(int resCode, String resDesc) {
        return MetaDataNetwork.getSuccess(resCode, resDesc);
    }
}
