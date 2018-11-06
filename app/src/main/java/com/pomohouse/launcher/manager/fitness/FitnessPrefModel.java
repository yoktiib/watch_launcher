package com.pomohouse.launcher.manager.fitness;

import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by Admin on 9/12/16 AD.
 */
public class FitnessPrefModel {
    private int lastStep = 0;
    private int lastStepSync = 0;
    private int stepForSync = 0;

    private int totalStep = 0;
    private float distance;
    private float speed;
    private float calories;
    private int pace;
    private String date;

    public FitnessPrefModel() {
        totalStep = 0;
        lastStepSync = 0;
        stepForSync = 0;
        lastStep = 0;
        distance = 0;
        speed = 0;
        calories = 0;
        pace = 0;
        date = "";
    }

    public int getLastStepSync() {
        return lastStepSync;
    }

    public void setLastStepSync(int lastStepSync) {
        this.lastStepSync = lastStepSync;
    }

    public void getSyncStep() {
        stepForSync = totalStep - lastStepSync;
        lastStepSync = totalStep;
    }

    public int getStepForSync() {
        return stepForSync;
    }

    public void setStepForSync(int stepForSync) {
        this.stepForSync = stepForSync;
    }

    public int getLastStep() {
        return lastStep;
    }

    public FitnessPrefModel setLastStep(int lastStep) {
        this.lastStep = lastStep;
        return this;
    }

    public int calculateStepSync(int curr, String currDate) {
        if (currDate == null || currDate.isEmpty())
            currDate = getCurrentDate();
        Timber.i("check last(" + lastStep + ") > curr(" + curr + ")");
        if (lastStep > curr) {
            totalStep += curr;
            lastStep = curr;
            Timber.i("==> total(" + totalStep + ")+ " + curr + "last(" + lastStep);
        } else {
            if (!currDate.equalsIgnoreCase(date)) {
                totalStep = 0;
                lastStepSync = 0;
                stepForSync = 0;
            }
            date = currDate;
            Timber.i("total = total(" + totalStep + ")+curr(" + curr + ")-last(" + lastStep);
            totalStep = totalStep + (curr - lastStep);
            lastStep = curr;
            Timber.i("==> total(" + totalStep + "), last(" + lastStep);
        }
        return totalStep;
    }

    public String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        return yy + "-" + mm + "-" + dd;
    }

    public int getTotalStep() {
        return totalStep;
    }

    public void setTotalStep(int totalStep) {
        this.totalStep = totalStep;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public String getDate() {
        return date;
    }

    public FitnessPrefModel setDate(String date) {
        this.date = date;
        return this;
    }


}
