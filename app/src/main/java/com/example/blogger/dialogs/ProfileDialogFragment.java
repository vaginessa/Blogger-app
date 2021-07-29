package com.example.blogger.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogger.R;
import com.example.blogger.activities.MainActivity;
import com.example.blogger.activities.SigninActivity;
import com.example.blogger.models.UserModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDialogFragment extends DialogFragment {

    private Context context;
    private MaterialToolbar toolbar;
    private TextInputEditText name_txt, surname_txt, email_txt;
    private CircleImageView profileCircleView;
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
        getUserDetails(view);

        return view;
    }

    private void init(ViewGroup view)
    {
        toolbar = view.findViewById(R.id.tool_bar);

        profileCircleView = view.findViewById(R.id.profileImage);

        name_txt = view.findViewById(R.id.profile_name_et);
        surname_txt = view.findViewById(R.id.profile_surname_et);
        email_txt = view.findViewById(R.id.profile_email_et);

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

    private void getUserDetails(ViewGroup view)
    {
        FirebaseDatabase
                .getInstance()
                .getReference("Blog")
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String username = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                            String profile = Objects.requireNonNull(snapshot.child("profile").getValue()).toString();
                            String surname = Objects.requireNonNull(snapshot.child("surname").getValue()).toString();
                            String email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();

                            UserModel user = new UserModel();
                            RequestOptions placeholderOption = new RequestOptions();
                            if (user.getProfile() !=null)
                            {
                               /* Glide.with(Objects.requireNonNull(getContext()))
                                        .applyDefaultRequestOptions(placeholderOption)
                                        .load(profile)
                                        .into(profileCircleView);*/
                                try
                                {
                                    Picasso
                                            .get()
                                            .load(profile)
                                            .into(profileCircleView);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else
                            {
                                placeholderOption.placeholder(R.drawable.ic_account_circle_black_24dp);
                            }

                            name_txt.setText(username);
                            surname_txt.setText(surname);
                            email_txt.setText(email);

                        }else
                        {
                            Toast.makeText(getContext(), "No user info exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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