package com.pomohouse.launcher.activity.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.camera.presenter.CameraPresenter;
import com.pomohouse.launcher.activity.camera.presenter.ImageModelRequest;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.library.WearerInfoUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CameraPreviewActivity extends BaseActivity implements ICameraView {
    private CameraPresenter cameraPresenter;
    @BindView(R.id.tvSave)
    TextView tvSave;
    @BindView(R.id.tvNo)
    TextView tvNo;
    @BindView(R.id.img_view)
    ImageView img_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        ButterKnife.bind(this);
        File myFile = new File(getIntent().getStringExtra("PICTURE"));
        Timber.e(myFile.getAbsolutePath());
        cameraPresenter = new CameraPresenter(this);
        ImageModelRequest imageModelRequest = new ImageModelRequest();
        imageModelRequest.setImei(WearerInfoUtils.getInstance().getImei(this));
        imageModelRequest.setImage(myFile);
        Glide.with(this).load(myFile).centerCrop().into(img_view);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSave.setEnabled(false);
                cameraPresenter.onSendPictureToStore(imageModelRequest);
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNo.setEnabled(false);
                finish();
            }
        });
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    @Override
    public void onSuccessUploadPicture() {
        //Toast.makeText(mContext, "Upload Photo Successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailureUploadPicture() {
        //Toast.makeText(mContext, "Upload Photo Not Success", Toast.LENGTH_SHORT).show();


        finish();
    }
}
