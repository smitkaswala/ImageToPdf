package com.example.imagetopdf.Adpters;

import static com.example.imagetopdf.Class.FileInfoUtils.getDate;
import static com.example.imagetopdf.Class.FileInfoUtils.getDateView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Class.DatabaseHelper;
import com.example.imagetopdf.Class.FavoritePDF;
import com.example.imagetopdf.Class.FileInfoUtils;
import com.example.imagetopdf.Class.PDFFile;
import com.example.imagetopdf.R;
import com.example.imagetopdf.SQLiteDataBase.SqliteDatabase;
import com.example.imagetopdf.UtilsClass.FileUtils;
import com.example.imagetopdf.UtilsClass.PDFUtils;
import com.example.imagetopdf.UtilsClass.stringUtils;
import com.example.imagetopdf.databinding.PdfDetailsBinding;
import com.example.imagetopdf.databinding.PdfOptionBinding;
import com.example.imagetopdf.databinding.PdfOptionFavoriteBinding;
import com.example.imagetopdf.databinding.PdfViewBinding;
import com.example.imagetopdf.databinding.RenameOptionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Activity activity;
    private List<FavoritePDF> favoritePDFS;
    SqliteDatabase sqliteDatabase;
    FileUtils mFileUtils;
    DatabaseHelper mDatabaseHelper;



    public FavoriteAdapter(Activity activity, List<FavoritePDF> favoritePDFS){
        this.activity = activity;
        this.favoritePDFS = favoritePDFS;
        sqliteDatabase = new SqliteDatabase(activity);
        mFileUtils = new FileUtils(activity);
        mDatabaseHelper = new DatabaseHelper(activity);

    }

    @NonNull
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new FavoriteViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pdf_view, parent, false));

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavoriteViewHolder holder, int position) {
         File file = new File(favoritePDFS.get(position).getKey());
         holder.binding.file.setText(file.getName());
        holder.binding.size.setText(FileInfoUtils.getFormattedSize(file));
//        String date = getDate(file);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//
//        Date iconID_3 = null;
//        try {
//            iconID_3 = dateFormat.parse(date);
//        } catch (
//                ParseException e) {
//            e.printStackTrace();
//        }
//
//        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        holder.binding.date.setText(dateFormat.format(iconID_3));
        holder.binding.date.setText(getDateView(file));
        holder.binding.time.setText(getDate(file));
        holder.binding.lock.setVisibility(file.canRead() ? View.VISIBLE : View.GONE);
        holder.binding.menu.setOnClickListener(v -> {
            BottomSheetDialog bottom = new BottomSheetDialog(activity, R.style.BottomSheetDialogTheme);
            PdfOptionFavoriteBinding pdfBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pdf_option_favorite, null, false);
            bottom.setContentView(pdfBinding.getRoot());

            if (sqliteDatabase.checkIfUserExit(file.getPath())){
                pdfBinding.favorite.setImageResource(R.drawable.ic_like_done);
            }else {
                pdfBinding.favorite.setImageResource(R.drawable.ic_favorite);
            }

            pdfBinding.details.setOnClickListener(v1 -> {
                FileDetails(file,bottom);
            });
            pdfBinding.like.setOnClickListener(v1 -> {

                if (pdfBinding.favorite.getDrawable().getConstantState() == Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.ic_favorite)).getConstantState())
                { pdfBinding.favorite.setImageResource(R.drawable.ic_like_done);
                    sqliteDatabase.insertInfoTheDatabase(file.getPath());
                }else {
                    pdfBinding.favorite.setImageResource(R.drawable.ic_favorite);
                    sqliteDatabase.deleteData(file.getPath());
                    favoritePDFS.remove(position);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
                bottom.dismiss();


            });

            bottom.show();
        });
        holder.itemView.setOnClickListener(v -> {
            mFileUtils.openFile(file.getPath(), FileUtils.FileType.e_PDF);
        });
    }

    @Override
    public int getItemCount() {
        return favoritePDFS.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        PdfViewBinding binding;

        public FavoriteViewHolder(@NonNull PdfViewBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }
    }
    private void FileDetails(File file, BottomSheetDialog bottom ) {
        String name = file.getName();
        String path = file.getPath();
        String size = FileInfoUtils.getFormattedSize(file);
        String lastModDate = FileInfoUtils.getFormattedDate(file);

        PdfDetailsBinding detailsBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.pdf_details, null, false);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(detailsBinding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        detailsBinding.file.setText(name);
        detailsBinding.path.setText(path);
        detailsBinding.size.setText(size);
        detailsBinding.date.setText(lastModDate);
        detailsBinding.mOk.setOnClickListener(v -> {
            dialog.dismiss();
            bottom.dismiss();
        });
        dialog.show();

    }


}
