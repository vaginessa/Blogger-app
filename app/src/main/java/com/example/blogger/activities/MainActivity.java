package com.example.blogger.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.adapters.PostsAdapter;
import com.example.blogger.dialogs.AddPostDialogFragment;
import com.example.blogger.dialogs.NewPostDialogFrag;
import com.example.blogger.models.PostsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private android.widget.Toolbar toolbar;
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

        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        list = new ArrayList<>();


        //call methods
        initAppbarComponents();
        postActivity();
        GoToProfile();
        //logoutUser();
        initToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        toolbar = findViewById(R.id.my_toolbar);

    }

    private void initToolbar()
    {
        final ProgressDialog logoutProgress = new ProgressDialog(MainActivity.this);
        logoutProgress.setMessage("Logging out... Please wait");

        user = FirebaseAuth.getInstance().getCurrentUser();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null)
                {
                    //show dialog for logout
                    logoutProgress.show();

                    FirebaseAuth.getInstance().signOut();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                            finish();
                            //close dialog if user chooses yes
                            logoutProgress.dismiss();
                        }
                    },3000);

                }
            }
        });
    }

    private void getAllPosts()
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading... Please wait");
        dialog.setCancelable(false);
        dialog.show();

        try
        {
            for(int i=0; i<25; i++)
            {
                    PostsModel mdl = new PostsModel();
                    mdl.setAuthor("whiz");
                    mdl.setDesc("post descr");
                    //mdl.setImage_url("https://picsum.photos/id/"+i+"/200/300");
                    mdl.setImage_url(null);
                    mdl.setUser_id(FirebaseAuth.getInstance().getUid());
                    int pos = 1;
                    mdl.setImage_thumb(null);
                    mdl.setTimeStamp("07/April/2021");
                    list.add(mdl);
            }

            adapter = new PostsAdapter(context ,list);
            recyclerView.setAdapter(adapter);
            //close dialog
            dialog.dismiss();

            //firebase instatiation
            /*reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            PostsModel l= dataSnapshot.getValue(PostsModel.class);
                            list.add(l);
                        }

                        adapter = new PostsAdapter(context ,list);
                        recyclerView.setAdapter(adapter);
                    }

                    dialog.dismiss();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    dialog.dismiss();

                    Toast.makeText(getApplicationContext(),""+error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });*/

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

                try {
                    startActivity(new Intent(getApplicationContext() ,ProfileActivity.class));
                    finish();
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
