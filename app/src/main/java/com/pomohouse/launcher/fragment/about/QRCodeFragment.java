package com.pomohouse.launcher.fragment.about;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.fragment.about.presenter.OnQRCodeListener;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.launcher.utils.QrGenerator;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Admin on 9/20/16 AD.
 */
public class QRCodeFragment extends BaseFragment implements OnQRCodeListener {
    @BindView(R.id.img_qr_generated)
    ImageView img_qr_generated;
    private String code = null;
    @BindView(R.id.spin_kit)
    SpinKitView spin_kit;
    @BindView(R.id.boxQRCode)
    FrameLayout boxQRCode;


    private ErrorCorrectionLevel mEcc = ErrorCorrectionLevel.L;

    @Override
    protected List<Object> injectModules() {
        return null;
    }

    public static QRCodeFragment newInstance() {
        QRCodeFragment fragment = new QRCodeFragment();
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
        if (code == null || code.isEmpty()) {
            spin_kit.setVisibility(View.VISIBLE);
            boxQRCode.setVisibility(View.GONE);
            TCPSocketServiceProvider.getInstance().sendMessageQRCode(this, CMDCode.CMD_QR_CODE, "{}");
        } else {
            boxQRCode.setVisibility(View.VISIBLE);
            spin_kit.setVisibility(View.GONE);
            onGenerateClick(code);
        }
    }

    void onGenerateClick(String code) {
        try {
            int _color = Color.rgb(0, 0, 0);
            int _bgColor = Color.rgb(255, 255, 255);

            Bitmap qrCode = new QrGenerator.Builder().content(code).qrSize(500)
                    /*.margin(2)*/.color(_color).bgColor(_bgColor).ecc(mEcc).overlay(/*mOverlayEnabled ? BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_brightness) :*/ null)
                    /*.overlaySize(100)
                    .overlayAlpha(255)
                    .overlayXfermode(PorterDuff.Mode.SRC_ATOP)*/
                    /*.footNote(WearerInfoUtils.getInstance(getContext()).getIMEI())*/.encode();
            img_qr_generated.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

/*
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
    }*/

    @Override
    public void onQRCodeFailure(MetaDataNetwork error) {
        spin_kit.setVisibility(View.GONE);
        boxQRCode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQRCodeSuccess(MetaDataNetwork metaData, QRCodeModel readyModel) {
        spin_kit.setVisibility(View.GONE);
        boxQRCode.setVisibility(View.VISIBLE);
        onGenerateClick(code = readyModel.getCode());
    }
}
