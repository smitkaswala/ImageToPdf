package com.example.imagetopdf.Adpters;

import static com.example.imagetopdf.Class.FileInfoUtils.getDate;
import static com.example.imagetopdf.Class.FileInfoUtils.getDateView;
import static com.example.imagetopdf.Class.FileInfoUtils.getFormattedDate;
import static com.example.imagetopdf.ConstantsClass.constants.SORTING_INDEX;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagetopdf.Class.DatabaseHelper;
import com.example.imagetopdf.Class.DirectoryUtils;
import com.example.imagetopdf.Class.FileInfoUtils;
import com.example.imagetopdf.Class.FileSortUtils;
import com.example.imagetopdf.Class.PDFFile;
import com.example.imagetopdf.Class.PopulateList;
import com.example.imagetopdf.InterFace.DataSetChanged;
import com.example.imagetopdf.InterFace.EmptyStateChangeListener;
import com.example.imagetopdf.R;
import com.example.imagetopdf.SQLiteDataBase.SqliteDatabase;
import com.example.imagetopdf.UtilsClass.FileUtils;
import com.example.imagetopdf.UtilsClass.PDFRotationUtils;
import com.example.imagetopdf.UtilsClass.PDFUtils;
import com.example.imagetopdf.UtilsClass.stringUtils;
import com.example.imagetopdf.databinding.FolderListPopupBinding;
import com.example.imagetopdf.databinding.PdfDetailsBinding;
import com.example.imagetopdf.databinding.PdfOptionBinding;
import com.example.imagetopdf.databinding.PdfViewBinding;
import com.example.imagetopdf.databinding.RenameOptionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.ViewFilesHolder> implements DataSetChanged, EmptyStateChangeListener {

    Activity mActivity;
    List<PDFFile> mFileList;
    EmptyStateChangeListener mEmptyStateChangeListener;
    ArrayList<Integer> mSelectedFiles;
    FileUtils mFileUtils;
    PDFUtils mPDFUtils;
    DatabaseHelper mDatabaseHelper;
    PDFRotationUtils mPDFRotationUtils;
    SharedPreferences mSharedPreferences;
    SqliteDatabase sqliteDatabase;

    public ViewFilesAdapter(Activity activity, List<PDFFile> feedItems, EmptyStateChangeListener emptyStateChangeListener) {
        this.mActivity = activity;
        this.mFileList = feedItems;
        this.mEmptyStateChangeListener = emptyStateChangeListener;
        mSelectedFiles = new ArrayList<>();
        mFileUtils = new FileUtils(activity);
        mPDFUtils = new PDFUtils(activity);
        mDatabaseHelper = new DatabaseHelper(mActivity);
        mPDFRotationUtils = new PDFRotationUtils(activity);
        sqliteDatabase = new SqliteDatabase(activity);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    @NonNull
    @Override
    public ViewFilesAdapter.ViewFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewFilesHolder(DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.pdf_view, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewFilesAdapter.ViewFilesHolder holder, final int pos) {

        final int position = holder.getAdapterPosition();
        final PDFFile pdfFile = mFileList.get(position);


        holder.binding.file.setText(pdfFile.getPdfFile().getName());
        holder.binding.size.setText(FileInfoUtils.getFormattedSize(pdfFile.getPdfFile()));
        holder.binding.date.setText(getDateView(pdfFile.getPdfFile()));

//        String date = getFormattedDate(pdfFile.getPdfFile());
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

        holder.binding.time.setText(getDate(pdfFile.getPdfFile()));
        holder.binding.lock.setVisibility(pdfFile.isEncrypted() ? View.VISIBLE : View.GONE);
        holder.binding.menu.setOnClickListener(v -> {
            BottomSheetDialog bottom = new BottomSheetDialog(mActivity, R.style.BottomSheetDialogTheme);
            PdfOptionBinding pdfBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.pdf_option, null, false);
            bottom.setContentView(pdfBinding.getRoot());

            if (sqliteDatabase.checkIfUserExit(pdfFile.getPdfFile().getPath())){
                pdfBinding.favorite.setImageResource(R.drawable.ic_like_done);
            }else {
                pdfBinding.favorite.setImageResource(R.drawable.ic_favorite);
            }

            pdfBinding.rename.setOnClickListener(v1 -> {
                onRenameFileClick(position,bottom);
            });
            pdfBinding.delete.setOnClickListener(v1 -> {
                deleteFile(position, bottom);
            });
            pdfBinding.details.setOnClickListener(v1 -> {
                FileDetails(pdfFile.getPdfFile(),bottom);
            });
            pdfBinding.like.setOnClickListener(v1 -> {

                if (pdfBinding.favorite.getDrawable().getConstantState() == Objects.requireNonNull(ContextCompat.getDrawable(mActivity, R.drawable.ic_favorite)).getConstantState())
                { pdfBinding.favorite.setImageResource(R.drawable.ic_like_done);
                    sqliteDatabase.insertInfoTheDatabase(pdfFile.getPdfFile().getPath());
                }else {
                    pdfBinding.favorite.setImageResource(R.drawable.ic_favorite);
                    sqliteDatabase.deleteData(pdfFile.getPdfFile().getPath());

                }
            });
            bottom.show();
        });

        holder.itemView.setOnClickListener(v -> {
            mFileUtils.openFile(pdfFile.getPdfFile().getPath(), FileUtils.FileType.e_PDF);
        });

    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }


    class ViewFilesHolder extends RecyclerView.ViewHolder {

        PdfViewBinding binding;

        public ViewFilesHolder(@NonNull PdfViewBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }

    }

    private void onRenameFileClick(final int position, BottomSheetDialog bottom) {

        RenameOptionBinding renameBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.rename_option, null, false);
        Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(renameBinding.getRoot());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (renameBinding.rename.getText() == null || renameBinding.rename.toString().trim().isEmpty())
            stringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);

         else {
            if (!mFileUtils.isFileExist(renameBinding.rename + mActivity.getString(R.string.pdf_ext))) {
                renameBinding.mOk.setOnClickListener(v -> {
                    renameFile(position, renameBinding.rename.getText().toString());
                    dialog.dismiss();
                    bottom.dismiss();
                });
                renameBinding.mCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        }

        dialog.show();

    }

    private void renameFile(int position, String newName) {
        PDFFile pdfFile = mFileList.get(position);
        File oldFile = pdfFile.getPdfFile();
        String oldPath = oldFile.getPath();
        String newfilename = oldPath.substring(0, oldPath.lastIndexOf('/'))
                + "/" + newName + mActivity.getString(R.string.pdf_ext);
        File newfile = new File(newfilename);
        if (oldFile.renameTo(newfile)) {
            stringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_renamed);
            pdfFile.setPdfFile(newfile);
            notifyDataSetChanged();
            mDatabaseHelper.insertRecord(newfilename, mActivity.getString(R.string.renamed));
        } else
            stringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_file_not_renamed);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<PDFFile> pdfFiles) {
        mFileList = pdfFiles;
        notifyDataSetChanged();
    }

    public PDFUtils getPDFUtils() {
        return mPDFUtils;
    }

    /**
     * Delete single file
     */

    private void deleteFile(int position, BottomSheetDialog bottom) {
        if (position < 0 || position >= mFileList.size())
            return;

        ArrayList<Integer> files = new ArrayList<>();
        files.add(position);
        deleteFiles(files , bottom);
    }

    private void deleteFiles(ArrayList<Integer> files , BottomSheetDialog bottom) {

        int messageAlert, messageSnackbar;
        if (files.size() > 1) {
            messageAlert = R.string.delete_alert_selected;
            messageSnackbar = R.string.snackbar_files_deleted;
        } else {
            messageAlert = R.string.delete_alert_singular;
            messageSnackbar = R.string.snackbar_file_deleted;
        }

        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(mActivity,R.style.MyDialogTheme)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setTitle(messageAlert)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    ArrayList<String> filePath = new ArrayList<>();

                    for (int position : files) {
                        if (position >= mFileList.size())
                            continue;
                        filePath.add(mFileList.get(position).getPdfFile().getPath());
                        mFileList.remove(position);
                    }

                    mSelectedFiles.clear();
                    files.clear();
//                    updateActionBarTitle();
                    notifyDataSetChanged();

                    if (mFileList.size() == 0)
                        mEmptyStateChangeListener.setEmptyStateVisible();

                    AtomicInteger undoClicked = new AtomicInteger();
                    stringUtils.getInstance().getSnackbarwithAction(mActivity, messageSnackbar)
                            .setAction(R.string.snackbar_undoAction, v -> {
                                if (mFileList.size() == 0) {
                                    mEmptyStateChangeListener.setEmptyStateInvisible();
                                }
                                updateDataset();
                                undoClicked.set(1);
                            }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (undoClicked.get() == 0) {
                                for (String path : filePath) {
                                    File fdelete = new File(path);
                                    mDatabaseHelper.insertRecord(fdelete.getAbsolutePath(),
                                            mActivity.getString(R.string.deleted));
                                    if (fdelete.exists() && !fdelete.delete())
                                        stringUtils.getInstance().showSnackbar(mActivity,
                                                R.string.snackbar_file_not_deleted);
                                }
                            }
                        }
                    }).show();
                    bottom.dismiss();
                });
        dialogAlert.create().show();
    }

    private void FileDetails(File file, BottomSheetDialog bottom ) {
        String name = file.getName();
        String path = file.getPath();
        String size = FileInfoUtils.getFormattedSize(file);
        String lastModDate = FileInfoUtils.getFormattedDate(file);

        PdfDetailsBinding detailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.pdf_details, null, false);
        Dialog dialog = new Dialog(mActivity);
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

    @Override
    public void updateDataset() {
        int index = mSharedPreferences.getInt(SORTING_INDEX, FileSortUtils.getInstance().NAME_INDEX);
        new PopulateList(this, this,
                new DirectoryUtils(mActivity), index, null).execute();
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
