package com.example.blogger.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.adapters.PostsAdapter;
import com.example.blogger.dialogs.AddPostDialogFragment;
import com.example.blogger.dialogs.LoadingDialogFragment;
import com.example.blogger.dialogs.ProfileDialogFragment;
import com.example.blogger.models.PostsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton new_post, logout_button;
    private ImageView close_icon, profile_icon;
    private TextView appbar_title;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*---firebase assignment--*/
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Blog");

        new_post = findViewById(R.id.fab_new_post);
        //logout_button = (FloatingActionButton)findViewById(R.id.fab_logout);

        //call methods
        initAppbarComponents();
        postActivity();
        GoToProfile();
        //logoutUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getEmail(),Toast.LENGTH_LONG).show();
        }
        //get all the posts
        getAllPosts();
    }

    private void initAppbarComponents()
    {
        //close_icon = (ImageView)findViewById(R.id.icon_close);
        profile_icon = findViewById(R.id.icon_profile);
        //appbar_title = (TextView)findViewById(R.id.appbar_title);

        //set the appbar title
        //appbar_title.setText("Posts");
        android.widget.Toolbar toolbar = findViewById(R.id.my_toolbar);

    }

    private void getAllPosts()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.posts_rv);
        final ArrayList<PostsModel> list = new ArrayList<PostsModel>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PostsAdapter adapter = new PostsAdapter( this,list);
        recyclerView.setAdapter(adapter);

        try
        {
            String currentDate = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault()).format(new Date());

            /*for (int i = 0 ; i < 20 ; i++)
            {
                PostsModel posts = new PostsModel();

                posts.setAuthor("thor ragnor");
                posts.setId(null);
                posts.setUser_id(null);
                posts.setTimeStamp(currentDate+" "+currentTime);
                posts.setDesc("this is a test for the posts description");
                posts.setUrl(null);

                list.add(posts);
            }
            adapter = new PostsAdapter(getApplicationContext(), list);
            recyclerView.setAdapter(adapter);*/

            FirebaseFirestore
                    .getInstance()
                    .collection("Posts")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                for (final DocumentChange dc : value.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                            list.add(dc.getDocument().toObject(PostsModel.class));
                                            adapter.notifyDataSetChanged();
                                            break;
                                        case MODIFIED:
                                            list.set(dc.getOldIndex(), dc.getDocument().toObject(PostsModel.class));
                                            adapter.notifyDataSetChanged();
                                            break;
                                        case REMOVED:
                                            //to remove item
                                            list.remove(dc.getOldIndex());
                                            adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void postActivity()
    {
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    //call the dialog to write post content on
                    AddPostDialogFragment postDialogFrag = new AddPostDialogFragment();
                    postDialogFrag.show(getSupportFragmentManager().beginTransaction(), "Add menu");

                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void GoToProfile()
    {
        profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LoadingDialogFragment dialogFragment =  new LoadingDialogFragment("Loading...");
                dialogFragment.setCancelable(false);
                dialogFragment.show(getSupportFragmentManager().beginTransaction(), "PROFILE DIALOG");

                try
                {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                dialogFragment.dismiss();

                                ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
                                profileDialogFragment.show(getSupportFragmentManager().beginTransaction(), "profile");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logoutUser()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    }
}
