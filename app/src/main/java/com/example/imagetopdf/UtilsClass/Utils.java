package com.example.imagetopdf.UtilsClass;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.imagetopdf.Class.imageModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Utils {
    public static ArrayList<imageModel> mFolderDialogList = new ArrayList<>();
    public static ArrayList<String> mAllImageDialogList = new ArrayList<>();
    public static ArrayList<imageModel> mVideosDialogList = new ArrayList<>();
    public static String VIEW_TYPE="Grid";
    public static String SORTING_TYPE = "";
    public static String SORTING_TYPE2 = "";
    public static int COLUMN = 2;
    public static boolean IsUpdate=false;
    public static boolean IsUpdateVideos=false;
    public static File mOriginalFile;
    public static String mEditPath;
    public static String mVideoPath;
    public static Bitmap mOriginalBitmap;
    public static Bitmap mEditedBitmap;
    public static Uri mEditedURI;
    public static boolean IsFramed=false;
    public static int mDoodleColor= Color.BLACK;
    public static boolean IsCropped=false;
    public static boolean isEdited = false;

    public static Context c;

    public static Bitmap mainBitmap;

    public static int GetRandomNumber(){
        int flag=0;
        flag=new Random().nextInt(1000);
        return flag;
    }
    public static File CaptureImage(Bitmap bitmap,Context context) {
        c=context;
//        Bitmap bitmap=null;
        File f = null;
        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            //----------------dsestination path--------
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/"+"Editer");
            if(!myDir.exists())
                myDir.mkdirs();
//            String ParentPath= Utils.mOriginalFile.getParentFile().getPath();
////            Log.e("Parent path:",ParentPath + "*");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Uri destination = Uri.fromFile(new File(myDir.getPath() + "/" + timeStamp + ".png"));
            String NewImagePath=destination.getPath();
//            Log.e("New path:",NewImagePath + "*");
            //-----------------------------------------
            if (NewImagePath != null) {
                f = new File(NewImagePath);
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //------------insert into media list----
                File newfilee = new File(destination.getPath());
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, destination.getPath());
                values.put(MediaStore.Images.Media.DATE_TAKEN, newfilee.lastModified());
                scanPhoto(newfilee.getPath());
                Toast.makeText(c, "Image saved successfully!!!", Toast.LENGTH_LONG).show();
                Uri uri1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri1 = FileProvider.getUriForFile(c.getApplicationContext(), c.getPackageName() + ".provider", newfilee);
                } else {
                    uri1 = Uri.fromFile(newfilee);
                }
                c.getContentResolver().notifyChange(uri1, null);
                Utils.IsUpdate=true;
//                ImageActivity.list.add(0,NewImagePath);

            }
        } catch (Exception e) {
        }
        return f;

    }
    public static MediaScannerConnection msConn;

    public static void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(c, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
//                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
//                Log.i("msClient obj", "scan completed");
            }
        });
        msConn.connect();
    }


}
