package com.example.imagetopdf.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.divyanshu.colorseekbar.ColorSeekBar;
import com.example.imagetopdf.Adpters.CropAdapter;
import com.example.imagetopdf.Adpters.FontColorAdapter;
import com.example.imagetopdf.Adpters.FontStyleAdapter;
import com.example.imagetopdf.Class.CallStickerView;
import com.example.imagetopdf.Class.CrpModel;
import com.example.imagetopdf.Class.DrawingView;
import com.example.imagetopdf.Class.RotateTransformation;
import com.example.imagetopdf.InterFace.ColorInterface;
import com.example.imagetopdf.InterFace.CropInterface;
import com.example.imagetopdf.InterFace.FontStyleInterface;
import com.example.imagetopdf.R;
import com.example.imagetopdf.UtilsClass.Utils;
import com.example.imagetopdf.databinding.ActivityEditImageBinding;
import com.example.imagetopdf.databinding.TextDailogBinding;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.TextSticker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditImageActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityEditImageBinding binding;
    CallStickerView callStickerView;
    Bitmap mFilterBitmap;
    CropInterface cropInterface;
    DrawingView mDrawingView;
    FontColorAdapter mAdapter;
    FontStyleAdapter mFontAdapter;
    private CropAdapter cropAdapter;
    FontStyleInterface fontStyleInterface;
    ColorInterface colorInterface;
    Boolean IsDoodle = false;
    String file, newText = "", mStyleList[];
    int[] colors;

    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_image);

        file = getIntent().getStringExtra("file");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(EditImageActivity.this);
        Start();


        IFontArrayList();
        IColorArrayList();


        fontStyleInterface = new FontStyleInterface() {
            @Override
            public void Font(Typeface typeface) {
                Sticker sticker = callStickerView.GetStickerView();
                if (sticker != null) {
                    ((TextSticker) sticker).setTypeface(typeface);
                    callStickerView.UpdateStickerDetail(sticker);
                } else {
                    Toast.makeText(getApplicationContext(), "Click on text to apply font", Toast.LENGTH_LONG).show();
                }
            }
        };

        cropInterface = new CropInterface() {
            @Override
            public void CropRatio(int x, int y, int p) {
                if (p == 0)
                    binding.cropImageView.setCropMode(com.isseiaoki.simplecropview.CropImageView.CropMode.FREE);
                else
                    binding.cropImageView.setCustomRatio(x, y);
            }
        };

        colorInterface = new ColorInterface() {
            @Override
            public void ColorCode(int color) {
                Sticker sticker = callStickerView.GetStickerView();
                if (sticker != null) {
                    ((TextSticker) sticker).setTextColor(color);
                    callStickerView.UpdateStickerDetail(sticker);
                } else {
                    Toast.makeText(getApplicationContext(), "Click on text to fill color", Toast.LENGTH_LONG).show();
                }
            }
        };

        binding.colorBarDoodle.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int i) {
                fireAnalytics("Edit Image", "Doodle color");
                Utils.mDoodleColor = i;
            }
        });

        mFontAdapter = new FontStyleAdapter(EditImageActivity.this, mStyleList, fontStyleInterface);
        mAdapter = new FontColorAdapter(EditImageActivity.this, colors, colorInterface);


    }


    public Bitmap BitmapFromPath(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(path, bmOptions);
    }

    private void Start() {

        Glide.with(getApplicationContext())
                .load(file)
                .into(binding.image);
//        String filePath = file;
//        Bitmap bit = BitmapFactory.decodeFile(filePath);
//        binding.image.setImageBitmap(bit);

        binding.back.setOnClickListener(this);
        binding.rotate.setOnClickListener(this);
        binding.crop.setOnClickListener(this);
        binding.doodle.setOnClickListener(this);
        binding.text.setOnClickListener(this);
        binding.mAddText.setOnClickListener(this);
        binding.stickerView.setOnClickListener(this);
        binding.icClose.setOnClickListener(this);
        binding.done.setOnClickListener(this);
        binding.mTextStyle.setOnClickListener(this);
        binding.mTextColor.setOnClickListener(this);

        callStickerView = new CallStickerView(this, binding.stickerView);
        callStickerView.IStickerView();
        callStickerView.IStickerEvent();
        callStickerView.ShowBorder();

        binding.mStyleRec.setVisibility(View.GONE);
        binding.mStyleRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                finish();
                break;

            case R.id.rotate:
                binding.done.setVisibility(View.VISIBLE);
                binding.icRotate.setImageResource(R.drawable.ic_rotate_on);
                binding.icCrop.setImageResource(R.drawable.ic_crop);
                binding.icDoodle.setImageResource(R.drawable.ic_doodle);
                binding.icText.setImageResource(R.drawable.ic_aa_text);
                binding.colorBarDoodle.setVisibility(View.GONE);
                binding.back.setVisibility(View.GONE);
                binding.icClose.setVisibility(View.VISIBLE);
                new AsyRotateImage().execute((Void[]) null);
                break;

            case R.id.crop:
//                Utils.IsCropped = true;
//                Glide.with(getApplicationContext())
//                        .load(file)
//                        .into(binding.cropImageView);
//                binding.mStyleRec.setVisibility(View.VISIBLE);
//                cropAdapter = new CropAdapter(this, getCrpItem(), cropInterface);
//                binding.mStyleRec.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
//                binding.mStyleRec.setAdapter(cropAdapter);
//
//                Bitmap bitmap = Utils.mainBitmap;
//
//                binding.cropImageView.setImageBitmap(bitmap);
//                binding.cropImageView.setCropMode(com.isseiaoki.simplecropview.CropImageView.CropMode.FREE);

//                Intent intent = new Intent(this, CropActivity.class);
//                intent.putExtra("File", file);
//                startActivityForResult(intent, 100);
                String filePath = file;
                Bitmap bit = BitmapFactory.decodeFile(filePath);
                Utils.IsCropped = true;
                StartCropImage(getImageToUri(EditImageActivity.this, bit));
                callStickerView.HideBorder();
                binding.done.setVisibility(View.VISIBLE);
                binding.icRotate.setImageResource(R.drawable.ic_rotate);
                binding.icCrop.setImageResource(R.drawable.ic_crop_on);
                binding.icDoodle.setImageResource(R.drawable.ic_doodle);
                binding.icText.setImageResource(R.drawable.ic_aa_text);
                binding.colorBarDoodle.setVisibility(View.GONE);
                binding.back.setVisibility(View.GONE);
                binding.icClose.setVisibility(View.VISIBLE);

                break;

            case R.id.doodle:
                binding.done.setVisibility(View.VISIBLE);
                binding.icRotate.setImageResource(R.drawable.ic_rotate);
                binding.icCrop.setImageResource(R.drawable.ic_crop);
                binding.icDoodle.setImageResource(R.drawable.ic_doodle_on);
                binding.colorBarDoodle.setVisibility(View.VISIBLE);
                binding.back.setVisibility(View.GONE);
                binding.icClose.setVisibility(View.VISIBLE);
                binding.icText.setImageResource(R.drawable.ic_aa_text);
                fireAnalytics("Edit Image", "Doodle color");
                IsDoodle = true;
                binding.doodle.setEnabled(false);
                mDrawingView = new DrawingView(this);
                binding.viewDrawingPad.addView(mDrawingView);
                break;

            case R.id.text:
                binding.done.setVisibility(View.VISIBLE);
                binding.icRotate.setImageResource(R.drawable.ic_rotate);
                binding.icCrop.setImageResource(R.drawable.ic_crop);
                binding.icDoodle.setImageResource(R.drawable.ic_doodle);
                binding.icText.setImageResource(R.drawable.ic_aa_text_on);
                binding.back.setVisibility(View.GONE);
                binding.icClose.setVisibility(View.VISIBLE);
                binding.colorBarDoodle.setVisibility(View.GONE);
                binding.TextID.setVisibility(View.VISIBLE);
                AddText();
                break;

            case R.id.ic_close:
                binding.icClose.setVisibility(View.GONE);
                binding.TextID.setVisibility(View.GONE);
                binding.back.setVisibility(View.VISIBLE);
                if (binding.doodle.getVisibility() == View.VISIBLE) {
                    showSaveDialog();
                }
                break;

            case R.id.mAddText:
                binding.mStyleRec.setVisibility(View.GONE);
                binding.icAddText.setImageResource(R.drawable.ic_aa_text_on);
                binding.icTextStyle.setImageResource(R.drawable.ic_text_style);
                binding.icTextColor.setImageResource(R.drawable.ic_color);
                AddText();
                break;

            case R.id.mTextStyle:
                binding.icAddText.setImageResource(R.drawable.ic_aa_text);
                binding.icTextStyle.setImageResource(R.drawable.ic_text_style_on);
                binding.icTextColor.setImageResource(R.drawable.ic_color);
                binding.mStyleRec.setVisibility(View.VISIBLE);
                binding.mStyleRec.setAdapter(mFontAdapter);
                binding.mStyleRec.setItemAnimator(new DefaultItemAnimator());
                break;

            case R.id.mTextColor:
                binding.icAddText.setImageResource(R.drawable.ic_aa_text);
                binding.icTextStyle.setImageResource(R.drawable.ic_text_style);
                binding.icTextColor.setImageResource(R.drawable.ic_color_on);
                binding.mStyleRec.setVisibility(View.VISIBLE);
                binding.mStyleRec.setAdapter(mAdapter);
                binding.mStyleRec.setItemAnimator(new DefaultItemAnimator());
                break;

            case R.id.done:
                new AsynchSaveImage().execute((Void[]) null);
                break;
        }

    }


    private void showSaveDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsynchSaveImage().execute((Void[]) null);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.create().show();
    }

    public class AsynchSaveImage extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                callStickerView.HideBorder();
                progressDialog = new ProgressDialog(EditImageActivity.this);
                progressDialog.setMessage("Wait..");
                progressDialog.show();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            binding.frame.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(binding.frame.getDrawingCache());
            binding.frame.setDrawingCacheEnabled(false);
