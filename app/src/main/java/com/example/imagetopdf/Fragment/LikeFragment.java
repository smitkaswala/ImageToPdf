package com.example.imagetopdf.Fragment;

import static androidx.databinding.DataBindingUtil.inflate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagetopdf.Adpters.FavoriteAdapter;
import com.example.imagetopdf.Adpters.ViewFilesAdapter;
import com.example.imagetopdf.InterFace.EmptyStateChangeListener;
import com.example.imagetopdf.R;
import com.example.imagetopdf.SQLiteDataBase.SqliteDatabase;
import com.example.imagetopdf.databinding.FragmentLikeBinding;

public class LikeFragment extends Fragment implements EmptyStateChangeListener {

    FragmentLikeBinding binding;
    SqliteDatabase sqliteDatabase;
    FavoriteAdapter adapter;
    GridLayoutManager gridLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  inflate(inflater,R.layout.fragment_like, container, false);

        sqliteDatabase = new SqliteDatabase(getActivity());
        gridLayoutManager = new GridLayoutManager(getContext(), 1);


        return binding.getRoot();

    }

    @Override
    public void onStart() {

        if (sqliteDatabase.favoritePdf() != null && sqliteDatabase.favoritePdf().size() >= 1){
            binding.recyclerview.setLayoutManager(gridLayoutManager);
            adapter = new FavoriteAdapter(getActivity(),sqliteDatabase.favoritePdf());
            binding.recyclerview.setAdapter(adapter);

        }

        super.onStart();
    }

    @Override
    public void setEmptyStateVisible() {

    }

    @Override
    public void setEmptyStateInvisible() {

    }

    @Override
    public void showNoPermissionsView() {

    }

    @Override
    public void hideNoPermissionsView() {

    }

    @Override
    public void filesPopulated() {

    }
}