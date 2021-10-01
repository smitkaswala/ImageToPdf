package com.example.imagetopdf.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.imagetopdf.Activity.ListImageActivity;
import com.example.imagetopdf.Class.imageModel;
import com.example.imagetopdf.R;
import com.example.imagetopdf.UtilsClass.NotificationUtils;
import com.example.imagetopdf.UtilsClass.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GetFileList extends Service{

    public static int TotalPhotos = 0;
    public static int TotalVideos = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, NotificationUtils.ANDROID_CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        String action = null;
        String action_video = null;

        try {
            action = intent.getStringExtra("action");
        } catch (Exception e) {

        }
//        Log.e("Action:",action);
        try {
            assert action != null;
            if (action.equalsIgnoreCase("photo")) {

                GetAllPhotos();

            }else if (action.equalsIgnoreCase("video")){

                GetAllVideos();
            }

            else if (action.equalsIgnoreCase("album")) {

                GetPhotoAlbumLis();

            }else if (action.equalsIgnoreCase("album_video")){

                GetVideosAlbumLis();
            }
        } catch (Exception e) {

        }

        return super.onStartCommand(intent, flags, startId);

    }

    public void GetAllPhotos() {

        try {
            ArrayList<String> Image_List;
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATA
            };

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor cur = getContentResolver().query(images,
                    projection, // Which columns to return
                    null,       // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    orderBy + " DESC"        // Ordering
            );

            Image_List = new ArrayList<>();
            if (cur.moveToFirst()) {
                String bucket;
                String path;
                int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int column_index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {

                    bucket = cur.getString(bucketColumn);
                    if(!bucket.startsWith(".")){
                        path = cur.getString(column_index);
                        File filePath = new File(path);
                        double length = filePath.length();
//                        Log.e("TAG","GetAllPhotos: " + filePath.getName() + " ?? " + length);
                        if(!filePath.getName().startsWith(".")){
                            if(length > 0)
                                Image_List.add(path);
                        }
                    }
                } while (cur.moveToNext());

                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
                    Collections.reverse(Image_List);
                }
                Message message = new Message();
                message.what = 27;
                message.obj = Image_List;
                ListImageActivity.handler.sendMessage(message);
            }
        } catch (Exception e) {

        }
    }

    public void GetAllVideos() {

        try {
            ArrayList<String> Video_List;
            String[] projection = new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DURATION
            };

            Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            Cursor cur = getContentResolver().query(videos,
                    projection, // Which columns to return
                    null,       // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    orderBy + " DESC"        // Ordering
            );

            Video_List = new ArrayList<>();
            if (cur.moveToFirst()) {
                String bucket;
                String path;
                int bucketColumn = cur.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                int dateColumn = cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                int column_index = cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int durationColumn = cur.getColumnIndex(MediaStore.Video.Media.DURATION);
                do {

                    bucket = cur.getString(bucketColumn);
                    if(!bucket.startsWith(".")){
                        path = cur.getString(column_index);
                        File filePath = new File(path);
                        double length = filePath.length();
//                        Log.e("TAG","GetAllPhotos: " + filePath.getName() + " ?? " + length);
                        if(!filePath.getName().startsWith(".")){
                            if(length > 0)
                                Video_List.add(path);
                        }
                    }
                } while (cur.moveToNext());

                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
                    Collections.reverse(Video_List);
                }
                Message message = new Message();
                message.what = 22;
                message.obj = Video_List;
            }
        } catch (Exception e) {
        }
    }

    class NameNoComparator implements Comparator<imageModel>{

        @Override
        public int compare(imageModel o1, imageModel o2) {
            return Integer.compare(o1.getPathList().size(), o2.getPathList().size());
        }
    }

    public void GetPhotoAlbumLis() {
        try {
            TotalPhotos = 0;
            ArrayList<imageModel> folderListArray = new ArrayList<imageModel>();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            ArrayList<String> ids = new ArrayList<String>();
            int count = 0;
//            Log.e("array size", "" + ids.size() + "===" + cursor.getCount());

            if (cursor != null) {
//                TotalPhotos = cursor.getCount();
                while (cursor.moveToNext()) {

                    imageModel album = new imageModel();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    int columnIndexName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.setBucketId(cursor.getString(columnIndex));
                    String fname = cursor.getString(columnIndexName);
                    if(!fname.startsWith(".")){
//                    if (fname != null || !fname.equalsIgnoreCase("null")) {
//                    Log.e("array size", "" + ids.size() + "===" + album.bucket_id + " >>>>> " + cursor.getString(columnIndexName) + " >>> " + getCameraCover("" + album.bucket_id).size());
                        if (!ids.contains(album.getBucketId())) {
                            columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                            album.setBucketName(cursor.getString(columnIndex));
                            if (cursor.getString(columnIndex)!=null){
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                String result = cursor.getString(column_index);
                                String ParentPath = GetParentPath(result);
                                album.setBucketPath(ParentPath);

                                columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                album.setId(cursor.getString(columnIndex));
                                album.pathList = getCameraCover("" + album.getBucketId()); //----get four image path arraylist
                                album.setType(0);
                                album.setFolderName(result);
//                                Log.e("TAG", "GetPhotoAlbumLis: " + album.getBucketName());
//                            if (album.foldername != null || !album.foldername.equalsIgnoreCase("null")) {
                                if (album.pathList.size() > 0) {
//                        if (album.pathlist.size() > 0 && album.foldername.trim().length()>0) {

                                        folderListArray.add(album);
                                        ids.add(album.getBucketId());

                                }
//                            }
                                TotalPhotos += album.pathList.size();
                            }
                        }
                    }
                }
                cursor.close();

//                if (Utils.SORTING_TYPE.equals(getString(R.string.no_of_photos))) {
//                    Collections.sort(folderListArray, new NameNoComparator());
//                }
//                if (Utils.SORTING_TYPE.equals(getString(R.string.name))) {
//                    Collections.sort(folderListArray, (Comparator<AlbumDetail>) (lhs, rhs) -> {
//                        return lhs.getBucketName().toLowerCase().compareTo(rhs.getBucketName().toLowerCase());
//                    });
//                }
//
//                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
//                    Collections.reverse(folderListArray);
//                }
//                Log.e("array size folder", "" + folderListArray.size());
                Message message = new Message();
                message.what = 23;
                message.obj = folderListArray;
                ListImageActivity.handler_new.sendMessage(message);
            }

        } catch (Exception e) {
        }
    }

    public void GetVideosAlbumLis() {
        try {
            TotalVideos = 0;
            ArrayList<imageModel> videoFolderListArray = new ArrayList<imageModel>();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA,MediaStore.Video.Media.DURATION};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            ArrayList<String> ids = new ArrayList<String>();
            int count = 0;

//            Log.d("array size", "+" + ids.size() + "===" + cursor.getCount());

            if (cursor != null) {
//                TotalPhotos = cursor.getCount();
                while (cursor.moveToNext()) {

                    imageModel album_video = new imageModel();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
                    int columnIndexName = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                    album_video.setBucketId(cursor.getString(columnIndex));
                    String fname = cursor.getString(columnIndexName);
                    if(!fname.startsWith(".")){
//                    if (fname != null || !fname.equalsIgnoreCase("null")) {
//                    Log.d("array size", "" + ids.size() + "===" + album_video.getBucketId() + " >>>>> " + cursor.getString(columnIndexName) + " >>> " + getCameraCover("" + album_video.bucket_id).size());
                        if (!ids.contains(album_video.getBucketId())) {
                            columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

                            album_video.setBucketName(cursor.getString(columnIndex));
                            if (cursor.getString(columnIndex)!=null){
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                                String result = cursor.getString(column_index);
                                String ParentPath = GetParentPath(result);
                                album_video.setBucketPath(ParentPath);

                                columnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                                album_video.setId(cursor.getString(columnIndex));
                                album_video.pathList = getVideoCover("" + album_video.getBucketId()); //----get four image path arraylist
                                album_video.setType(0);
                                album_video.setFolderName(result);
//                                Log.e("TAG", "GetPhotoAlbumLis: " + album.getBucketName());
//                            if (album.foldername != null || !album.foldername.equalsIgnoreCase("null")) {
                                if (album_video.pathList.size() > 0) {
//                        if (album.pathlist.size() > 0 && album.foldername.trim().length()>0) {

                                    videoFolderListArray.add(album_video);
                                    ids.add(album_video.getBucketId());

                                }
//                            }
                                TotalVideos += album_video.pathList.size();
                            }
                        }
                    }
                }
                cursor.close();

//                if (Utils.SORTING_TYPE.equals(getString(R.string.no_of_photos))) {
//                    Collections.sort(folderListArray, new NameNoComparator());
//                }
//                if (Utils.SORTING_TYPE.equals(getString(R.string.name))) {
//                    Collections.sort(folderListArray, (Comparator<AlbumDetail>) (lhs, rhs) -> {
//                        return lhs.getBucketName().toLowerCase().compareTo(rhs.getBucketName().toLowerCase());
//                    });
//                }
//
//                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
//                    Collections.reverse(folderListArray);
//                }
//                Log.e("array size folder", "" + folderListArray.size());
                Message message = new Message();
                message.what = 25;
                message.obj = videoFolderListArray;
//                ImagesActivity.album_handler.sendMessage(message);
            }

        } catch (Exception e) {
        }
    }

    public ArrayList<String> getCameraCover(String id) {

        String data = null;
        ArrayList<String> result = new ArrayList<String>();
        final String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);

        if (cursor.moveToFirst()) {

            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            do {
                data = cursor.getString(dataColumn);
                File filePath = new File(data);
                double length = filePath.length();
                if (length > 0) {
                    if(!filePath.getName().startsWith(".")) {
                        result.add(data);
                    }
                }
                //---------------------------------------------
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getVideoCover(String id) {

        String data = null;
        ArrayList<String> result = new ArrayList<String>();
        final String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media.DURATION};
        final String selection = MediaStore.Video.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        String orderBy = MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC";
        final Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);

        if (cursor.moveToFirst()) {

            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            final int name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            do {
                data = cursor.getString(dataColumn);
                File filePath = new File(data);
                double length = filePath.length();
                if (length > 0) {
                    if(!filePath.getName().startsWith(".")) {
                        result.add(data);
                    }
                }
                //---------------------------------------------
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public String GetParentPath(String path) {
        File file = new File(path);
        return file.getAbsoluteFile().getParent();

    }

}
