package com.example.imagetopdf.Fragment;

import static com.example.imagetopdf.ConstantsClass.constants.BUNDLE_DATA;
import static com.example.imagetopdf.ConstantsClass.constants.SORTING_INDEX;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.example.imagetopdf.Adpters.ViewFilesAdapter;
import com.example.imagetopdf.Class.DirectoryUtils;
import com.example.imagetopdf.Class.FileSortUtils;
import com.example.imagetopdf.Class.PopulateList;
import com.example.imagetopdf.Class.ViewFilesDividerItemDecoration;
import com.example.imagetopdf.InterFace.EmptyStateChangeListener;
import com.example.imagetopdf.R;
import com.example.imagetopdf.UtilsClass.FileUtils;
import com.example.imagetopdf.UtilsClass.dialogUtils;
import com.example.imagetopdf.databinding.FragmentHomeBinding;

import java.io.File;
import java.util.Objects;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EmptyStateChangeListener {

    FragmentHomeBinding binding;
    ViewFilesAdapter adapter;
    FileUtils mFileUtils;
    DirectoryUtils mDirectoryUtils;
    private int mCurrentSortingIndex;
    private SharedPreferences mSharedPreferences;
    boolean mIsMergeRequired = false;
    int mCountFiles = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mDirectoryUtils = new DirectoryUtils(getActivity());
        mFileUtils = new FileUtils(getActivity());
        mCurrentSortingIndex = mSharedPreferences.getInt(SORTING_INDEX, FileSortUtils.getInstance().NAME_INDEX);
        adapter = new ViewFilesAdapter(getActivity(), null, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        binding.recyclerview.setLayoutManager(mLayoutManager);
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.addItemDecoration(new ViewFilesDividerItemDecoration(binding.getRoot().getContext()));
        binding.swipe.setOnRefreshListener(this);


        int dialogId;
        if (getArguments() != null) {
            dialogId = getArguments().getInt(BUNDLE_DATA);
            dialogUtils.getInstance().showFilesInfoDialog(getActivity(), dialogId);
        }

        checkIfListEmpty();
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDataForQueryChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        return binding.getRoot();

    }

    private void setDataForQueryChange(String s) {
        populatePdfList(s);
    }

    @Override
    public void onRefresh() {
        populatePdfList(null);
        binding.swipe.setRefreshing(false);
    }

    private void checkIfListEmpty() {

        onRefresh();
        final File[] files = mDirectoryUtils.getOrCreatePdfDirectory().listFiles();
        int count = 0;

        if (files == null) {
            showNoPermissionsView();
            return;
        }

        for (File file : files)
            if (!file.isDirectory()) {
                count++;
                break;
            }
        if (count == 0) {
            setEmptyStateVisible();
            mCountFiles = 0;
            updateToolbar();
        }
    }


    private void populatePdfList(@Nullable String query) {
        new PopulateList(adapter, this,
                new DirectoryUtils(getActivity()), mCurrentSortingIndex, query).execute();
    }

    private void updateToolbar() {
        AppCompatActivity activity = ((AppCompatActivity)
                Objects.requireNonNull(requireActivity()));
        ActionBar toolbar = activity.getSupportActionBar();
        if (toolbar != null) {
            getActivity().setTitle(mCountFiles == 0 ?
                    getActivity().getResources().getString(R.string.viewFiles)
                    : String.valueOf(mCountFiles));
            //When one or more than one files are selected refresh
            //ActionBar: set Merge option invisible or visible
            mIsMergeRequired = mCountFiles > 1;
//            mIsAllFilesSelected = mCountFiles == mViewFilesAdapter.getItemCount();
            activity.invalidateOptionsMenu();

        }

    }

    @Override
    public void setEmptyStateVisible() {
        binding.imageType.setVisibility(View.VISIBLE);
    }

    @Override
    public void setEmptyStateInvisible() {
        binding.imageType.setVisibility(View.GONE);
    }

    @Override
    public void showNoPermissionsView() {
        binding.imageType.setVisibility(View.GONE);
    }

    @Override
    public void hideNoPermissionsView() {

    }

    @Override
    public void filesPopulated() {

    }

}