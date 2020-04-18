package com.NKRCreations.mi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;


public class AppDetailsFragment extends Fragment implements View.OnClickListener {

    private ApplicationInfo applicationInfo;
    private PackageInfo packageInfo;
    private TextView name, dataDir, apkPath, version;
    private ImageView icon;
    private PackageManager pm;
    private String packageName;
    private Button backup, move;
    private String busybox;
    private SharedPreferences preferences;
    private boolean isMoved, isMounted;

    private void moveApp(){
        AlertDialog dialog = Helper.createDialog(getContext(), "Moving app...");
        if((applicationInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
            Toast.makeText(getActivity(), "Cannot move a system app", Toast.LENGTH_LONG).show();
        } else {
            CommandExecuter mover = new CommandExecuter();
            if(!mover.isRooted()){
                Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            String mountPath = preferences.getString("MOUNT_PATH",  "/data/expandedStorage");
            String[] s = applicationInfo.sourceDir.split("/");
            String[] d = applicationInfo.dataDir.split("/");
            mover.execute(busybox + " mv " + applicationInfo.sourceDir + " " + mountPath + "\n",
                    busybox + " mv " + applicationInfo.dataDir + " " + mountPath + "\n",
                    busybox + " ln -s " + mountPath + "/" + s[s.length -1] + " " + applicationInfo.sourceDir + "\n",
                    busybox + " ln -s " + mountPath + "/" + d[d.length -1] + " " + applicationInfo.dataDir + "\n",
                    "exit\n");
            mover.close();
            Toast.makeText(getActivity(), "Moved Successfully", Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
        ((MainActivity)getActivity()).showSomeAd();
    }

    private void movetointernal(){
        AlertDialog dialog = Helper.createDialog(getContext(), "Please wait...");
        CommandExecuter executer = new CommandExecuter();
        if(!executer.isRooted()){
            Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        String mountPath = preferences.getString("MOUNT_PATH", "/data/expandedStorage");
        String[] s = applicationInfo.sourceDir.split("/");
        String[] d = applicationInfo.dataDir.split("/");
        String dd = "";
        for (int i = 0; i < d.length - 1; i++){
            dd += d[i];
            if(i != d.length - 2){
                dd += "/";
            }
        }
        executer.execute(busybox + " mv " + mountPath +"/" + s[s.length - 1] + " " + applicationInfo.sourceDir + "\n",
                busybox + " mv " + mountPath +"/" + d[d.length -1]+ " " + dd + "\n",
                "exit\n");
        executer.close();
        dialog.dismiss();
        Toast.makeText(getContext(), "Successfully moved to internal storage", Toast.LENGTH_SHORT).show();
        ((MainActivity)getActivity()).showInterstitial();
    }

    private void makebackup(){
        AlertDialog dialog = Helper.createDialog(getContext(), "Please wait...");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        String backupPath = sharedPreferences.getString("BACKUP_DIR", null);
        if(backupPath == null){
            Toast.makeText(getContext(), "Setup backup directory first", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        CommandExecuter executer = new CommandExecuter();
        if(!executer.isRooted()){
            Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        executer.execute(busybox + " cp " + applicationInfo.sourceDir + " " + backupPath + "\n",
                "exit\n");
        executer.close();
        Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
        dialog.dismiss();
        ((MainActivity)getActivity()).showSomeAd();
    }

    public AppDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pm = getActivity().getPackageManager();
        packageName = getArguments().getString(AppsAdapter.ARG_PARAM1);
        try {
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        busybox = Helper.busyboxPath(getContext());
        preferences = getActivity().getSharedPreferences("DISK_DATA", 0);
        isMounted = preferences.getBoolean("IS_MOUNTED", false);
        if(isMounted) {
            String mountPath = preferences.getString("MOUNT_PATH", null);
            if(mountPath != null) {
                File file = new File(mountPath + "/" +packageName);
                isMoved = file.exists();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View detailView = inflater.inflate(R.layout.fragment_app_details, container, false);
        name = detailView.findViewById(R.id.detailsName);
        icon = detailView.findViewById(R.id.detailIcon);
        version = detailView.findViewById(R.id.version);
        dataDir = detailView.findViewById(R.id.dataDir);
        apkPath = detailView.findViewById(R.id.apkPath);
        backup = detailView.findViewById(R.id.backup);
        move = detailView.findViewById(R.id.move);
        backup.setOnClickListener(this);
        move.setOnClickListener(this);
        String appName = (String) applicationInfo.loadLabel(pm);
        name.setText(appName);
        icon.setImageDrawable(applicationInfo.loadIcon(pm));
        version.setText("Version: " + packageInfo.versionName);
        dataDir.setText("Data Dir: " + applicationInfo.dataDir);
        apkPath.setText("Apk Path: " + applicationInfo.publicSourceDir);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(appName);
        return  detailView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.mov_to_internal:
                if(isMounted){
                    if(isMoved){
                        movetointernal();
                    }else {
                        Toast.makeText(getContext(), "App not found on virtual disk", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getContext(), "Disk not mounted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.backup:
                makebackup();
                break;
            case R.id.move:
                if(isMounted){
                    if(!isMoved){
                        moveApp();
                    }else {
                        Toast.makeText(getContext(), "Already on virtual disk", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getContext(), "Disk not mounted", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
