package com.pomohouse.library.networks;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by sirawit on 4/25/2016 AD.
 */
public class ProgressDialogLoader extends ProgressDialog {
    private OnCancelClickListener mListener;

    public interface OnCancelClickListener {
        void onCancelRequestLoader(ProgressDialog progressDialogLoader);
    }

    public ProgressDialogLoader(Context context) {
        super(context);
        initDialog();
    }

    public ProgressDialogLoader(Context context, int theme) {
        super(context, theme);
        initDialog();
    }

    public void setOnCancel(OnCancelClickListener listener) {
        this.mListener = listener;
    }

    public void initDialog() {
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setMessage("Loading. Please wait...");
        setIndeterminate(true);
        setCanceledOnTouchOutside(false);
        setOnCancelListener(
                dialog -> {
                    if (mListener != null)
                        mListener.onCancelRequestLoader(this);
                });
    }
}
