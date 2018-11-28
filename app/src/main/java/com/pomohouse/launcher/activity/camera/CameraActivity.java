package com.pomohouse.launcher.activity.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.camera.presenter.CameraPresenter;
import com.pomohouse.launcher.activity.camera.presenter.ImageModelRequest;
import com.pomohouse.launcher.activity.camera.presenter.OnUploadImageListener;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.models.WearerInfo;
import com.pomohouse.library.WearerInfoUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CameraActivity extends BaseActivity implements ICameraView{
    private CameraPresenter cameraPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //  takePhoto();
        cameraPresenter = new CameraPresenter(this);
        openImageInAssets("image.jpg");

    }

    protected void openImageInAssets(String imageName) {
        AssetManager assetManager = getAssets();
        InputStream fileStream = null;

        try {
            fileStream = assetManager.open(imageName);
            //                  BitmapFactory.Options bfo = new BitmapFactory.Options();
            //                  bfo.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //                  Bitmap bitmap = BitmapFactory.decodeStream(fileStream, null, bfo);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = fileStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            ImageModelRequest imageModelRequest = new ImageModelRequest();
            imageModelRequest.setImei(WearerInfoUtils.getInstance().getImei(this));
            imageModelRequest.setImage(buffer.toByteArray());
            cameraPresenter.onSendPictureToStore(new OnUploadImageListener() {
                @Override
                public void onUploadImageSuccess() {
                    Toast.makeText(mContext, "onUploadImageSuccess", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUploadImageFailure() {
                    Toast.makeText(mContext, "onUploadImageFailure", Toast.LENGTH_SHORT).show();
                }
            },imageModelRequest);
//            Bitmap bitmap = BitmapFactory.decodeStream(fileStream);
            // Convert bitmap to Base64 encoded image for web
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // to get image extension file name split the received
//            int fileExtensionPosition = imageName.lastIndexOf('.');
//            String fileExtension = imageName.substring(fileExtensionPosition + 1);
            //                  Log.d(IConstants.TAG,"fileExtension: " + fileExtension);
//
//            if (fileExtension.equalsIgnoreCase("png")) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                //                      Log.d(IConstants.TAG,"fileExtension is PNG");
//            } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                //                      Log.d(TAG,"fileExtension is JPG");
//            }
//
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//            String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //encodedImageBase64 = "data:image/png;base64," + imgageBase64;

        } catch (IOException e) {
            e.printStackTrace();
            //return encodedImageBase64 = "";
        } finally {
            //Always clear and close
            try {
                if (fileStream != null) {
                    fileStream.close();
                    fileStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//      Log.d(TAG,"encodedImageBase64: " + encodedImageBase64);
        //return encodedImageBase64;
    }

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "jollyPic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PICTURE:
                Toast.makeText(mContext, "Show", Toast.LENGTH_SHORT).show();
                //if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageUri;
                getContentResolver().notifyChange(selectedImage, null);
                //Toast.makeText(mContext, "Have", Toast.LENGTH_SHORT).show();
                ImageView imageView = (ImageView) findViewById(R.id.view);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);

                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }
                //}
        }
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    @Override
    public void onSuccessUploadPicture() {

    }

    @Override
    public void onFailureUploadPicture() {

    }
}
