package com.pomohouse.launcher.manager.fitness;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface IFitnessPrefManager {

    void addFitness(FitnessPrefModel fitnessPref);

    FitnessPrefModel getFitness();

    void removeFitness();
}
