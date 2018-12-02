package com.pomohouse.launcher.di.module;

import android.app.Application;

import com.pomohouse.launcher.activity.fitness.FitnessActivity;
import com.pomohouse.launcher.activity.fitness.IFitnessView;
import com.pomohouse.launcher.activity.fitness.interactor.IFitnessInteractor;
import com.pomohouse.launcher.activity.fitness.presenter.FitnessPresenterImpl;
import com.pomohouse.launcher.activity.fitness.presenter.IFitnessPresenter;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/18/16 AD.
 */
@Module(
        injects = {FitnessActivity.class},
        addsTo = ApplicationModule.class
)
public class FitnessPresenterModule {
    IFitnessView view;

    @Singleton
    @Provides
    IFitnessPresenter provideEventAlertPresenter(IFitnessView view, IFitnessInteractor interactor) {
        return new FitnessPresenterImpl(view, interactor);
    }

    public FitnessPresenterModule(IFitnessView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IFitnessView provideEventAlert() {
        return view;
    }


    @Singleton
    @Provides
    public IFitnessPrefManager provideFitnessManager(Application app) {
        return new FitnessPrefManagerImpl(app);
    }

}
