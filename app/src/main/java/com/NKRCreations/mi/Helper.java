package com.NKRCreations.mi;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Helper {

    public static String busyboxPath(Context context){
        return (context.getFilesDir().getAbsolutePath() + "/busybox");
    }

    public static boolean busyboxExists(Context context){
        File busybox = new File(context.getFilesDir().getAbsolutePath() + "/busybox");
        if(busybox.exists() && busybox.isFile()) {
            //Log.i("BUSYBOX", "Exists");
            return true;
        }else{
            //Log.i("BUSYBOX", "Does not Exist");
            return false;
        }
    }

    public static void extractBusybox(Context context){
        try {
            byte[] data = new byte[2048];
            InputStream inp = context.getAssets().open("busybox");
            FileOutputStream out = new FileOutputStream(context.getFilesDir().getAbsolutePath() + "/busybox");
            while (inp.read(data) > 0){
                out.write(data);
            }
            out.flush();
            inp.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String constructPath(String path, String file){
        if(path.charAt(path.length() - 1) == '/'){
            path += file;
        }else {
            path += "/" + file;
        }
        return path;
    }

    public static AlertDialog createDialog(Context context, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog);
        builder.setView(R.layout.loading_dialog);
        AlertDialog dialog = builder.create();
        dialog.show();
        ((TextView)dialog.findViewById(R.id.dialog_text)).setText(text);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
