package com.example.user.wecare.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.user.wecare.Interface.DrawerLocker;
import com.example.user.wecare.R;

public class AboutFragment extends Fragment {

    public android.support.v7.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("About WeCare");

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);


        return view;
    }
}
