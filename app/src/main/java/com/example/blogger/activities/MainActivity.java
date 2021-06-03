package com.example.blogger.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.content.Context;
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
    /*---firebase assignment--*/
    private DatabaseReference reference;
    //private DatabaseReference getPostsReference;

    private RecyclerView recyclerView;
    List<PostsModel> list;
    PostsAdapter adapter;
    Context context;
    FragmentManager fm = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reference = FirebaseDatabase.getInstance().getReference().child("Blog");

        new_post = findViewById(R.id.fab_new_post);
        //logout_button = (FloatingActionButton)findViewById(R.id.fab_logout);
        recyclerView = findViewById(R.id.posts_rv);
        recyclerView.setHasFixedSize(true);

        /*recyclerView.setLayoutManager( new LinearLayoutManager(this));

        list = new ArrayList<>();*/


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
        final LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        //Objects.requireNonNull(loadingDialogFragment.getDialog()).getWindow().setGravity(Gravity.CENTER);
        //loadingDialogFragment.show(getSupportFragmentManager().beginTransaction(), "Loading");

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try
        {
            String currentDate = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault()).format(new Date());

            for (int i = 0 ; i < 20 ; i++)
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
            recyclerView.setAdapter(adapter);


           /* FirebaseFirestore
                    .getInstance()
                    .collection("Feeds")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                                {
                                    PostsModel posts = snapshot.toObject(PostsModel.class);
                                    list.add(posts);
                                }
                                adapter = new PostsAdapter(getApplicationContext(), list);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });*/


            String time = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

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
