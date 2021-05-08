package com.example.blogger.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blogger.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProfileDialogFragment extends DialogFragment {

    private MaterialToolbar toolbar;
    private MaterialButton close_btn, update_btn;

    public ProfileDialogFragment() {
        // Required empty public constructor
    }

    String title;

    public ProfileDialogFragment(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog()
                .getWindow()
                .setLayout(1000, 1800);

        getDialog()
                .getWindow()
                .setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_profile_dialog, container, false);

        //call methods here
        init(view);
        myToolbar(view);
        dialogControls(view);

        return view;
    }

    private void init(ViewGroup view)
    {
        toolbar = view.findViewById(R.id.tool_bar);
        close_btn = view.findViewById(R.id.close_btn);
        update_btn = view.findViewById(R.id.update_btn);
    }

    private void myToolbar(ViewGroup view) {

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(getString(R.string.profile_text));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    private void dialogControls(ViewGroup view)
    {
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dismiss the dialog
                dismiss();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //display this message
                Toast.makeText(getActivity(), getString(R.string.profile_successful_text), Toast.LENGTH_LONG).show();
            }
        });
    }
}