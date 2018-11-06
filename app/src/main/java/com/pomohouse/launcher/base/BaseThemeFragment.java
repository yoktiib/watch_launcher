/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.pomohouse.launcher.base;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.R;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.networks.ProgressDialogLoader;
import com.pomohouse.launcher.broadcast.callback.TimeTickChangedListener;
import com.pomohouse.launcher.broadcast.receivers.DeviceActionReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;


/**
 * Base {@link android.app.Fragment} class for every fragment in this application.
 */
public abstract class BaseThemeFragment extends Fragment {
    protected final static String THEME = "THEME";
    private ObjectGraph activityGraph;
    private ProgressDialogLoader dialogLoading;
    protected Context mContext = ActivityContextor.getInstance().getContext();
    protected String[] dayOfWeek, monthOfYear;
    private int hours = 0;
    private int minutes = 0;
    private int dayNumber = 0;
    private Calendar calendar;

    protected abstract void updateTime(int hour, int minute);

    protected abstract void updateDate(int month, int day, int dayName);

    public abstract void eventReceived(EventDataInfo event);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProgressDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<Object> modules = injectModules();
        if (modules != null) {
            activityGraph = ((POMOWatchApplication) getActivity().getApplication()).createScopedGraph(modules.toArray());
            activityGraph.inject(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTime(hours, minutes);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        injectViews(view);
        dayOfWeek = getResources().getStringArray(R.array.dayOfWeekNameArr);
        monthOfYear = getResources().getStringArray(R.array.monthOfYearNameArr);
        //DeviceActionReceiver.getInstance().addTimeTickChangedListener(timeTickChangedListener);
        DeviceActionReceiver.getInstance().setThemeTimeTickChangedListener(timeTickChangedListener);
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        updateDay();
        updateTime(hours, minutes);
    }


    TimeTickChangedListener timeTickChangedListener = () -> {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        updateTime(hours, minutes);
        int currentDayNumber = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayNumber != currentDayNumber)
            updateDay();
    };

    public void timezoneChange() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        updateDay();
        updateTime(hours, minutes);
    }

    public void displayLoadingDialog() {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || getActivity().isFinishing())
                return;
            dialogLoading.show();
        });
    }

    public void displayLoadingDialog(ProgressDialogLoader.OnCancelClickListener listener) {
        new Handler().post(() -> {
            hideKeyboard();
            if (dialogLoading == null || getActivity().isFinishing())
                return;
            dialogLoading.setOnCancel(listener);
            dialogLoading.show();
        });
    }

    public void hideLoadingDialog() {
        if (dialogLoading == null)
            return;
        if (dialogLoading.isShowing())
            dialogLoading.dismiss();
    }

    public void hideKeyboard() {
        if (getView() == null)
            return;
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    void updateDay() {
        dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
        int months = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        updateDate(months, dayNumber, day);
    }

    /**
     * Replace every field annotated with ButterKnife annotations like @InjectView with the proper
     * value.
     *
     * @param view to extract each widget injected in the fragment.
     */
    private void injectViews(final View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityGraph = null;
//        DeviceActionReceiver.getInstance().removeTimeTickChangedListener(timeTickChangedListener);
        DeviceActionReceiver.getInstance().removeThemeTimeTickChangedListener();
    }

    protected abstract List<Object> injectModules();


    public void initProgressDialog() {
        dialogLoading = new ProgressDialogLoader(getActivity());

    }


}
