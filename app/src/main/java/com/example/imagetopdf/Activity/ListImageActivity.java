package com.example.imagetopdf.Activity;

import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_BORDER_WIDTH;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_COMPRESSION;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_IMAGE_BORDER_TEXT;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_PAGE_COLOR;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_PAGE_SIZE;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_PAGE_SIZE_TEXT;
import static com.example.imagetopdf.ConstantsClass.constants.DEFAULT_QUALITY_VALUE;
import static com.example.imagetopdf.ConstantsClass.constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static com.example.imagetopdf.ConstantsClass.constants.IMAGE_SCALE_TYPE_STRETCH;
import static com.example.imagetopdf.ConstantsClass.constants.MASTER_PWD_STRING;
import static com.example.imagetopdf.ConstantsClass.constants.OPEN_SELECT_IMAGES;
import static com.example.imagetopdf.ConstantsClass.constants.RESULT;
import static com.example.imagetopdf.ConstantsClass.constants.STORAGE_LOCATION;
import static com.example.imagetopdf.ConstantsClass.constants.appName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.example.imagetopdf.Adpters.ImageFolderAdapter;
import com.example.imagetopdf.Adpters.ImagesAdapter;
import com.example.imagetopdf.Adpters.SpinnerAdapter;
import com.example.imagetopdf.Class.DatabaseHelper;
import com.example.imagetopdf.Class.DefaultTextWatcher;
import com.example.imagetopdf.Class.PageSizeUtils;
import com.example.imagetopdf.Class.createPdf;
import com.example.imagetopdf.Class.imageModel;
import com.example.imagetopdf.Class.imageToPDFOptions;
import com.example.imagetopdf.ConstantsClass.constants;
import com.example.imagetopdf.ImagesFeatures.ImageUtils;
import com.example.imagetopdf.InterFace.CameraInterFace;
import com.example.imagetopdf.InterFace.Consumer;
import com.example.imagetopdf.InterFace.FolderClickListener;
import com.example.imagetopdf.InterFace.OnPDFCreatedInterface;
import com.example.imagetopdf.PrefManager.Preference;
import com.example.imagetopdf.R;
import com.example.imagetopdf.Service.GetFileList;
import com.example.imagetopdf.UtilsClass.FileUtils;
import com.example.imagetopdf.UtilsClass.NotificationUtils;
import com.example.imagetopdf.UtilsClass.dialogUtils;
import com.example.imagetopdf.UtilsClass.stringUtils;
import com.example.imagetopdf.UtilsClass.Utils;
import com.example.imagetopdf.databinding.ActivityListImageBinding;
import com.example.imagetopdf.databinding.FolderListPopupBinding;
import com.example.imagetopdf.databinding.SaveDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.StringUtils;
import com.zhihu.matisse.Matisse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class ListImageActivity extends AppCompatActivity implements CameraInterFace, FolderClickListener, AdapterView.OnItemSelectedListener, OnPDFCreatedInterface {

    private static final int CAMERA_PIC_REQUEST = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 12;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Uri imageUri;
    Dialog dialog;
    String mHomePath;
    ContentValues values;
    private String mPath;
    Preference preference;
    private int mPageColor;
    PageSizeUtils mPageSizeUtils;
    CameraInterFace interFace;
    private FileUtils mFileUtils;
    BottomSheetDialog bottom;
    private MaterialDialog mMaterialDialog;
    private String mPageNumStyle;
    DatabaseHelper databaseHelper;
    @SuppressLint("StaticFieldLeak")
    private imageToPDFOptions mPdfOptions;
    ActivityListImageBinding binding;
    public static ImagesAdapter adapter;
    public SpinnerAdapter spinner;
    public boolean isActionModeEnable = false;
    public static Handler handler, handler_new;
    private SharedPreferences mSharedPreferences;
    public static ImageFolderAdapter folderAdapter;
    private boolean mIsButtonAlreadyClicked = false;
    ArrayList<imageModel> album = new ArrayList<>();
    ArrayList<String> albumList = new ArrayList<>();
    ArrayList<String> selectItem = new ArrayList<>();
    ArrayList<String> Selected = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int CAMERA_PHOTO = 111;
    private Uri imageToUploadUri;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_image);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            new NotificationUtils(this);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ListImageActivity.this);
        databaseHelper = new DatabaseHelper(this);
        mFileUtils = new FileUtils(this);
        mPdfOptions = new imageToPDFOptions();
        mPageSizeUtils = new PageSizeUtils(this);
        mPageColor = mSharedPreferences.getInt(constants.DEFAULT_PAGE_COLOR_ITP, DEFAULT_PAGE_COLOR);
        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION, stringUtils.getInstance().getDefaultStorageLocation());
        adapter = new ImagesAdapter(this, this, this);
        folderAdapter = new ImageFolderAdapter(this, this);


        initialize();

        binding.back.setOnClickListener(v -> {

            finish();
        });

        binding.close.setOnClickListener(v -> {
            stopAction();
        });

        binding.mImg.setOnClickListener(v -> {

            AllAlbum();
            dialog = new Dialog(this);
            FolderListPopupBinding folderBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.folder_list_popup, null, false);
            dialog.setContentView(folderBinding.getRoot());
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            folderBinding.RecyclerView.setLayoutManager(new GridLayoutManager(ListImageActivity.this, 1));
            folderBinding.RecyclerView.setAdapter(folderAdapter);
            folderAdapter.notifyDataSetChanged();

        });

        binding.convert.setOnClickListener(v -> {
            if (selectItem == null && selectItem.size() > 0){
                stringUtils.getInstance().showSnackbar(this, R.string.image_selected_blank);
            }else {
                createPDF(false);
            }

        });

        binding.open.setOnClickListener(v -> {
            mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
        });
    }


    private void createPDF(boolean isGrayScale) {
        String preFillName = mFileUtils.getLastFileName(selectItem);
        String ext = getString(R.string.pdf_ext);
//        mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
        openSaveDialogMenu(preFillName, ext, filename -> save(isGrayScale, filename));
    }

    private void save(boolean isGrayScale, String filename) {
        mPdfOptions.setImagesUri(selectItem);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
        mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
        mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(filename);
        if (isGrayScale)
            saveImagesInGrayScale();
        new createPdf(mPdfOptions, mHomePath, ListImageActivity.this).execute();

    }

    private void saveImagesInGrayScale() {
        ArrayList<String> tempImageUri = new ArrayList<>();
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            final boolean mkdirs = dir.mkdirs();
            Log.d("TAG", "saveImagesInGrayScale: " + mkdirs);
            Toast.makeText(getApplicationContext(), "" + mkdirs, Toast.LENGTH_SHORT).show();

            int size = selectItem.size();
            for (int i = 0; i < size; i++) {
                String fileName = String.format(getString(R.string.filter_file_name),
                        String.valueOf(System.currentTimeMillis()), i + "_grayscale");
                File outFile = new File(dir, fileName);

                File f = new File(selectItem.get(i));
                FileInputStream fis = new FileInputStream(f);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Bitmap grayScaleBitmap = ImageUtils.getInstance().toGrayscale(bitmap);

                outFile.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile), 1024 * 8);
                grayScaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.close(); // Includes flushing the stream and closing the FileOutputStream
                tempImageUri.add(outFile.getAbsolutePath());
            }
            selectItem.clear();
            selectItem.addAll(tempImageUri);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.IsUpdate) {
            initialize();
            AllAlbum();
            Utils.IsUpdate = false;
        }
    }

    public void initialize() {

        handler = new Handler(new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int code = msg.what;
                if (code == 27) {
                    try {
                        albumList = new ArrayList<>();
                        albumList = (ArrayList<String>) msg.obj;
                        Utils.mAllImageDialogList = new ArrayList<>();
                        imageModel model = new imageModel();
                        model.setType(1);
                        imageModel FirstModel = new imageModel();
                        FirstModel.setFolderName("Camera");
                        FirstModel.setBucketName("Camera");
                        FirstModel.setType(1);

                        Utils.mAllImageDialogList.addAll(albumList);

                        if (albumList != null && albumList.size() > 0) {
                            Collections.sort(album, new Comparator<imageModel>() {
                                @Override
                                public int compare(imageModel o1, imageModel o2) {
                                    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                                }
                            });

                            for (int a = 0; a < albumList.size(); a++) {
                                Log.d("TAG", "handleMessage: " + albumList.get(a));
                            }
                            adapter.addAll(albumList);
                        }

                        binding.rv.setLayoutManager(new GridLayoutManager(ListImageActivity.this, 3));
                        binding.rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(ListImageActivity.this, GetFileList.class).putExtra("action", "photo"));
                } else {
                    startService(new Intent(ListImageActivity.this, GetFileList.class).putExtra("action", "photo"));
                }
            }
        }, 100);

    }

    public void AllAlbum() {

        handler_new = new Handler(new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int code = msg.what;
                if (code == 23) {
                    try {
                        album = new ArrayList<>();
                        album = (ArrayList<imageModel>) msg.obj;
                        Utils.mFolderDialogList = new ArrayList<>();
                        imageModel model = new imageModel();
                        model.setFolderName("Create_Album");
                        model.setType(1);
                        Utils.mFolderDialogList.add(0, model);
                        Utils.mFolderDialogList.addAll(album);

                        if (album != null && album.size() > 0) {
                            Collections.sort(album, new Comparator<imageModel>() {
                                @Override
                                public int compare(imageModel o1, imageModel o2) {
                                    return o1.getBucketName().toLowerCase().compareTo(o2.getBucketName().toLowerCase());
                                }
                            });

                            folderAdapter.addAll(album);

                        }

                        folderAdapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(ListImageActivity.this, GetFileList.class).putExtra("action", "album"));
                } else {
                    startService(new Intent(ListImageActivity.this, GetFileList.class).putExtra("action", "album"));
                }
            }
        }, 100);
    }

    @Override
    public void onCameraClick() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);

    }

    private void startAction() {
        isActionModeEnable = true;
        binding.convert.setVisibility(View.VISIBLE);
        binding.appBar.setVisibility(View.VISIBLE);
        binding.bottom.setVisibility(View.GONE);
        binding.mCount.setVisibility(View.VISIBLE);

        selectItem.clear();
        adapter.StartAction();
    }

    private void stopAction() {
        isActionModeEnable = false;
        binding.convert.setVisibility(View.GONE);
        binding.appBar.setVisibility(View.GONE);
        binding.bottom.setVisibility(View.VISIBLE);
        binding.open.setVisibility(View.GONE);
        selectItem.clear();
        adapter.StopAction();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    private void makeSelected(ImageView image, String videoItem) {
        if (image.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_round).getConstantState())) {
            selectItem.add(videoItem);
            image.setImageResource(R.drawable.ic_round_done);
        } else {
            selectItem.remove(videoItem);
            image.setImageResource(R.drawable.ic_round);
        }
        binding.mCount.setText(String.format("%d Selected", selectItem.size()));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onFolderClick(imageModel model, int position) {

        binding.mImg.setText(model.getBucketName() + "▼");
//        if (!mIsButtonAlreadyClicked) {
        if (model.getPathList() != null && model.getPathList().size() > 0) {

//                selectImages();
//                mIsButtonAlreadyClicked = true;
            adapter.addAll(model.pathList);
            adapter.notifyDataSetChanged();
//            }
        }

//        if (!mIsButtonAlreadyClicked) {
//            selectImages();
//            mIsButtonAlreadyClicked = true;
//        }

        binding.rv.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                selectItem.clear();
                Selected.clear();
                selectItem.addAll(Matisse.obtainPathResult(data));
                Selected.addAll(selectItem);
                if (selectItem.size() > 0) {
                    stringUtils.getInstance().showSnackbar(this, R.string.snackbar_images_added);
                    binding.convert.setEnabled(true);
                }
                binding.open.setVisibility(View.GONE);
                break;

        }

        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    saveImage(image);
                    Utils.IsUpdate = true;
                }
            }
        }

    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        File myDir = new File(root, "DCIM");
        myDir.mkdirs();

        File myDir1 = new File(myDir, "Camera");
        myDir1.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "" + timeStamp + ".jpg";

        File file = new File(myDir1, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "saveImage: " + file.getAbsolutePath());

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, new String[]{file.getName()}, null);
        MainCaller();

    }

    private void selectImages() {
        ImageUtils.selectImages(this, INTENT_REQUEST_GET_IMAGES);
    }

    public boolean isFileExist(String mFileName) {
        String path = mSharedPreferences.getString(STORAGE_LOCATION,
                stringUtils.getInstance().getDefaultStorageLocation()) + mFileName;
        File file = new File(path);

        return file.exists();
    }


    public void openSaveDialogMenu(String preFillName, String ext, Consumer<String> saveMethod) {

        bottom = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        SaveDialogBinding saveDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.save_dialog, null, false);
        bottom.setContentView(saveDialogBinding.getRoot());
        saveDialogBinding.edit1.setText(preFillName);
        final String file = preFillName;

        if (stringUtils.getInstance().isEmpty("")) {
            stringUtils.getInstance().showSnackbar(this, R.string.snackbar_name_not_blank);
        } else {

            if (!isFileExist(file + ext)) {
                saveMethod.accept(file);
                Toast.makeText(this, "PDF", Toast.LENGTH_SHORT).show();
            }
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Quality, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        saveDialogBinding.spinner.setAdapter(adapter);

        saveDialogBinding.spinner.setOnItemSelectedListener(this);


        if (saveDialogBinding.password.getText() != null && saveDialogBinding.password.length() > 0) {
            saveDialogBinding.password.setText(mPdfOptions.getPassword());
        }

        saveDialogBinding.password.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        saveDialogBinding.switchBar.setEnabled(s.toString().trim().length() > 0);
                    }
                }
        );

        saveDialogBinding.switchBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    final MaterialDialog dialog = new MaterialDialog.Builder(ListImageActivity.this)
                            .title(R.string.enter_password_custom)
                            .customView(R.layout.custom_dialogview, true)
                            .backgroundColorRes(R.color.white)
                            .positiveColorRes(R.color.color_blue)
                            .negativeColorRes(R.color.color_blue)
                            .neutralColorRes(R.color.color_blue)
                            .titleColorRes(R.color.color_blue)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .neutralText(R.string.remove_dialog)
                            .build();

                    final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                    final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
                    final View negativeAction = dialog.getActionButton(DialogAction.NEGATIVE);

                    final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);
                    passwordInput.setText(mPdfOptions.getPassword());
                    passwordInput.addTextChangedListener(
                            new DefaultTextWatcher() {
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                                }
                            });

                    positiveAction.setOnClickListener(v -> {
                        if (stringUtils.getInstance().isEmpty(passwordInput.getText())) {
                            stringUtils.getInstance().showSnackbar(ListImageActivity.this, R.string.snackbar_password_cannot_be_blank);
                        } else {
                            mPdfOptions.setPassword(passwordInput.getText().toString());
                            mPdfOptions.setPasswordProtected(true);

                            dialog.dismiss();
                        }
                    });

                    negativeAction.setOnClickListener(v -> {
                        saveDialogBinding.switchBar.setChecked(false);
                        dialog.dismiss();

                    });

                    if (stringUtils.getInstance().isNotEmpty(mPdfOptions.getPassword())) {
                        neutralAction.setOnClickListener(v -> {
                            saveDialogBinding.switchBar.setChecked(false);
                            mPdfOptions.setPassword(null);
                            mPdfOptions.setPasswordProtected(false);

                            stringUtils.getInstance().showSnackbar(ListImageActivity.this, R.string.password_remove);
                        });
                    }

                    dialog.show();
                    positiveAction.setEnabled(false);


                } else {

                }
            }

        });
        bottom.show();

        saveDialogBinding.mOk.setOnClickListener(v -> {
            saveMethod.accept(file);
            bottom.dismiss();
        });


        saveDialogBinding.mCancel.setOnClickListener(v -> {
            bottom.dismiss();
        });


    }

    private void MainCaller() {
        initialize();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onFolderLongClick() {
        if (!isActionModeEnable) {
            startAction();
        }
    }

    @Override
    public void onBind(ImageView imageView, String ImageClass) {
        if (isAvailable(ImageClass)) {
            imageView.setImageResource(R.drawable.ic_round_done);
        } else {
            imageView.setImageResource(R.drawable.ic_round);
        }
    }

    private boolean isAvailable(String albumClass) {

        for (int i = 0; i < selectItem.size(); i++) {

            if (albumClass.equals(selectItem.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(ImageView imageView, int position, ArrayList<String> imageClass, File file) {
        if (isActionModeEnable) {
            makeSelected(imageView, imageClass.get(position));
        } else {

            Intent i = new Intent(ListImageActivity.this, EditImageActivity.class);
            i.putExtra("file", file.getAbsolutePath());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
        Utils.mEditPath = file.getAbsolutePath();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = dialogUtils.getInstance().createAnimationDialog(this);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();

        if (!success) {
            stringUtils.getInstance().showSnackbar(this, R.string.snackbar_folder_not_created);
            return;
        }

        databaseHelper.insertRecord(path, ListImageActivity.this.getString(R.string.created));
        stringUtils.getInstance().getSnackbarwithAction(ListImageActivity.this, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();

        binding.open.setVisibility(View.VISIBLE);
        binding.mCount.setVisibility(View.INVISIBLE);
        binding.convert.setVisibility(View.GONE);
        adapter.StopAction();

        mPath = path;
        resetValues();

    }


    private void resetValues() {
//        mPdfOptions = new imageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
        mPdfOptions.setWatermarkAdded(false);
        selectItem.clear();
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(DEFAULT_IMAGE_SCALE_TYPE_TEXT,
                IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString(constants.PREF_PAGE_STYLE, null);
        mPageColor = mSharedPreferences.getInt(constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR);

    }

}