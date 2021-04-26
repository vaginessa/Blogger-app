package com.example.blogger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView closeImage;
    private CircleImageView profile_pic;
    private TextView combinedNames_txt, email_txt,name_txt, surname_txt;
    private Button updateButton;

    //firebase components
    private DatabaseReference profileRef;
    private FirebaseAuth profileAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase
        profileAuth = FirebaseAuth.getInstance();

        //call methods
        initComponents();
        GoToMainActivity();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //call get user details method
        getUserDetails();
    }

    private void initComponents()
    {
        email_txt = (TextView) findViewById(R.id.profile_email_text);
        name_txt = (TextView) findViewById(R.id.profile_name_text);
        surname_txt = (TextView) findViewById(R.id.profile_surname_text);
        combinedNames_txt = (TextView) findViewById(R.id.profile_combined_text);

        //profile picture
        profile_pic = (CircleImageView)findViewById(R.id.profile_image);

        updateButton = (Button) findViewById(R.id.update_button);

        closeImage = (ImageView)findViewById(R.id.close_profile_icon);
    }

    private void getUserDetails()
    {
        profileRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Blog");
        String currentUser = profileAuth.getCurrentUser().getUid();

        profileRef.child("Users").child(currentUser)
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        FirebaseUser user = profileAuth.getCurrentUser();

                        if (snapshot.exists())
                        {
                            if (user != null)
                            {
                                String name = snapshot.child("name").getValue().toString();
                                String surname = snapshot.child("surname").getValue().toString();
                                String email = snapshot.child("email").getValue().toString();
                                String image = snapshot.child("profile").getValue().toString();

                                combinedNames_txt.setText(name + " " + surname);
                                name_txt.setText(name);
                                surname_txt.setText(surname);
                                email_txt.setText(email);

                                //try to retrieve image for profile using picasso
                                try
                                {
                                    Picasso.get()
                                            .load(image)
                                            .into(profile_pic);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("Loading...");

        try
        {
            dialog.show();
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            },3000);

        }catch (Exception e)
        {
            Toast.makeText(ProfileActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void GoToMainActivity()
    {
        final ProgressDialog profileDialog = new ProgressDialog(ProfileActivity.this);
        profileDialog.setMessage("Loading...");

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    profileDialog.show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                            profileDialog.dismiss();
                        }
                    },3000);

                }catch (Exception e)
                {
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