//            Uri uri=getImageUri(EditActivity.this,bitmap);
            Utils.CaptureImage(bitmap, EditImageActivity.this);
            Utils.IsUpdate = true;
            fireAnalytics("Edit Image", "save image");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressDialog != null) {

                    if (!EditImageActivity.this.isFinishing() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                }

            } catch (Exception e) {
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.IsFramed) {
            Uri uri = Utils.mEditedURI;
            try {
                Utils.mEditedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mFilterBitmap = Utils.mEditedBitmap;
                Glide.with(EditImageActivity.this)
                        .load(Utils.mEditedBitmap)
                        .into(binding.image);
                Utils.IsFramed = false;
            } catch (IOException e) {
                Log.e("Enable to frame", "!!!");
                e.printStackTrace();
            }
        } else {

            if (!Utils.IsCropped) {
                String mEditFile = Utils.mEditPath;
                Utils.mOriginalFile = new File(mEditFile);

                Glide.with(EditImageActivity.this)
                        .load(Utils.mOriginalFile.getPath())
                        .into(binding.image);
                Utils.mOriginalBitmap = BitmapFromPath(Utils.mOriginalFile.getAbsolutePath());
                Utils.mEditedBitmap = Utils.mOriginalBitmap;
                mFilterBitmap = Utils.mOriginalBitmap;

            }

//            if (!Utils.IsFramed) {
//
//                Glide.with(EditImageActivity.this)
//                        .load(Utils.mEditedBitmap)
//                        .into(binding.image);
//                mFilterBitmap = Utils.mEditedBitmap;
//
//            }

        }

    }

    int CurrentAngle_Position = -1;
    Integer Angle[] = new Integer[]{90, 180, -90, 0};

    public void rotate() {
        try {

            CurrentAngle_Position--;
            if (CurrentAngle_Position == -2) {
                CurrentAngle_Position = 2;
            }
            if (CurrentAngle_Position < 0) {
                CurrentAngle_Position = 3;
            }

            Glide.with(EditImageActivity.this)
                    .asBitmap()
                    .load(Utils.mEditedBitmap)
                    .transform(new RotateTransformation(EditImageActivity.this, Angle[CurrentAngle_Position]))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.image.setImageBitmap(resource);
                            Utils.mEditedBitmap = resource;
                            mFilterBitmap = Utils.mEditedBitmap;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }
    }

    public class AsyRotateImage extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog = new ProgressDialog(EditImageActivity.this);
                progressDialog.setMessage("Wait..");
                progressDialog.show();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rotate();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
                if (progressDialog != null) {

                    if (!EditImageActivity.this.isFinishing() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    public Uri getImageToUri(Context context, Bitmap mImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), mImage, "Title", null);
        return Uri.parse(path);
    }

    private void StartCropImage(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(false)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    binding.image.setImageBitmap(bitmap);
                    Utils.mEditedBitmap = bitmap;
                    mFilterBitmap = bitmap;
//                    Utils.mEditedBitmap = null;

                } catch (Exception e) {
                    Toast.makeText(EditImageActivity.this, "Problem in cropping Image", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping Image Fail" + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    //        if (Utils.mainBitmap != null) {
//                binding.image.setImageBitmap(null);
//                binding.image.setImageBitmap(Utils.mainBitmap);
//            }
    private void AddText() {

        Dialog dialog = new Dialog(this);
        TextDailogBinding folderBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.text_dailog, null, false);
        dialog.setContentView(folderBinding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        folderBinding.mCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        folderBinding.mOk.setOnClickListener(v -> {
            newText = folderBinding.mEditText.getText().toString();
            if (!newText.equals("")) {
                callStickerView.AdTextViewSticker(newText, null);
                callStickerView.ShowBorder();
            }
            dialog.dismiss();
        });

        dialog.show();

    }

    private void IFontArrayList() {
        Resources res = getResources();
        AssetManager am = res.getAssets();
        mStyleList = new String[0];
        try {
            mStyleList = am.list("textfonts");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void IColorArrayList() {
        @SuppressLint("Recycle") TypedArray ta = getResources().obtainTypedArray(R.array.rainbow);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
    }
}