/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pomohouse.launcher.activity.fitness;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.fitness.presenter.IFitnessPresenter;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.di.module.FitnessPresenterModule;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.FitnessPrefModel;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.launcher.api.requests.StepRequest;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FitnessActivity extends BaseActivity implements IFitnessView {
    @BindView(R.id.tvStep)
    AppCompatTextView tvStep;
    @Inject
    IFitnessPrefManager fitnessPrefManager;
    @Inject
    IFitnessPresenter presenter;
    private String date = "";
    private boolean isUpdated = false;
    SensorManager mSensorManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fitness);
        ButterKnife.bind(this);
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        fitnessPrefManager = new FitnessPrefManagerImpl(this);
        tvStep.setText(String.valueOf(fitnessPrefManager.getFitness().getTotalStep()));
        date = fitnessPrefManager.getFitness().getCurrentDate();
        isUpdated = false;
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            Timber.i("Sensor Type : " + sensor.getType());
            if (Sensor.TYPE_STEP_COUNTER == sensor.getType()) {
                int curr = (int) event.values[0];
                Timber.i("curr => " + curr);
                FitnessPrefModel fitnessPrefModel = fitnessPrefManager.getFitness();
                fitnessPrefModel.calculateStepSync(curr, date);
                fitnessPrefModel.getSyncStep();
                fitnessPrefManager.addFitness(fitnessPrefModel);
                if (!isUpdated) {
                    isUpdated = true;
                    StepRequest stepReq = new StepRequest(fitnessPrefManager.getFitness().getStepForSync());
                    stepReq.setImei(WearerInfoUtils.getInstance().getImei());
                    presenter.sendStep(stepReq);
                }
                tvStep.setText(String.valueOf(fitnessPrefManager.getFitness().getTotalStep()));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            mSensorManager.registerListener(sensorEventListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new FitnessPresenterModule(this));
    }

}