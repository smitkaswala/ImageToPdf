package com.example.imagetopdf.InterFace;

import android.widget.ImageView;

import com.example.imagetopdf.Class.imageModel;

import java.io.File;
import java.util.ArrayList;

public interface FolderClickListener {
    void onFolderClick(imageModel model, int position);
    void onBind(ImageView imageView, String videoClass);
    void onItemClick (ImageView imageView, int position, ArrayList<String> videoClasses, File file);
    void onFolderLongClick();
}
