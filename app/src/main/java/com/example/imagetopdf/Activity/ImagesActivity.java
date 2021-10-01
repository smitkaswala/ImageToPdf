package com.example.imagetopdf.Activity;

import static com.example.imagetopdf.ConstantsClass.constants.READ_PERMISSIONS;
import static com.example.imagetopdf.ConstantsClass.constants.REQUEST_CODE_FOR_READ_PERMISSION;
import static com.example.imagetopdf.ConstantsClass.constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static com.example.imagetopdf.ConstantsClass.constants.WRITE_PERMISSIONS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.imagetopdf.ImagesFeatures.ImageUtils;
import com.example.imagetopdf.R;
import com.example.imagetopdf.UtilsClass.PermissionsUtils;
import com.example.imagetopdf.UtilsClass.stringUtils;
import com.example.imagetopdf.databinding.ActivityImagesBinding;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;

public class ImagesActivity extends AppCompatActivity {

    ActivityImagesBinding binding;
    private boolean mIsButtonAlreadyClicked = false;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_images);

        Start();

        binding.text1.setOnClickListener(v -> {
            if (!mIsButtonAlreadyClicked){
                if (isStoragePermissionGranted()){
                    selectImage();
                    mIsButtonAlreadyClicked = true;
                }else{
                    getRuntimePermissions();
                }
            }
        });
    }

    private void Start(){

        binding.back.setOnClickListener(v -> { finish(); });

    }

    private boolean isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= 29) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 29) {
            PermissionsUtils.getInstance().handleRequestPermissionsResult(this, grantResults,
                    requestCode, REQUEST_CODE_FOR_READ_PERMISSION, this::selectImage);
        } else {
            PermissionsUtils.getInstance().handleRequestPermissionsResult(this, grantResults,
                    requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode){
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                mUnarrangedImagesUri.clear();
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                mUnarrangedImagesUri.addAll(mImagesUri);
                if (mImagesUri.size() > 0){
                    binding.tvNoOfImages.setText(String.format(this.getResources()
                    .getString(R.string.images_selected), mImagesUri.size()));
                    binding.tvNoOfImages.setVisibility(View.VISIBLE);
                    stringUtils.getInstance().showSnackbar(this,R.string.snackbar_images_added);
                    binding.text2.setEnabled(true);
                    //
                }

        }
    }

    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT < 29) {
            PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    WRITE_PERMISSIONS,
                    REQUEST_CODE_FOR_WRITE_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= 29) {
            PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    READ_PERMISSIONS,
                    REQUEST_CODE_FOR_READ_PERMISSION);
        }
    }

    private void selectImage(){
        ImageUtils.selectImages(this,INTENT_REQUEST_GET_IMAGES);
    }
}