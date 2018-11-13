package com.pomohouse.launcher.fragment.about;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.library.WearerInfoUtils;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class AboutInfoFragment extends BaseFragment {
    @BindView(R.id.tvImei)
    TextView tvImei;
    @BindView(R.id.tvFirmware)
    TextView tvFirmware;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @Override
    protected List<Object> injectModules() {
        return null;
    }

    public static AboutInfoFragment newInstance() {
        AboutInfoFragment fragment = new AboutInfoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvImei.setText(" ".concat(WearerInfoUtils.getInstance().getImei(getActivity())));
        tvVersion.setText(" ".concat(WearerInfoUtils.getInstance().getPomoVersion()));
        tvFirmware.setText(" ".concat(Build.DISPLAY));
    }
}
