package com.pomohouse.launcher.activity.fitness.interactor;

import com.pomohouse.launcher.api.requests.StepRequest;

/**
 * Created by Admin on 10/25/2016 AD.
 */

public interface IFitnessInteractor {
    void callSendStep(StepRequest step);
}
