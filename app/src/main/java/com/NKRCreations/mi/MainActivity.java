package com.NKRCreations.mi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.navigation.NavigationView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private LinearLayout linearLayout;
    private TextView status, space;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        status = linearLayout.findViewById(R.id.status);
        space = linearLayout.findViewById(R.id.spaceStats);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_help,
                R.id.nav_configuration,
                R.id.nav_app_details)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        update();
        navigationView.bringToFront();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1660643511323810/8896831887");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Toast.makeText(getParent(), "Rewarded Ad Loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                mRewardedVideoAd.loadAd("ca-app-pub-1660643511323810/2491371473",
                        new AdRequest.Builder().build());
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
                mRewardedVideoAd.loadAd("ca-app-pub-1660643511323810/2491371473",
                        new AdRequest.Builder().build());
            }
        });
        mRewardedVideoAd.loadAd("ca-app-pub-1660643511323810/2491371473",
                new AdRequest.Builder().build());
    }

    public void update(){
        SharedPreferences preferences = getSharedPreferences("DISK_DATA", Context.MODE_PRIVATE);
        if(preferences.getBoolean("IS_MOUNTED", false)){
            status.setText("Disk Status: Mounted");
            updateSpace();
        }else {
            status.setText("Disk Status: Unmounted");
            space.setText("../..");
        }
    }

    public void updateSpace(){
        SharedPreferences preferences = getSharedPreferences("DISK_DATA", 0);
        String mntdir = preferences.getString("MOUNT_PATH", "/data/expandedStorage");
        SpaceStats s = new SpaceStats(mntdir);
        space.setText(((int) s.getFreeSpace()/(1024*1024)) + "MB free of " + ((int)s.getTotalSpace()/(1024*1024)) +"MB");
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showInterstitial(){
        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void showSomeAd(){
        if(mRewardedVideoAd.isLoaded()){
            mRewardedVideoAd.show();
        }else {
            if(mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

}
