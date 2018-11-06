package com.pomohouse.launcher.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseDialogFragment;

import java.io.IOException;

import butterknife.BindView;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Admin on 11/7/2016 AD.
 */

public class SOSDialogFragment extends BaseDialogFragment {
    GifDrawable gifFromResource;
    private OnCloseClickListener dialogListener;

    public interface OnCloseClickListener {
        void onCloseClick(DialogFragment dialog);
    }

    public static SOSDialogFragment newInstance(OnCloseClickListener listener) {
        SOSDialogFragment fragment = new SOSDialogFragment();
        fragment.dialogListener = listener;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.ivSos)
    ImageView ivSos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_sos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            gifFromResource = new GifDrawable(getResources(), R.drawable.sos_anim);
            ivSos.setVisibility(View.VISIBLE);
            ivSos.setImageDrawable(gifFromResource);
            gifFromResource.addAnimationListener(loopNumber -> closeDialog());
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 500, 500, 500, 500, 500,500, 500, 500, 500, 500};
            v.vibrate(pattern, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        if (dialogListener != null)
            dialogListener.onCloseClick(this);
        if (gifFromResource != null)
            gifFromResource.stop();
        if (getDialog() != null)
            dismiss();
    }
}
