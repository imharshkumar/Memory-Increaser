package com.NKRCreations.mi.ui.help;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.NKRCreations.mi.R;

public class Help extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_help, container, false);
        TextView textView = root.findViewById(R.id.text_contact);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }
}