package com.pomohouse.launcher.activity.pincode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PinCodeActivity extends BaseActivity {
    @BindView(R.id.tvPinCode)
    TextView tvPinCode;
    public static final String EXTRA_PIN_CODE = "PIN_CODE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            if (intent.getExtras().getString(EXTRA_PIN_CODE) != null)
                tvPinCode.setText(intent.getExtras().getString(EXTRA_PIN_CODE));
            else
                tvPinCode.setText("- - - -");
        }
    }

    @OnClick(R.id.btnClose)
    void onClickClose() {
        finish();
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
