package com.example.blogger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.models.PostsModel;
import com.example.blogger.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout name_txt, surname_txt,email_txt,password_txt, confirmpassword_txt;
    private MaterialTextView backToLogin_tv;
    private ImageView selectedImage;
    private MaterialButton signup_button;

    //firebase objects
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReference;

    private static final int GALLERY_REQUEST = 1;

    private Uri reg_imageUri = null;

    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        reference = FirebaseDatabase.getInstance().getReference("Blog");
        storageReference = FirebaseStorage.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        //call methods
        signuplinks();
        animateComponents();
        selectImage();
        backToLoginPage();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUserAccount(reg_imageUri);
            }
        });
    }

    private void signuplinks()
    {
        name_txt = (TextInputLayout)findViewById(R.id.reg_name);
        surname_txt = (TextInputLayout)findViewById(R.id.reg_surname);
        email_txt = (TextInputLayout)findViewById(R.id.reg_email);
        password_txt = (TextInputLayout)findViewById(R.id.reg_password);
        confirmpassword_txt = (TextInputLayout)findViewById(R.id.reg_confirmpassword);
        //already an account textview
        backToLogin_tv = findViewById(R.id.link_login);
        //profile imageview
        selectedImage = (CircleImageView)findViewById(R.id.reg_profile_image);
        //button
        signup_button = (MaterialButton) findViewById(R.id.btn_signup);
    }

    private void animateComponents()
    {
        //float for X and Y axis
        float x = 300;
        int duration = 1000;
        int delay = 2000;

        //edittexts horizontal
        name_txt.setTranslationX(x);
        surname_txt.setTranslationX(x);
        email_txt.setTranslationX(x);
        password_txt.setTranslationX(x);
        confirmpassword_txt.setTranslationX(x);

        //edittexts opacity
        name_txt.setAlpha(v);
        surname_txt.setAlpha(v);
        email_txt.setAlpha(v);
        password_txt.setAlpha(v);
        confirmpassword_txt.setAlpha(v);

        //button horizontal shift
        signup_button.setTranslationX(x);
        signup_button.setAlpha(v);
        //textview
        backToLogin_tv.setTranslationX(x);
        backToLogin_tv.setAlpha(v);

        //get the normal width and height for all the components
        name_txt.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 100).start();
        surname_txt.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 200).start();
        email_txt.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 300).start();
        password_txt.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 400).start();
        confirmpassword_txt.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 500).start();

        signup_button.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 600).start();
        backToLogin_tv.animate().translationX(0).alpha(1).setDuration(duration).setStartDelay(delay + 700).start();

    }

    private void CreateUserAccountFirst()
    {
        String email_str = email_txt.getEditText().getText().toString().trim();
        String password_str = password_txt.getEditText().getText().toString().trim();

        auth.createUserWithEmailAndPassword(email_str, password_str).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String email = email_txt.getEditText().getText().toString().trim();
                        String name = name_txt.getEditText().getText().toString().trim();
                        String surname = surname_txt.getEditText().getText().toString().trim();

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        //start uplaoding image

                        //end of image upload task

                        UserModel newuser = new UserModel();

                        newuser.setName(name);
                        newuser.setSurname(surname);
                        newuser.setEmail(email);

                        try
                        {
                            reference.child("Users").child(uid).setValue(newuser);

                        }catch(Exception e)
                        {
                            Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),""+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void backToLoginPage()
    {
        backToLogin_tv.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),SigninActivity.class));
        });
    }

    private void selectImage()
    {
        selectedImage.setOnClickListener(new View.OnClickListener() {
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

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null)
        {
            reg_imageUri = data.getData();
            selectedImage.setImageURI(reg_imageUri);
        }
    }

    private void CreateUserAccount(final Uri uri)
    {
        String email_str = email_txt.getEditText().getText().toString().trim();
        String password_str = password_txt.getEditText().getText().toString().trim();

        auth.createUserWithEmailAndPassword(email_str, password_str)
                .addOnSuccessListener(authResult -> {
                    final String name = name_txt.getEditText().getText().toString().trim();
                    final String surname = surname_txt.getEditText().getText().toString().trim();

                    if (!(TextUtils.isEmpty(email_str) && TextUtils.isEmpty(name) && TextUtils.isEmpty(surname) && reg_imageUri !=null))
                    {
                        final StorageReference filepath = storageReference
                                .child("Profile_images")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(System.currentTimeMillis()+"."+getFileExtention(uri));

                        filepath.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {

                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri uri1)
                                    {
                                        try
                                        {
                                            user = FirebaseAuth.getInstance().getCurrentUser();
                                            String uid = user.getUid();

                                            UserModel newuser = new UserModel();

                                            newuser.setName(name);
                                            newuser.setSurname(surname);
                                            newuser.setEmail(email_str);
                                            newuser.setProfile(uri1.toString());

                                            try
                                            {
                                                //reference.child("Users").child(uid).setValue(newuser);
                                                FirebaseFirestore
                                                        .getInstance()
                                                        .collection("Users")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .set(newuser)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(SignupActivity.this, "New user created Successfully", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }catch(Exception e)
                                            {
                                                Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(SignupActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try
        {
            startActivity(new Intent(getApplicationContext(),SigninActivity.class));
            finish();

        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtention(Uri mUri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
