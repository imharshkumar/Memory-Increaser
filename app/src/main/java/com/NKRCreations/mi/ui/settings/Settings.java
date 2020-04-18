package com.NKRCreations.mi.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.NKRCreations.mi.CommandExecuter;
import com.NKRCreations.mi.Helper;
import com.NKRCreations.mi.MainActivity;
import com.NKRCreations.mi.R;
import com.NKRCreations.mi.SettingsData;

import java.io.File;

public class Settings extends Fragment implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private ListView list;
    private String busybox;
    private EditText edt, edt2, edt3;
    private int index;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        list = root.findViewById(R.id.settings_listview);
        SettingsData[] data = new SettingsData[]{
                new SettingsData("Mount", "Mount the created virtual disk"),
                new SettingsData("Unmount", "Unmount the previously mounted virtual disk"),
                new SettingsData("Backup Dir", "Set a backup directory"),
                new SettingsData("Recover", "Recover older image"),
                new SettingsData("Mount Path", "Set mount path")
        };
        SettingsAdapter adapter = new SettingsAdapter(data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        busybox = Helper.busyboxPath(getContext());
        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (i){
            case 0:
                SharedPreferences preferences = getActivity().getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
                String path = preferences.getString("PATH", null);
                String mntPath = preferences.getString("MOUNT_PATH", "/data/expandedStorage");
                if(path != null){
                    File f = new File(path);
                    if(f.exists() && f.isFile()) {
                        CommandExecuter e = new CommandExecuter();
                        if(e.isRooted()) {
                            File file = new File(mntPath);
                            if (!file.exists() && !file.isDirectory()) {
                                e.execute("mkdir " + mntPath);
                            }
                            e.execute(
                                    busybox + " mknod /dev/block/loop999 b 7 999\n",
                                    busybox + " losetup /dev/block/loop999 " + path + "\n",
                                    busybox + " mount /dev/block/loop999 " + mntPath + "\n",
                                    busybox + " chown 1000:1000 " + mntPath,
                                    busybox + " chmod 771 " + mntPath,
                                    "exit\n");
                            e.close();
                            SharedPreferences.Editor w = preferences.edit();
                            w.putBoolean("IS_MOUNTED", true);
                            w.commit();
                            Toast.makeText(getContext(), "Mounted Successfully", Toast.LENGTH_SHORT).show();
                            MainActivity activity = (MainActivity) getActivity();
                            activity.update();
                        }else{
                            Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "Virtual Disk not found", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Virtual Disk not found", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
                String mntpath = sharedPreferences.getString("MOUNT_PATH", "/data/expandedStorage");
                if(sharedPreferences.getBoolean("IS_MOUNTED", false)) {
                    CommandExecuter ex = new CommandExecuter();
                    if(!ex.isRooted()){
                        Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    ex.execute(
                            busybox + " umount " + mntpath + "\n",
                            busybox + " losetup -d /dev/block/loop999\n",
                            busybox + " rm /dev/block/loop999\n",
                            "exit\n");
                    ex.close();
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putBoolean("IS_MOUNTED", false);
                    e.commit();
                    Toast.makeText(getContext(), "Unmounted Successfully", Toast.LENGTH_SHORT).show();
                    MainActivity mactivity = (MainActivity) getActivity();
                    mactivity.update();
                }else {
                    Toast.makeText(getContext(), "Virtual Disk not mounted", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(R.layout.backup_dir_dialog);
                final AlertDialog dialog = builder.create();
                dialog.show();
                final EditText edtBackupPath = dialog.findViewById(R.id.backup_dir);
                Button btnConfirmBackupDir = dialog.findViewById(R.id.btn_backup_dir_confirm);
                btnConfirmBackupDir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferencesB = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferencesB.edit();
                        editor.putString("BACKUP_DIR", edtBackupPath.getText().toString());
                        editor.commit();
                        CommandExecuter executer = new CommandExecuter();
                        if(executer.isRooted()) {
                            executer.execute(busybox + " mkdir " + edtBackupPath.getText().toString() + "\n",
                                    "exit\n");
                            executer.close();
                        }else {
                            Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                //Toast.makeText(getContext(), "Backup", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setView(R.layout.recover_dialog);
                index = 0;
                builder1.setPositiveButton("OK", this);
                AlertDialog mDialog = builder1.create();
                mDialog.show();
                edt = mDialog.findViewById(R.id.recover_path);
                edt2 = mDialog.findViewById(R.id.recover_path_mount);
                break;
            case 4:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setView(R.layout.mount_path_dialog);
                index = 1;
                builder2.setPositiveButton("OK", this);
                AlertDialog mDialog1 = builder2.create();
                mDialog1.show();
                edt3 = mDialog1.findViewById(R.id.mount_path);
                break;

        }
    }

    //Recovering previously created disk image
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(index == 0) {
            String path = edt.getText().toString();
            String mountPath = edt2.getText().toString();
            if (path != null && mountPath != null && path.length() != 0 && mountPath.length() != 0) {
                path = Helper.constructPath(path, ".expandedStorage.img");
                if (mountPath.endsWith("/")) {
                    mountPath = mountPath.substring(0, mountPath.length() - 2);
                }
                File disk = new File(path);
                if (disk.exists() && disk.isFile()) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
                    SharedPreferences.Editor writer = sharedPreferences.edit();
                    writer.putString("PATH", path);
                    writer.putString("MOUNT_PATH", mountPath);
                    writer.commit();
                    Toast.makeText(getContext(), "Disk image found now you can mount it in settings", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No disk image found in path entered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Both fields are necessary", Toast.LENGTH_SHORT).show();
            }
        } else if (index == 1) {
            String mountPath = edt3.getText().toString();
            if(mountPath != null && mountPath.length() != 0) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
                boolean isMnted = sharedPreferences.getBoolean("IS_MOUNTED", false);
                if(!isMnted) {
                    SharedPreferences.Editor writer = sharedPreferences.edit();
                    writer.putString("MOUNT_PATH", mountPath);
                    writer.commit();
                }else {
                    Toast.makeText(getContext(), "Unmount the disk first", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(), "Enter a path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        ((MainActivity)getActivity()).showSomeAd();
        super.onDestroyView();
    }

    private class  SettingsAdapter extends BaseAdapter{

        SettingsData[] data;

        public SettingsAdapter(SettingsData[] data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.settings_item, viewGroup, false);
            TextView title = view.findViewById(R.id.major_title);
            TextView details = view.findViewById(R.id.details);
            title.setText(data[i].title);
            details.setText(data[i].details);
            //Log.i("DATA_SETTINGS", getCount() + "");
            return view;
        }
    }
}