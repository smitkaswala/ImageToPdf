package com.example.imagetopdf.Adpters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.InterFace.FontStyleInterface;
import com.example.imagetopdf.R;

public class FontStyleAdapter extends RecyclerView.Adapter<FontStyleAdapter.ViewHolder> {

    Activity activity;
    String[] data;
    Typeface myTypeFace;
    FontStyleInterface interFace;

    public FontStyleAdapter(Activity activity, String[] data, FontStyleInterface interFace){
        this.activity = activity;
        this.data = data;
        this.interFace = interFace;
    }

    @Override
    public FontStyleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.style_view, null));

    }

    @Override
    public void onBindViewHolder(@NonNull FontStyleAdapter.ViewHolder holder, int position) {

        myTypeFace = Typeface.createFromAsset(activity.getAssets(), "textfonts/" + data[position]);
        holder.button.setTypeface(myTypeFace);
        holder.button.setOnClickListener(v -> {
            myTypeFace = Typeface.createFromAsset(activity.getAssets(), "textfonts/" + data[position]);
            interFace.Font(myTypeFace);
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            button = (Button)itemView.findViewById(R.id.btn_style);

        }
    }
}
