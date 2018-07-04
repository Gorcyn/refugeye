package com.refugeye;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class About extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_about, container, false);
        ((TextView)view.findViewById(R.id.about_name)).setText(Html.fromHtml(getString(R.string.app_name_cdata)));
        view.findViewById(R.id.about_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    activity.getSupportFragmentManager().beginTransaction().remove(About.this).commit();
                }
            }
        });
        return view;
    }
}
