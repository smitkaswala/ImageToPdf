package com.example.imagetopdf.Adpters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.InterFace.ColorInterface;
import com.example.imagetopdf.R;

public class FontColorAdapter extends RecyclerView.Adapter<FontColorAdapter.ViewHolder> {

    Activity activity;
    int[] data;
    Typeface myTypeface;
    ColorInterface colorInterface;

    public FontColorAdapter(Activity activity, int[] data, ColorInterface colorInterface){
        this.activity = activity;
        this.data = data;
        this.colorInterface = colorInterface;
    }

    @SuppressLint("InflateParams")
    @Override
    public FontColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull FontColorAdapter.ViewHolder holder, int position) {

        holder.mCardColor.setCardBackgroundColor(data[position]);
        holder.mCardColor.setOnClickListener(v -> {
            colorInterface.ColorCode(data[position]);
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mCardColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardColor =  itemView.findViewById(R.id.mCardColor);

        }
    }
}
