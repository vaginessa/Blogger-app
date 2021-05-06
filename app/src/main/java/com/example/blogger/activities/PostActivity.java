package com.example.blogger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.models.PostsModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private ImageView postImage;
    private EditText postDescr;
    private TextView postDate;

    private TextView postAuthor;
    private CircleImageView profileCircleView;

    private Button postButton;

    //firebase objects

    private Uri imageUri = null;

    //firebase
    private StorageReference imageStorage;
    private DatabaseReference reference, userRef;

    private ProgressDialog progress;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        FirebaseAuth auth =  FirebaseAuth.getInstance();

        imageStorage = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Blog");
        userRef = FirebaseDatabase.getInstance().getReference().child("Blog");

        postImage = (ImageView)findViewById(R.id.postImage) ;
        postDescr = (EditText)findViewById(R.id.postDescription);
        postDate = (TextView)findViewById(R.id.postDate);

        postAuthor = (TextView)findViewById(R.id.postUsername);
        profileCircleView = (CircleImageView)findViewById(R.id.postProfilePic);

        postButton = (Button)findViewById(R.id.newPostBtn);

        //progress = new ProgressDialog(getApplicationContext());

        selectImage();
        //startPosting();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting(imageUri);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //call method to display user pic and current time
        showUserDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
           /* progress.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {

                    try
                    {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        progress.dismiss();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            },3000);*/
    }

    //show time and user profile pic when posting
    private void showUserDetails()
    {
        String time = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        postDate.setText(time);

        String userid = FirebaseAuth.getInstance().getUid();

        userRef.child("Users").child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (currentUser != null)
                            {
                                String username = snapshot.child("name").getValue().toString();
                                String profile = snapshot.child("profile").getValue().toString();

                                postAuthor.setText(username);

                                Picasso.get()
                                        .load(profile)
                                        .into(profileCircleView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectImage()
    {
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            imageUri = data.getData();

            postImage.setImageURI(imageUri);
        }
    }

    private void startPosting(Uri uri)
    {
        final String description_val = postDescr.getText().toString().trim();
        //progressDialog
        /*progress.setMessage("posting...please wait");
        progress.setCancelable(false);
        progress.show();*/

        //final String randomName = UUID.randomUUID().toString();

        if (!TextUtils.isEmpty(description_val) && imageUri !=null)
        {
            final StorageReference filepath = imageStorage.child("Blog_images").child(System.currentTimeMillis()+"."+getFileExtention(uri));
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            try
                            {
                                String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                                //note the postImg object is not for the image only


                                PostsModel posts = new PostsModel();
                                posts.setUrl(uri.toString());
                                posts.setDesc(description_val);
                                posts.setTimeStamp(date);

                                //post id
                                String postId = reference.child("Posts").push().getKey();

                                //get user id
                                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                reference.child("Posts").push().setValue(posts).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(PostActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(PostActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }

        //start of the new posts

        //end of the new posts
    }

    private String getFileExtention(Uri mUri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
