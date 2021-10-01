package com.example.imagetopdf.Adpters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagetopdf.Class.imageModel;
import com.example.imagetopdf.R;

import java.io.File;
import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter {

    Activity activity;
    ArrayList<imageModel> imageModels;
    RequestOptions options = new RequestOptions();

    public SpinnerAdapter(Activity activity, ArrayList<imageModel> imageModels) {
        super(activity, 0, imageModels);
        this.activity = activity;
        this.imageModels = imageModels;
        options = new RequestOptions();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return intView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return intView(position, convertView, parent);
    }

    private View intView(int position, View view, ViewGroup group) {

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.folder_view, group, false);
        }

        ImageView imageView = view.findViewById(R.id.img);
        TextView text = view.findViewById(R.id.text);
        TextView count = view.findViewById(R.id.count);

        imageModel imageModel = imageModels.get(position);
        if (imageModel.getPathList().size() > 0) {
            Uri uri = Uri.fromFile(new File(imageModel.getPathList().get(0)));
            try {
                Glide.with(activity)
                        .load(uri)
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW))
                        .into(imageView);
            } catch (Exception e) {
                Glide.with(activity)
                        .load(uri)
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW))
                        .into(imageView);
            }

            text.setText(imageModel.getBucketName());
            count.setText(imageModel.getPathList().size() + "");

        }

        return view;

    }

}
