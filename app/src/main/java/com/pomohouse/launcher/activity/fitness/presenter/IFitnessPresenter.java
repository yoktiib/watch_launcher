package com.pomohouse.launcher.activity.fitness.presenter;

import com.pomohouse.launcher.api.requests.StepRequest;

/**
 * Created by Admin on 10/25/2016 AD.
 */

public interface IFitnessPresenter {
    void sendStep(StepRequest step);
}
