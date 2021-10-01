package com.example.imagetopdf.UtilsClass;

import static com.example.imagetopdf.ConstantsClass.constants.AUTHORITY_APP;
import static com.example.imagetopdf.ConstantsClass.constants.PATH_SEPERATOR;
import static com.example.imagetopdf.ConstantsClass.constants.STORAGE_LOCATION;
import static com.example.imagetopdf.ConstantsClass.constants.pdfExtension;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.imagetopdf.InterFace.Consumer;
import com.example.imagetopdf.R;
import com.example.imagetopdf.databinding.SaveDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FileUtils(Activity mContext) {
        this.mContext = mContext;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public enum FileType{
        e_PDF,
        e_TXT
    }

    public void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(uri);
        shareFile(uris);
    }

    public void shareMultipleFiles(List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file : files) {
            Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
            uris.add(uri);
        }
        shareFile(uris);
    }
    private void shareFile(ArrayList<Uri> uris){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mContext.getString(R.string.pdf_type));
        mContext.startActivity(Intent.createChooser(intent,
                mContext.getResources().getString(R.string.share_chooser)));
    }

    public void openFile(String path, FileType fileType) {
        if (path == null) {
            stringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found);
            return;
        }
        openFileInternal(path, fileType == FileType.e_PDF ?
                mContext.getString(R.string.pdf_type) : mContext.getString(R.string.txt_type));
    }

    private void openFileInternal(String path, String dataType) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            Uri uri = FileProvider.getUriForFile(mContext,mContext.getPackageName() + ".provider", file);
            target.setDataAndType(uri, dataType);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
        } catch (Exception e) {
            stringUtils.getInstance().showSnackbar(mContext, R.string.error_open_file);
        }
    }

    private void openIntent(Intent intent) {
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            stringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_pdf_app);
        }
    }

    private int checkRepeat(String finalOutputFile, final List<File> mFile) {
        boolean flag = true;
        int append = 0;
        while (flag) {
            append++;
            String name = finalOutputFile.replace(mContext.getString(R.string.pdf_ext),
                    append + mContext.getString(R.string.pdf_ext));
            flag = mFile.contains(new File(name));
        }

        return append;

    }

    public String getUriRealPath(Uri uri) {

        if (uri == null || fileUriUtils.getInstance().isWhatsappImage(uri.getAuthority()))
            return null;

        return fileUriUtils.getInstance().getUriRealPathAboveKitkat(mContext, uri);

    }

    public boolean isFileExist(String mFileName) {
        String path = mSharedPreferences.getString(STORAGE_LOCATION,
                stringUtils.getInstance().getDefaultStorageLocation()) + mFileName;
        File file = new File(path);

        return file.exists();
    }

    public String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme == null)
            return null;

        if (scheme.equals("file")) {
            return uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    cursor.moveToFirst();
                    fileName = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }

        return fileName;
    }

    public static String getFileName(String path) {
        if (path == null)
            return null;

        int index = path.lastIndexOf(PATH_SEPERATOR);
        return index < path.length() ? path.substring(index + 1) : null;
    }

    public static String getFileNameWithoutExtension(String path) {
        if (path == null || path.lastIndexOf(PATH_SEPERATOR) == -1)
            return path;

        String filename = path.substring(path.lastIndexOf(PATH_SEPERATOR) + 1);
        filename = filename.replace(pdfExtension, "");

        return filename;
    }

    public static String getFileDirectoryPath(String path) {
        return path.substring(0, path.lastIndexOf(PATH_SEPERATOR) + 1);
    }

    public String getLastFileName(ArrayList<String> filesPath) {
        if (filesPath.size() == 0)
            return "";

        String lastSelectedFilePath = filesPath.get(filesPath.size() - 1);
        String nameWithoutExt = stripExtension(getFileNameWithoutExtension(lastSelectedFilePath));

        return nameWithoutExt + mContext.getString(R.string.pdf_suffix);

    }

    public String stripExtension(String fileNameWithExt) {
        // Handle null case specially.
        if (fileNameWithExt == null) return null;

        // Get position of last '.'.
        int pos = fileNameWithExt.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return fileNameWithExt;

        // Otherwise return the string, up to the dot.
        return fileNameWithExt.substring(0, pos);

    }

    public void openImage(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file);
        target.setDataAndType(uri, "image/*");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)));
    }

    public Intent getFileChooser() {
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, mContext.getString(R.string.pdf_type));

        return Intent.createChooser(intent, mContext.getString(R.string.merge_file_select));
    }

    String getUniqueFileName(String fileName) {
        String outputFileName = fileName;
        File file = new File(outputFileName);

        if (!isFileExist(file.getName()))
            return outputFileName;

        File parentFile = file.getParentFile();
        if (parentFile != null) {
            File[] listFiles = parentFile.listFiles();

            if (listFiles != null) {
                int append = checkRepeat(outputFileName, Arrays.asList(listFiles));
                outputFileName = outputFileName.replace(mContext.getString(R.string.pdf_ext),
                        append + mContext.getResources().getString(R.string.pdf_ext));
            }
        }

        return outputFileName;
    }

    public void openSaveDialog(String preFillName, String ext, Consumer<String> saveMethod) {

        MaterialDialog.Builder builder = dialogUtils.getInstance().createCustomDialog(mContext,
                R.string.creating_pdf, R.string.enter_file_name);
        builder.input(mContext.getString(R.string.example), preFillName, (dialog, input) -> {
            if (stringUtils.getInstance().isEmpty(input)) {
                stringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_name_not_blank);
            } else {
                final String filename = input.toString();
                if (!isFileExist(filename + ext)) {
                    saveMethod.accept(filename);
                }
                else {
                    MaterialDialog.Builder builder2 = dialogUtils.getInstance().createOverwriteDialog(mContext);
                    builder2.onPositive((dialog2, which) -> saveMethod.accept(filename))
                            .onNegative((dialog1, which) ->
                                    openSaveDialog(preFillName, ext, saveMethod)).show();
                }

            }

        }).show();

    }
}
