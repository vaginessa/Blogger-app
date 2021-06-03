package com.example.blogger.dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blogger.R;

import java.util.Objects;

public class LoadingDialogFragment extends DialogFragment {


    public LoadingDialogFragment() {
        // Required empty public constructor
    }

    String caption;

    public LoadingDialogFragment(String caption) {
        this.caption = caption;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        //set dialog width and height
        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //set the dialog gravity
        getDialog()
                .getWindow()
                .setGravity(Gravity.BOTTOM);
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_loading_dialog, container, false);
        context = view.getContext();

        //call methods here


        return view;
    }
}