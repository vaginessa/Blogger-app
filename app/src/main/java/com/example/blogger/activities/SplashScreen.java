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

    private TextView bloggerTitle;
    private LoadingDialogFragment loadingDialogFragment;

    //for animation opacity
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initComponents();
    }

    private void initComponents()
    {
        bloggerTitle = findViewById(R.id.splash_blogger_text);

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

        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        String userId = FirebaseAuth.getInstance().getUid();

        if (user != null )
        {
            try
            {
                loadingDialogFragment = new LoadingDialogFragment();
                loadingDialogFragment.show(getSupportFragmentManager().beginTransaction(), "loading");

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            Thread.sleep(3000);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getApplicationContext(),"Please login or sign up...!", Toast.LENGTH_LONG).show();
        }
    }
}
