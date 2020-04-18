package com.NKRCreations.mi.ui.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.NKRCreations.mi.CommandExecuter;
import com.NKRCreations.mi.Helper;
import com.NKRCreations.mi.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.io.File;

public class ToolsFragment extends Fragment implements View.OnClickListener {

    private Button btnCreateDisk;
    private EditText edtSize, edtPath;
    private CommandExecuter executer;
    private String path;
    private int size;
    private RewardedVideoAd mRewardedVideoAd;
    private String busybox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busybox = Helper.busyboxPath(getContext());
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                //Toast.makeText(getActivity(), "Rewarded Ad Loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        mRewardedVideoAd.loadAd("ca-app-pub-1660643511323810/2491371473",
                new AdRequest.Builder().build());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        toolsViewModel =
//                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        /*final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        edtPath = root.findViewById(R.id.ed_path);
        edtSize = root.findViewById(R.id.ed_size);
        btnCreateDisk = root.findViewById(R.id.createDiskButton);
        btnCreateDisk.setOnClickListener(this);
        return root;
    }

    private void makeDisk(){
        path = edtPath.getText().toString();
        File file = new File(path);
        if(file.exists() && file.isDirectory()) {
            path = Helper.constructPath(path, ".expandedStorage.img");
            String strsize = edtSize.getText().toString();
            if(strsize.length() == 0){
                Toast.makeText(getContext(), "Enter a valid size", Toast.LENGTH_SHORT).show();
                return;
            }
            size = Integer.parseInt(strsize);
            if (size <= 2048) {
                executer = new CommandExecuter();
                if (executer.isRooted()) {
                    new DiskMaker().execute();
                } else {
                    Toast.makeText(getContext(), "Cannot get root access", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(), "Enter a valid size", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), "Enter a valid directory", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferences preferences = getActivity().getSharedPreferences("DISK_DATA", 0);
        String s = preferences.getString("PATH", null);
        if(s != null){
            File disk = new File(s);
            if(disk.exists() && disk.isFile()){
                Toast.makeText(getContext(), "Disk image already exist", Toast.LENGTH_SHORT).show();
            }else {
                makeDisk();
            }
        }else{
            makeDisk();
        }
        if(mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    private class DiskMaker extends AsyncTask<Void, Void, Void>{

        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
            builder.setView(R.layout.waiting_dialog);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            executer.execute(busybox + " dd if=/dev/zero of=" + path + " bs=1M count=" + size + "\n",
                    "make_ext4fs -l " + (size * 1024 * 1024) + " " + path + "\n",
                    "exit\n");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            executer.close();
            dialog.dismiss();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
            SharedPreferences.Editor writer = sharedPreferences.edit();
            writer.putString("PATH", path);
            writer.putString("MOUNT_PATH", "/data/expandedStorage");
            Toast.makeText(getContext(), "Disk image created", Toast.LENGTH_SHORT).show();
            writer.commit();
            super.onPostExecute(aVoid);
        }
    }
}