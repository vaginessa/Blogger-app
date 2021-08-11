package com.example.blogger.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.dialogs.LoadingDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    //for animation opacity
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initComponents();
        //GoToSignIn();
    }

    private void initComponents()
    {
        TextView bloggerTitle = findViewById(R.id.splash_blogger_text);

        //animate the splash app name text
        bloggerTitle.setTranslationY(300);

        //opacity
        bloggerTitle.setAlpha(v);

        //to make the textview to go back to normal
        bloggerTitle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        final String userId = FirebaseAuth.getInstance().getUid();

        try {
            Thread thread = new Thread(() -> {
                try
                {
                    Thread.sleep(3000);
                    if (user != null) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else {
                        startActivity(new Intent(getApplicationContext(),SigninActivity.class));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void GoToSignIn()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }
}
