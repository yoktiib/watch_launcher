package com.pomohouse.launcher.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sirawit on 8/28/16 AD.
 */
public class ConfirmDialogFragment extends BaseDialogFragment {
    public interface ConfirmDialogListener {
        void onYesDialogClick();

        void onNoDialogClick();
    }

    public static final int _KEY_POWER_OFF = 1;
    public static final int _KEY_RESTART = 0;
    public static final String TYPE = "TYPE";

    private ConfirmDialogListener confirmDialogListener;
    @BindView(R.id.ivSettingProfile)
    AppCompatImageView ivSettingProfile;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    public static ConfirmDialogFragment newInstance(ConfirmDialogListener mListener, int type) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.confirmDialogListener = mListener;
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null)
            window.setBackgroundDrawableResource(R.color.waffle_background_color);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null)
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getArguments().getInt(TYPE) == _KEY_RESTART) {
                ivSettingProfile.setImageResource(R.drawable.ic_restart);
                tvTitle.setText(getString(R.string.text_title_restart));
            } else {
                ivSettingProfile.setImageResource(R.drawable.ic_shutdown);
                tvTitle.setText(getString(R.string.text_title_power_off));
            }
        } catch (Exception ignore) {
        }
    }

    @OnClick(R.id.btnCancel)
    void onCancelFriend() {
        if (confirmDialogListener != null)
            confirmDialogListener.onNoDialogClick();
        dismiss();
    }

    @OnClick(R.id.btnConfirm)
    void onAcceptFriend() {
        if (confirmDialogListener != null)
            confirmDialogListener.onYesDialogClick();
        dismiss();
    }
}
