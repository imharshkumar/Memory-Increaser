package com.NKRCreations.mi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    public static final String ARG_PARAM1 = "param1";

    private AppCompatActivity context;
    private int size;
    private ArrayList<App> apps;
    private LayoutInflater layoutInflater;

    public AppsAdapter(ArrayList<App> apps, AppCompatActivity context) {
        this.apps = apps;
        this.size = apps.size();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.app_view, parent, false);
        AppViewHolder appViewHolder = new AppViewHolder(view);
        return appViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.appName.setText(apps.get(position).getName());
        holder.appIcon.setImageDrawable(apps.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class AppViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView appName;
        ImageView appIcon;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.appName);
            appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Log.i("CLICK", "Name : " + appName.getText());
            NavController navController = Navigation.findNavController(context, R.id.nav_host_fragment);
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, apps.get(getAdapterPosition()).getPackageName());
            navController.navigate(R.id.nav_app_details, args);
        }
    }

}
