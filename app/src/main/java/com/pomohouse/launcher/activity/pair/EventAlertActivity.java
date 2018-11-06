package com.pomohouse.launcher.activity.pair;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.utils.EventConstant;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.EventMember;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventAlertActivity extends BaseActivity {
    public static final String EVENT_EXTRA = "EVENT_EXTRA";
    private EventDataInfo eventData;
    @BindView(R.id.activity_event)
    LinearLayout activity_event;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.ivStatus)
    AppCompatImageView ivStatus;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_event_pair);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        Timber.e("Init Event");
        if (bundle != null) {
            eventData = (EventDataInfo) bundle.getSerializable(EVENT_EXTRA);
            Timber.e("Event Have");
        }
        if (eventData == null)
            finish();
        Timber.e("Init View");
        switch (eventData.getEventCode()) {
            case EventConstant.EventPair.EVENT_PAIR_CODE:
                pairWatchEventReceived(eventData);
                break;
            case EventConstant.EventPair.EVENT_UN_PAIR_CODE:
                unPairWatchEventReceived(eventData);
                break;
        }
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    public void pairWatchEventReceived(EventDataInfo eventData) {
        activity_event.setBackgroundColor(ContextCompat.getColor(this, R.color.waffle_blue_02_color));
        tvStatus.setText(getString(R.string.pair_text));
        if (eventData == null)
            return;
        if (eventData.getSenderInfo() == null || eventData.getSenderInfo().isEmpty())
            return;
        this.setUserEvent(eventData.getSenderInfo());
        ivStatus.setImageDrawable(statusConvertToDrawable(R.drawable.notification_yes));
        CombineObjectConstance.getInstance().getContactEntity().setContactSynced(false);
    }

    @OnClick(R.id.activity_event)
    void onClickEvent(View view) {
        finish();
    }


    public void unPairWatchEventReceived(EventDataInfo eventData) {
        activity_event.setBackgroundColor(ContextCompat.getColor(this, R.color.waffle_orange_01_color));
        tvStatus.setText(getString(R.string.un_pair_text));
        if (eventData == null)
            return;
        if (eventData.getSenderInfo() == null || eventData.getSenderInfo().isEmpty())
            return;
        this.setUserEvent(eventData.getSenderInfo());
        ivStatus.setImageDrawable(statusConvertToDrawable(R.drawable.notification_no));
        CombineObjectConstance.getInstance().getContactEntity().setContactSynced(false);
    }

    public void setUserEvent(String userData) {
        try {
            EventMember fromUser = new Gson().fromJson(userData, EventMember.class);
            if (fromUser == null)
                return;
            String name = fromUser.getName();
            if (name.length() > 12) {
                name = name.substring(0, 12) + "..";
            }
            tvName.setText(name);

        } catch (Exception ignore) {

        }
    }

    private Drawable statusConvertToDrawable(int resources) {
        return VectorDrawableCompat.create(getResources(), resources, getTheme());
    }
}
