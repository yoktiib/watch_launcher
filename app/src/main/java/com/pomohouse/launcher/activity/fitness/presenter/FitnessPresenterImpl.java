package com.pomohouse.launcher.activity.fitness.presenter;

import com.pomohouse.launcher.activity.fitness.IFitnessView;
import com.pomohouse.launcher.activity.fitness.interactor.IFitnessInteractor;
import com.pomohouse.launcher.api.requests.StepRequest;

import javax.inject.Inject;

/**
 * Created by Admin on 10/25/2016 AD.
 */

public class FitnessPresenterImpl implements IFitnessPresenter {
    IFitnessView view;
    IFitnessInteractor interactor;

    @Inject
    public FitnessPresenterImpl(IFitnessView view, IFitnessInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void sendStep(StepRequest step) {
        interactor.callSendStep(step);
    }
}
