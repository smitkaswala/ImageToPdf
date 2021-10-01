package com.example.imagetopdf.Adpters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Class.CrpModel;
import com.example.imagetopdf.InterFace.CropInterface;
import com.example.imagetopdf.R;
import com.example.imagetopdf.databinding.CropItemBinding;

import java.util.ArrayList;

public class CropAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

     Activity context;
     ArrayList<CrpModel> arrayList;
     CropInterface cropInterface;

    public CropAdapter(Activity context, ArrayList<CrpModel> arrayList, CropInterface cropInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.cropInterface = cropInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CropViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.crop_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CropViewHolder holder1 = (CropViewHolder) holder;
        CrpModel crpModel = arrayList.get(position);
        holder1.binding.img.setImageResource(crpModel.img);
        holder1.itemView.setOnClickListener(v -> cropInterface.CropRatio(crpModel.a, crpModel.b,position));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class CropViewHolder extends RecyclerView.ViewHolder {

        CropItemBinding binding;

        public CropViewHolder(@NonNull CropItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}
