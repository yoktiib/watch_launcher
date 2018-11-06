package com.pomohouse.launcher.fragment.theme;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseThemeFragment extends Fragment {
    private ThemePrefModel theme;
    private static final String THEME = "THEME";
    @BindView(R.id.imBgChange)
    ImageView imBgChange;
    IThemePrefManager iThemeManager;

    public static ChooseThemeFragment newInstance(ThemePrefModel theme) {
        ChooseThemeFragment fragment = new ChooseThemeFragment();
        Bundle args = new Bundle();
        args.putParcelable(THEME, theme);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iThemeManager = new ThemePrefManager(getContext());
    }

    @OnClick(R.id.imBgChange)
    void bgSelected() {
        closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_theme, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        theme = getArguments().getParcelable(THEME);
        int resId = getResources().getIdentifier(theme.getSample_theme(), "drawable", getActivity().getPackageName());
        Glide.with(this).load(resId).into(imBgChange);
    }

    private void closeActivity() {
        theme.setChanged(true);
        iThemeManager.addCurrentTheme(theme);
        Intent i = new Intent();
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();

    }
}
