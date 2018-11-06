package com.pomohouse.launcher.activity.getstarted.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.di.module.PinCodePresenterModule;
import com.pomohouse.launcher.fragment.about.IPinCodeView;
import com.pomohouse.launcher.fragment.about.presenter.IPinCodePresenter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class GetStartedPinCodeFragment extends BaseFragment implements IPinCodeView {
    @Inject
    IPinCodePresenter presenter;
    @BindView(R.id.btnGetCode)
    Button btnGetCode;
    @BindView(R.id.tvPinCode)
    TextView tvPinCode;

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new PinCodePresenterModule(this));
    }

    public static GetStartedPinCodeFragment newInstance() {
        GetStartedPinCodeFragment fragment = new GetStartedPinCodeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pin_code, container, false);
    }

    @OnClick(R.id.btnGetCode)
    void onCLickGetCode() {
        if (isNetworkAvailable())
            presenter.requestGetCode();
        else
            Toast.makeText(getActivity(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetPinSuccess(PinCodeModel readyModel) {
        tvPinCode.setText(readyModel.getCode());
    }

    @Override
    public void enableGetCodeButton() {
        btnGetCode.setEnabled(true);
    }

    @Override
    public void disableGetCodeButton() {
        btnGetCode.setEnabled(false);
    }
}
