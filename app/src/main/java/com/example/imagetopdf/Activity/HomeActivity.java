package com.example.imagetopdf.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.imagetopdf.Fragment.HomeFragment;
import com.example.imagetopdf.Fragment.LikeFragment;
import com.example.imagetopdf.R;
import com.example.imagetopdf.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityHomeBinding binding;
    Fragment currentFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.home.setOnClickListener(this);
        binding.like.setOnClickListener(this);
        binding.mAdd.setOnClickListener(v1 -> {
            Intent i = new Intent(HomeActivity.this,ListImageActivity.class);
            startActivity(i);
        });

        getSupportFragmentManager().beginTransaction().replace(binding.frame.getId(), new HomeFragment()).commit();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.home){

            currentFragment = new HomeFragment();
            binding.home.setImageResource(R.drawable.ic_home_lite);
            binding.like.setImageResource(R.drawable.ic_like_donee);

        }

        else if (v.getId() == R.id.like){

            currentFragment = new LikeFragment();
            binding.home.setImageResource(R.drawable.ic_home);
            binding.like.setImageResource(R.drawable.ic_like);

        }

        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(binding.frame.getId(), currentFragment).commit();
        }

    }

}