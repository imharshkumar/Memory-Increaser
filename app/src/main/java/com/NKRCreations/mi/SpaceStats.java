package com.NKRCreations.mi;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.StatFs;

public class SpaceStats {

    StatFs statFs;

    public long getFreeSpace(){
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getFreeBytes();
        }else {
            return (statFs.getFreeBlocks() * statFs.getBlockSize());
        }
    }

    public long getTotalSpace(){
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getTotalBytes();
        }else {
            return (statFs.getBlockCount() * statFs.getBlockSize());
        }
    }

    public SpaceStats(String path) {
        statFs = new StatFs(path);
    }
}
