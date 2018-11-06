package com.pomohouse.launcher.activity.settings.reset_factory;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.fragment.settings.reset_factory.ResetFactoryInfoFragment;

import java.util.List;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 2/3/2017 AD.
 */

public class ResetFactoryInfoActivity extends BaseActivity {
    @BindView(R.id.content)
    FrameLayout content;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_factory);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initInstances(ResetFactoryInfoFragment.newInstance());
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    private void initInstances(@NonNull Fragment main) {
        getSupportFragmentManager().beginTransaction()
                .add(content.getId(), main)
                .addToBackStack(main.getClass().getName())
                .commit();
    }
}
