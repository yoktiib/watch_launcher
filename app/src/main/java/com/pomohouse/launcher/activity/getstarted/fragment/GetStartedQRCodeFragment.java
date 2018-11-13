package com.pomohouse.launcher.activity.getstarted.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.launcher.utils.QrGenerator;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class GetStartedQRCodeFragment extends BaseFragment {
    @BindView(R.id.img_qr_generated)
    ImageView img_qr_generated;

    private ErrorCorrectionLevel mEcc = ErrorCorrectionLevel.L;

    @Override
    protected List<Object> injectModules() {
        return null;
    }

    public static GetStartedQRCodeFragment newInstance() {
        GetStartedQRCodeFragment fragment = new GetStartedQRCodeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onGenerateClick();
    }

    void onGenerateClick() {
        try {
            int _color = Color.rgb(0, 0, 0);
            int _bgColor = Color.rgb(255, 255, 255);

            Bitmap qrCode = new QrGenerator.Builder()
                    .content(WearerInfoUtils.getInstance().getImei(getContext()))
                    .qrSize(500)
                    /*.margin(2)*/
                    .color(_color)
                    .bgColor(_bgColor)
                    .ecc(mEcc)
                    .overlay(/*mOverlayEnabled ? BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_brightness) :*/ null)
                    /*.overlaySize(100)
                    .overlayAlpha(255)
                    .overlayXfermode(PorterDuff.Mode.SRC_ATOP)*/
                        /*.footNote(WearerInfoUtils.getInstance(getContext()).getIMEI())*/
                    .encode();

            img_qr_generated.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();

        }
    }


    private boolean checkEmpty(EditText e) {
        return TextUtils.isEmpty(e.getText());
    }

    private int getInputtedInt(EditText edt, int def) {
        try {
            String s = edt.getText().toString();

            if (TextUtils.isEmpty(s))
                return def;
            else
                return Integer.parseInt(s);

        } catch (Exception e) {
            e.printStackTrace();
            return def;
        }
    }
}
