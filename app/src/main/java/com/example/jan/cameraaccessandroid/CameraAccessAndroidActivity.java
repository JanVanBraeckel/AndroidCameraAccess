package com.example.jan.cameraaccessandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class CameraAccessAndroidActivity extends AppCompatActivity {
    private static final String TAG = "CameraAccessAndroidActi";

    private Camera mCamera;
    private CameraPreview mPreview;
    private final int mDesiredCameraPreviewWidth = 1920;
    private final int MY_PERMISSION_REQUEST_USE_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
            Log.d(TAG, "Camera not available or in use.");
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initializeCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        int currentWidth = 0;
        int currentHeight = 0;
        boolean foundDesiredWidth = false;
        for (Camera.Size s : sizes) {
            if (s.width == mDesiredCameraPreviewWidth) {
                currentWidth = s.width;
                currentHeight = s.height;
                foundDesiredWidth = true;
                break;
            }
        }
        if (foundDesiredWidth)
            parameters.setPreviewSize(currentWidth, currentHeight);
        mCamera.setParameters(parameters);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_USE_CAMERA);
        }else{
            initCamera();
        }
    }

    private void initCamera(){
        mCamera = getCameraInstance();
        initializeCameraParameters();
        if(mCamera == null){
            Log.d(TAG, "Camera not available");
        }else{
            mPreview = new CameraPreview(this, mCamera);
            setContentView(mPreview);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_REQUEST_USE_CAMERA:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initCamera();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
}
