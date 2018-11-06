package com.pomohouse.launcher.fragment.theme;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pomohouse.component.clock.ClockDrawable;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseThemeFragment;
import com.pomohouse.launcher.di.module.ThemeFragmentPresenterModule;
import com.pomohouse.launcher.fragment.theme.presenter.IThemeFragmentPresenter;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.theme.ThemePrefModel;
import com.pomohouse.launcher.models.EventDataInfo;

import org.joda.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by Admin on 9/26/2016 AD.
 */

public class ThemeAnalogFragment extends BaseThemeFragment implements IThemeFragmentView {

    @BindView(R.id.container)
    RelativeLayout container;
    /*@BindView(R.id.clock)
    ClockView clock;*/
    /*@BindView(R.id.tvHour)
    AppCompatTextView tvHour;
    @BindView(R.id.tvMinute)
    AppCompatTextView tvMinute;

    @BindView(R.id.tvHour2)
    AppCompatTextView tvHour2;
    @BindView(R.id.tvMinute2)
    AppCompatTextView tvMinute2;
    */
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

    ClockDrawable clockDrawable;

    @Inject
    IThemeFragmentPresenter presenter;

    @Inject
    INotificationManager notificationManager;

    private ThemePrefModel theme;

    public static ThemeAnalogFragment newInstance(ThemePrefModel themeId) {
        ThemeAnalogFragment fragment = new ThemeAnalogFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.e(THEME + ":" + theme.getPosition());
        if (theme == null)
            theme = new ThemePrefModel();
        switch (theme.getThemeId()) {
            case 3:
                return inflater.inflate(R.layout.fragment_theme_3, container, false);
            case 6:
                return inflater.inflate(R.layout.fragment_theme_6, container, false);
            default:
                return inflater.inflate(R.layout.fragment_theme_6, container, false);
        }
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onInitial();
        clockDrawable = new ClockDrawable(getResources(), Color.parseColor(theme.getHourColor()), Color.parseColor(theme.getMinuteColor()));
        clockDrawable.setAnimateDays(false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageDrawable(clockDrawable);
        int resId = getResources().getIdentifier(theme.getBackground(), "drawable", getActivity().getPackageName());
        container.setBackgroundResource(resId);
        tvDate.setTextColor(Color.parseColor(theme.getDataColor()));
        tvDay.setTextColor(Color.parseColor(theme.getDataColor()));
        presenter.onCheckNotificationIcon(notificationManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onCheckNotificationIcon(notificationManager);
    }

    private static final int UPDATE_TIME = 1;
    private static final int UPDATE_MINUTE = 2;
    private static final int UPDATE_DAY = 3;
    private static final int UPDATE_DATE = 4;
    private final LocalDateTime now = LocalDateTime.now().withTime(0, 0, 0, 0);
    private final Handler _handler = new Handler() {
        public void handleMessage(final Message msg) {
            String time;
            LocalDateTime current;
            switch (msg.what) {
                case UPDATE_TIME:
                    if (clockDrawable == null)
                        return;
                    int hour = msg.arg1;
                    int minute = msg.arg2;
                    current = now.plusHours(hour).plusMinutes(minute);
                    clockDrawable.start(current);
                    break;
                case UPDATE_DAY:
                    time = (String) msg.obj;
                    tvDay.setText(time);
                    break;
                case UPDATE_DATE:
                    time = (String) msg.obj;
                    tvDate.setText(time);
                    break;
            }
        }
    };

    @Override
    public void updateTime(int hour, int minute) {
        Timber.i(hour + ":" + minute);
        _handler.sendMessage(_handler.obtainMessage(UPDATE_TIME, hour, minute));
    }

    @Override
    protected void updateDate(int month, int day, int dayName) {
        _handler.sendMessage(_handler.obtainMessage(UPDATE_DAY, dayOfWeek[dayName > 0 ? dayName - 1 : dayName]));
        _handler.sendMessage(_handler.obtainMessage(UPDATE_DATE, day + " " + monthOfYear[month]/*getMonthFromInt().toUpperCase()*/));
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
}
