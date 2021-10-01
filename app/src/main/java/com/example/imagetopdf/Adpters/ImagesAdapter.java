package com.example.imagetopdf.Adpters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.imagetopdf.InterFace.CameraInterFace;
import com.example.imagetopdf.InterFace.FolderClickListener;
import com.example.imagetopdf.R;

import java.io.File;
import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    CameraInterFace interFace;
    ArrayList<String> objects = new ArrayList<>();
    ArrayList<String> filter = new ArrayList<>();
    RequestOptions options = new RequestOptions();
    FolderClickListener Interface;
    boolean isActionOn;
    int From;

    public ImagesAdapter(Activity activity, CameraInterFace interFace,  FolderClickListener Interface) {
        this.activity = activity;
        this.interFace = interFace;
        this.Interface = Interface;
        options = new RequestOptions();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(activity).inflate(R.layout.camera_view, parent, false);
                viewHolder = new MyClass(itemView);
                break;
            case 1:
                itemView = LayoutInflater.from(activity).inflate(R.layout.image_view, parent, false);
                viewHolder = new MyClass1(itemView);
                break;

        }
        assert viewHolder != null;
        return viewHolder;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                MyClass myClass = (MyClass) holder;
                myClass.imageView.setOnClickListener(v -> {
                    interFace.onCameraClick();
                });

                break;

            case 1:
                MyClass1 myClass1 = (MyClass1) holder;
                File file = new File(objects.get(position));
                myClass1.onBind(file, position);

                break;

        }

    }


    public static class MyClass extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyClass(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.camera);
        }
    }

     class MyClass1 extends RecyclerView.ViewHolder {

        ImageView imageView,mSelect;

        public MyClass1(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.images);
            mSelect = itemView.findViewById(R.id.checkbox);

        }

        public void onBind(File file,int position){

            if (isActionOn){
                mSelect.setImageResource(R.drawable.ic_round);
                mSelect.setVisibility(View.VISIBLE);
                Interface.onBind(mSelect,file.getAbsolutePath());
            }else {
                mSelect.setImageResource(R.drawable.ic_round);
                mSelect.setVisibility(View.GONE);
            }
            if (file.exists()) {
                try {
                    Glide.with(activity)
                            .load(file.getPath())
                            .apply(options.centerCrop()
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW))
                            .into(imageView);
                } catch (Exception e) {
                    Glide.with(activity)
                            .load(file.getPath())
                            .apply(options.centerCrop()
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW))
                            .into(imageView);
                }
            }
            itemView.setOnClickListener(v -> {
                Interface.onItemClick(mSelect, position, objects, file);
            });
            itemView.setOnLongClickListener(v -> {
                Interface.onFolderLongClick();
                return false;
            });

        }

    }
    @SuppressLint("NotifyDataSetChanged")
    public void StartAction(){
        this.isActionOn = true;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void StopAction(){
        this.isActionOn = false;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return objects != null ? objects.size() : 0 ;
    }

    @Override
    public int getItemViewType(int position) {

//        int pos = 0;
//        try {
//            if (objects != null && objects.size() > 0){
//                pos = objects.get(position);
//            }else {
//                pos = 0;
//            }
//        }
//        catch (Exception e){
//
//        }
//        return pos;


        if (position == 0)
            return 0;
        else
            return 1;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAll(ArrayList<String> itemView) {
        filter = new ArrayList<>();
        objects = itemView;
        objects.add(0,"");
        filter.addAll(itemView);
//        filter.add(0,"");
        notifyDataSetChanged();

    }

}
