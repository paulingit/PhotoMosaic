package com.canva.photomosaic.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionChecker {

    private Context mContext;

    public PermissionChecker(Context context){
        mContext = context;
    }

    private boolean hasPermission(@NonNull String permission) {
        int permissionStatus = ContextCompat.checkSelfPermission(mContext, permission);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasStorageReadPermission(){
        return hasPermission(READ_EXTERNAL_STORAGE);
    }

    public boolean hasStorageWritePermission(){
        return hasPermission(WRITE_EXTERNAL_STORAGE);
    }


}
