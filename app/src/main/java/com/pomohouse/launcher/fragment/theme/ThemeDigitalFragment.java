package com.pomohouse.launcher.fragment.theme;

import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseThemeFragment;
import com.pomohouse.launcher.di.module.ThemeFragmentPresenterModule;
import com.pomohouse.launcher.fragment.theme.presenter.IThemeFragmentPresenter;
import com.pomohouse.launcher.lock_screen.LockScreenService;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.theme.ThemePrefModel;
import com.pomohouse.launcher.models.EventDataInfo;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by Admin on 9/26/2016 AD.
 */

public class ThemeDigitalFragment extends BaseThemeFragment implements IThemeFragmentView {

    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.tvHour)
    AppCompatTextView tvHour;
    @BindView(R.id.tvMinute)
    AppCompatTextView tvMinute;

    @BindView(R.id.tvHour2)
    AppCompatTextView tvHour2;
    @BindView(R.id.tvMinute2)
    AppCompatTextView tvMinute2;

    @BindView(R.id.tvAmPm)
    AppCompatTextView tvAmPm;

    @BindView(R.id.tvDay)
    AppCompatTextView tvDay;
    @BindView(R.id.tvDate)
    AppCompatTextView tvDate;

    @BindView(R.id.ivCallNotification)
    AppCompatImageView ivCallNotification;

    @BindView(R.id.ivMessageNotification)
    AppCompatImageView ivMessageNotification;

    @BindView(R.id.ivStatusDNotification)
    AppCompatImageView ivStatusDNotification;
    @Inject
    IThemeFragmentPresenter presenter;

    @Inject
    INotificationManager notificationManager;

    private ThemePrefModel theme;

    //yangyu add
    private String currentTimeFormat = "24";
