package com.pomohouse.launcher.activity.fitness.presenter;

import com.google.gson.Gson;
import com.pomohouse.launcher.activity.fitness.IFitnessView;
import com.pomohouse.launcher.api.requests.StepRequest;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;

import javax.inject.Inject;

/**
 * Created by Admin on 10/25/2016 AD.
 */

public class FitnessPresenterImpl implements IFitnessPresenter {
    IFitnessView view;

    @Inject
    public FitnessPresenterImpl(IFitnessView view) {
        this.view = view;
    }

    @Override
    public void sendStep(StepRequest step) {
        TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_FITNESS, new Gson().toJson(step));
    }
}
