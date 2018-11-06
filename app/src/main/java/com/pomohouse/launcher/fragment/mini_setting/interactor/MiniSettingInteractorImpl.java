package com.pomohouse.launcher.fragment.mini_setting.interactor;

import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.library.base.BaseInteractor;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class MiniSettingInteractorImpl extends BaseInteractor implements IMiniSettingInteractor {
    WatchService settingService;

    public MiniSettingInteractorImpl() {
        settingService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }
}