//    private ContentResolver mContentResolver;

    private ContentObserver timeFormatObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            currentTimeFormat = Settings.System.getString(getActivity().getContentResolver(), Settings.System.TIME_12_24);
            if (currentTimeFormat.equals("24")) {
                LockScreenService.is24HourFormat = true;
            } else if (currentTimeFormat.equals("12")) {
                LockScreenService.is24HourFormat = false;
            }
        }
    };

    public static ThemeDigitalFragment newInstance(ThemePrefModel themeId) {
        ThemeDigitalFragment fragment = new ThemeDigitalFragment();
        Bundle args = new Bundle();
        args.putParcelable(THEME, themeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getArguments().getParcelable(THEME);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onCheckNotificationIcon(notificationManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.e(THEME + ":" + theme.getPosition());
        themeDefault = false;
        if (theme == null)
            theme = new ThemePrefModel();
        switch (theme.getThemeId()) {
            case 1:
                return inflater.inflate(R.layout.fragment_theme_1, container, false);
            case 2:
                return inflater.inflate(R.layout.fragment_theme_2, container, false);
            case 4:
                return inflater.inflate(R.layout.fragment_theme_4, container, false);
            case 5:
                themeDefault = true;
                themeDefault = theme.getPosition() == 1 && theme.getThemeId() == 5;//normal
//                themeDefault = theme.getPosition() <= 2 && theme.getThemeId() == 5;//frisco
                return inflater.inflate(R.layout.fragment_theme_5, container, false);
            case 7:
                return inflater.inflate(R.layout.fragment_theme_7, container, false);
            default:
                return inflater.inflate(R.layout.fragment_theme_5, container, false);
        }
    }

    private boolean themeDefault = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onInitial();
        if (themeDefault) {
            LinearLayout boxTime = (LinearLayout) view.findViewById(R.id.boxTime);
            if (boxTime != null)
                boxTime.setBackground(getResources().getDrawable(R.drawable.shape_rectangle));

        }
        if (theme.getThemeId() == 5) {
            TextView tvPoral = (TextView) view.findViewById(R.id.tvPoral);
            if (tvPoral != null)
                tvPoral.setTextColor(Color.parseColor(theme.getDosColor()));
        }
        int resId = getResources().getIdentifier(theme.getBackground(), "drawable", getActivity().getPackageName());
        container.setBackgroundResource(resId);
        tvDate.setTextColor(Color.parseColor(theme.getDataColor()));
        tvDay.setTextColor(Color.parseColor(theme.getDayNameColor()));
        tvHour.setTextColor(Color.parseColor(theme.getHourColor()));
        tvHour2.setTextColor(Color.parseColor(theme.getHourColor()));
        tvMinute.setTextColor(Color.parseColor(theme.getMinuteColor()));
        tvMinute2.setTextColor(Color.parseColor(theme.getMinuteColor()));
        presenter.onCheckNotificationIcon(notificationManager);

        tvAmPm.setVisibility(View.INVISIBLE);

        currentTimeFormat = Settings.System.getString(getActivity().getContentResolver(), Settings.System.TIME_12_24);
        updateTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        getActivity().getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.TIME_12_24),
                false, timeFormatObserver);
        if (currentTimeFormat == null)
            return;
        if (currentTimeFormat.equals("24"))
            LockScreenService.is24HourFormat = true;
        else if (currentTimeFormat.equals("12"))
            LockScreenService.is24HourFormat = false;
    }


    private static final int UPDATE_HOUR = 1;
    private static final int UPDATE_MINUTE = 2;
    private static final int UPDATE_DAY = 3;
    private static final int UPDATE_DATE = 4;
    private final Handler _handler = new Handler(msg -> {
        String time, day, date;
        switch (msg.what) {
            case UPDATE_HOUR:
                time = (String) msg.obj;
                tvHour.setText(String.valueOf(time.charAt(0)));
                tvHour2.setText(String.valueOf(time.charAt(1)));
                break;
            case UPDATE_MINUTE:
                time = (String) msg.obj;
                tvMinute.setText(String.valueOf(time.charAt(0)));
                tvMinute2.setText(String.valueOf(time.charAt(1)));
                break;
            case UPDATE_DAY:
                day = (String) msg.obj;
                tvDay.setText(day);
                break;
            case UPDATE_DATE:
                date = (String) msg.obj;
                tvDate.setText(date);
                break;
        }
        return true;
    });

    @Override
    public void updateTime(int hour, int minute) {
        Timber.i(hour + ":" + minute);
        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
        tvAmPm.setText(hour >= 12 ? getResources().getString(R.string.text_pm) : getResources().getString(R.string.text_am));
        if (currentTimeFormat == null)
            return;
        if (currentTimeFormat.equals("24")) {
            tvAmPm.setVisibility(View.INVISIBLE);
        } else if (currentTimeFormat.equals("12")) {
            tvAmPm.setVisibility(View.VISIBLE);
            hour = hour < 13 ? hour : (hour - 12);
            hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        }

        _handler.sendMessage(_handler.obtainMessage(UPDATE_HOUR, hourStr));
        _handler.sendMessage(_handler.obtainMessage(UPDATE_MINUTE, minuteStr));
    }

    @Override
    protected void updateDate(int month, int day, int dayName) {
        _handler.sendMessage(_handler.obtainMessage(UPDATE_DAY, dayOfWeek[dayName > 0 ? dayName - 1 : dayName]));
        _handler.sendMessage(_handler.obtainMessage(UPDATE_DATE, day + " " + monthOfYear[month]));
    }

    @Override
    public void eventReceived(EventDataInfo event) {
        presenter.eventReceived(event);
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new ThemeFragmentPresenterModule(this));
    }

    @Override
    public void updateTimezone() {
        super.timezoneChange();
    }


    @Override
    public void notificationVolumeMute(boolean isShow) {
        ivStatusDNotification.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void notificationMessage(boolean isShow) {
        ivMessageNotification.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void notificationCall(boolean isShow) {
        ivCallNotification.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(timeFormatObserver);
    }
}