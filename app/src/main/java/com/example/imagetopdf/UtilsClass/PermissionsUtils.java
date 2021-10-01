package com.example.imagetopdf.UtilsClass;

import static com.example.imagetopdf.ConstantsClass.constants.READ_PERMISSIONS;
import static com.example.imagetopdf.ConstantsClass.constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static com.example.imagetopdf.ConstantsClass.constants.WRITE_PERMISSIONS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionsUtils {

    private static class SingletonHolder {
        static final PermissionsUtils INSTANCE = new PermissionsUtils();
    }

    public static PermissionsUtils getInstance() {
        return PermissionsUtils.SingletonHolder.INSTANCE;
    }

    public void requestRuntimePermissions(Object context, String[] permissions,
                                          int requestCode) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    permissions, requestCode);
        } else if (context instanceof Fragment) {
            ((Fragment) context).requestPermissions(permissions, requestCode);
        }
    }

    private Context retrieveContext(@NonNull Object context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getApplicationContext();
        } else {
            return ((Fragment) context).requireActivity();
        }
    }

    public void handleRequestPermissionsResult(Activity context, @NonNull int[] grantResults,
                                               int requestCode, int expectedRequest, @NonNull Runnable whenSuccessful) {

        if (requestCode == expectedRequest && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                whenSuccessful.run();
            } else {
                showPermissionDenyDialog(context, requestCode);
            }
        }
    }

    private void showPermissionDenyDialog(Activity activity, int requestCode) {
        String[] permission;
        if (requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) {
            permission = WRITE_PERMISSIONS;
        } else {
            permission = READ_PERMISSIONS;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            new AlertDialog.Builder(activity)
                    .setTitle("Permission Denied")
                    .setMessage("Storage permission is needed for proper functioning of app.")
                    .setPositiveButton("Re-try", (dialog, which) -> {
                        requestRuntimePermissions(activity, permission, REQUEST_CODE_FOR_WRITE_PERMISSION);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            new AlertDialog.Builder(activity)
                    .setTitle("Permission Denied")
                    .setMessage("You have chosen to never ask the permission again, but storage permission is needed for proper functioning of app. ")
                    .setPositiveButton("Enable from settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        activity.startActivity(intent);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        }
    }
}
