package com.NKRCreations.mi.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NKRCreations.mi.App;
import com.NKRCreations.mi.AppLoader;
import com.NKRCreations.mi.AppsAdapter;
import com.NKRCreations.mi.CommandExecuter;
import com.NKRCreations.mi.Helper;
import com.NKRCreations.mi.MainActivity;
import com.NKRCreations.mi.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppsAdapter adapter;
    private AppLoader appLoader;
    private String busybox;
    private AdView mAdView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);*/
        setHasOptionsMenu(true);
        SharedPreferences data = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        int filterOpt = data.getInt("FILTER", 0);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        BackgroundLoader backgroundLoader = new BackgroundLoader(filterOpt);
        recyclerView = (RecyclerView) root.findViewById(R.id.appList);
        appLoader = new AppLoader(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AppsAdapter(new ArrayList<App>(Collections.EMPTY_LIST), (AppCompatActivity) getActivity());
        recyclerView.setAdapter(adapter);
        busybox = Helper.busyboxPath(getContext());
        backgroundLoader.execute(getActivity());
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               refresh();
            }
        });
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        return root;
    }


    public void refresh(){
        SharedPreferences data = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        int filterOpt = data.getInt("FILTER", 0);
        BackgroundLoader backgroundLoader = new BackgroundLoader(filterOpt);
        backgroundLoader.execute(getActivity());
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.update();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sort) {
            AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
            String[] items = {"All", "System", "User"};
            SharedPreferences data = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
            int filterOpt = data.getInt("FILTER", 0);
            d.setSingleChoiceItems(items, filterOpt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences data = getActivity().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
                    SharedPreferences.Editor writer = data.edit();
                    writer.putInt("FILTER", i);
                    writer.commit();
                    apply(i);
                }
            });
            AlertDialog a = d.create();
            a.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    private class BackgroundLoader extends AsyncTask<Context, Integer, Boolean>{

        int type;
        Context context;
        AlertDialog dialog;

        public BackgroundLoader(int i) {
            this.type = i;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
            builder.setView(R.layout.loading_dialog);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Context... context) {
            appLoader.loadAllApps();
            this.context = context[0];
            if(!Helper.busyboxExists(getContext())){
                Helper.extractBusybox(getContext());
                CommandExecuter ex = new CommandExecuter();
                if(ex.isRooted()){
                    ex.execute("chmod 755 " + busybox + "\n",
                            "exit\n");
                }else {
                    Toast.makeText(getContext(), "App will not work without root permissions", Toast.LENGTH_SHORT).show();
                }
            }
            //Log.i("EXECUTE", "Background task executed");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            apply(type);
            dialog.dismiss();
        }
    }

    private void apply(int type){
        switch(type){
            case 0:
                adapter = new AppsAdapter(appLoader.getAllApps(), (AppCompatActivity) getActivity());
                break;
            case 1:
                adapter = new AppsAdapter(appLoader.getSystemApps(), (AppCompatActivity) getActivity());
                break;
            case 2:
                adapter = new AppsAdapter(appLoader.getUserApps(), (AppCompatActivity) getActivity());
                break;
        }
        recyclerView.setAdapter(adapter);
    }
}