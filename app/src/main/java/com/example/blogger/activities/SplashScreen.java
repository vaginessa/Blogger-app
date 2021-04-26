package com.example.blogger.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.blogger.R;

public class SplashScreen extends AppCompatActivity {

    private TextView bloggerTitle;

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                    finish();

                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
