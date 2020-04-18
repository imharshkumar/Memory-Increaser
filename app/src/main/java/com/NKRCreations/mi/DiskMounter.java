package com.NKRCreations.mi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class DiskMounter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String busybox = Helper.busyboxPath(context);
        //Toast.makeText(context, "Boot Completed", Toast.LENGTH_SHORT).show();
        //Log.i("BOOT", "boot completed");
        SharedPreferences preferences = context.getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
        String path = preferences.getString("PATH", null);
        String mntPath = preferences.getString("MOUNT_PATH", "/data/expandedStorage");
        boolean isMounted = preferences.getBoolean("IS_MOUNTED", false);
        if(path != null && isMounted){
            File f = new File(path);
            if(f.exists() && f.isFile()) {
                CommandExecuter e = new CommandExecuter();
                File file = new File(mntPath);
                if(!file.exists() && !file.isDirectory()) {
                    e.execute("mkdir " + mntPath);
                }
                e.execute(
                        busybox + " mknod /dev/block/loop999 b 7 999\n",
                        busybox + " losetup /dev/block/loop999 " + path + "\n",
                        busybox + " mount /dev/block/loop999 " + mntPath + "\n",
                        "exit\n");
                e.close();

            }
        }
    }
}
