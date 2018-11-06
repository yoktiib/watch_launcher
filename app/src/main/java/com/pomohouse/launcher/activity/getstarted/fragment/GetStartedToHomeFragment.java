package com.pomohouse.launcher.activity.getstarted.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class GetStartedToHomeFragment extends BaseFragment {

    @Override
    protected List<Object> injectModules() {
        return null;
    }

    public static GetStartedToHomeFragment newInstance() {
        GetStartedToHomeFragment fragment = new GetStartedToHomeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_to_home, container, false);
    }

    @OnClick(R.id.btnOK)
    public void onOKClick() {
        getActivity().finish();
    }

}
