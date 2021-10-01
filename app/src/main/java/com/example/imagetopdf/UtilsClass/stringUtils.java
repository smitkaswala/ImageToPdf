package com.example.imagetopdf.UtilsClass;

import static com.example.imagetopdf.ConstantsClass.constants.pdfDirectory;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Objects;

public class stringUtils {

    private stringUtils() {
    }

    private static class SingletonHolder {
        static final stringUtils INSTANCE = new stringUtils();
    }

    public static stringUtils getInstance() {
        return stringUtils.SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }

    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(Activity context, String resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public Snackbar showIndefiniteSnackbar(Activity context, String resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_INDEFINITE);
    }

    public Snackbar getSnackbarwithAction(Activity context, int resID) {
        return Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG);
    }
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getDefaultStorageLocation() {

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }

        return dir.getAbsolutePath() + pdfDirectory;

    }

    public int parseIntOrDefault(CharSequence text, int def) throws NumberFormatException {
        if (isEmpty(text))
            return def;
        else
            return Integer.parseInt(text.toString());
    }

}
