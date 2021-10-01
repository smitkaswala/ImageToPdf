package com.example.imagetopdf.Adpters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagetopdf.Class.imageModel;
import com.example.imagetopdf.InterFace.FolderClickListener;
import com.example.imagetopdf.R;

import java.io.File;
import java.util.ArrayList;

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.ViewHolder> {

    Activity activity;
    ArrayList<imageModel> objects = new ArrayList<>();
    ArrayList<imageModel> filter = new ArrayList<>();
    FolderClickListener Interface;
    RequestOptions options = new RequestOptions();

    public ImageFolderAdapter(Activity activity,FolderClickListener Interface){
        this.activity = activity;
        this.Interface = Interface;
        options = new RequestOptions();
    }

    @NonNull
    @Override
    public ImageFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.folder_view, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImageFolderAdapter.ViewHolder holder, int position) {

        imageModel imageModel = filter.get(position);
        if (imageModel.getPathList().size() > 0){
            Uri uri = Uri.fromFile(new File(imageModel.getPathList().get(0)));
            try {
                Glide.with(activity)
                        .load(uri)
                        .apply(options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW))
                        .into(holder.img);
            }
            catch (Exception e){
                Glide.with(activity)
                        .load(uri)
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW))
                        .into(holder.img);
            }

            holder.text.setText(imageModel.getBucketName());
            holder.count.setText(imageModel.getPathList().size() + "");
            holder.itemView.setOnClickListener(v -> {
                Interface.onFolderClick(imageModel,position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return filter.size();
    }

    @Override
    public int getItemViewType(int position) {
        int val = 0;
        try {
            if (filter != null && filter.size() > 0) {
                val = filter.get(position).getType();
            } else {
                val = 0;
            }
        } catch (Exception w) {
        }
        return val;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAll(ArrayList<imageModel> itemData) {

        objects = new ArrayList<>();
        objects.addAll(itemData);
        filter = new ArrayList<>();
        filter.addAll(itemData);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView text,count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            text = itemView.findViewById(R.id.text);
            count = itemView.findViewById(R.id.count);

        }

    }
}
