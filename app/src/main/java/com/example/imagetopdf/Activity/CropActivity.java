package com.example.imagetopdf.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.imagetopdf.Adpters.CropAdapter;
import com.example.imagetopdf.Class.CrpModel;
import com.example.imagetopdf.InterFace.CropInterface;
import com.example.imagetopdf.R;
import com.example.imagetopdf.UtilsClass.Utils;
import com.example.imagetopdf.databinding.ActivityCropBinding;
import com.isseiaoki.simplecropview.CropImageView;

import java.util.ArrayList;

public class CropActivity extends AppCompatActivity {

    private ActivityCropBinding binding;
     CropAdapter cropAdapter;
     CropInterface cropInterface;
     String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop);

        file = getIntent().getStringExtra("File");

        Toast.makeText(getApplicationContext(), "file" + file, Toast.LENGTH_SHORT).show();

//        Glide.with(getApplicationContext())
//                .load(file)
//                .into(binding.cropImageView);

        cropInterface = new CropInterface() {
            @Override
            public void CropRatio(int x, int y, int p) {
                if (p == 0)
                    binding.cropImageView.setCropMode(CropImageView.CropMode.FREE);
                else
                    binding.cropImageView.setCustomRatio(x, y);

            }
        };

        binding.rvCrp.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cropAdapter = new CropAdapter(this, getCrpItem(), cropInterface);
        binding.rvCrp.setAdapter(cropAdapter);


        Bitmap bitmap = Utils.mainBitmap;

        String filePath = file;
        Bitmap bit = BitmapFactory.decodeFile(filePath);


        binding.cropImageView.setImageBitmap(bit);
        binding.cropImageView.setCropMode(CropImageView.CropMode.FREE);


        binding.ivDone.setOnClickListener(v -> {
            Utils.mainBitmap = binding.cropImageView.getCroppedBitmap();
            Utils.isEdited = true;
            onBackPressed();
        });

    }

    private ArrayList<CrpModel> getCrpItem() {
        ArrayList<CrpModel> crp = new ArrayList<>();
        crp.add(new CrpModel(R.drawable.ic_crp_free, 0, 0));
        crp.add(new CrpModel(R.drawable.ic_crp_insta_1, 1, 1));
        crp.add(new CrpModel(R.drawable.ic_crp_insta_2, 4, 5));
        crp.add(new CrpModel(R.drawable.ic_crp_insta_3, 9, 16));
        crp.add(new CrpModel(R.drawable.ic_crp_4_3, 4, 3));
        crp.add(new CrpModel(R.drawable.ic_crp_2_3, 2, 3));
        crp.add(new CrpModel(R.drawable.ic_crp_3_2, 3, 2));
        crp.add(new CrpModel(R.drawable.ic_crp_9_16, 9, 16));
        crp.add(new CrpModel(R.drawable.ic_crp_16_9, 16, 9));
        crp.add(new CrpModel(R.drawable.ic_crp_1_2, 1, 2));
        crp.add(new CrpModel(R.drawable.ic_crp_a4, 1, 2));
        crp.add(new CrpModel(R.drawable.ic_crp_a5, 1, 2));
        crp.add(new CrpModel(R.drawable.ic_crp_fb_1, 4, 5));
        crp.add(new CrpModel(R.drawable.ic_crp_fb_2, 16, 9));
        crp.add(new CrpModel(R.drawable.ic_crp_pin, 2, 3));
        crp.add(new CrpModel(R.drawable.ic_crp_yt, 16, 9));
        crp.add(new CrpModel(R.drawable.ic_crp_twit_1, 16, 9));
        crp.add(new CrpModel(R.drawable.ic_crp_twit_2, 3, 1));

        return crp;
    }
}