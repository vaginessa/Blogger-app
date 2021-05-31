package com.example.blogger.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.activities.MainActivity;
import com.example.blogger.activities.SigninActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileDialogFragment extends DialogFragment {

    private MaterialToolbar toolbar;
    private MaterialButton logout_btn, update_btn;

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

        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        /*getDialog()
                .getWindow()
                .setBackgroundDrawableResource(R.drawable.dialog_bg);*/
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
        logout_btn = view.findViewById(R.id.close_btn);
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
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display the alert dialog
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()));
                builder.setTitle(getString(R.string.alert_title));
                builder.setMessage(getString(R.string.logout_message_text));
                builder.setPositiveButton(getString(R.string.position_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismiss();
                                    startActivity(new Intent(getActivity(), SigninActivity.class));
                                }
                            }, 3000);
                        } else {
                            Toast.makeText(getActivity(), "An error occurred while trying to logout", Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton(getString(R.string.negative_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel the dialog
                        dialog.dismiss();
                    }
                }).show();
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