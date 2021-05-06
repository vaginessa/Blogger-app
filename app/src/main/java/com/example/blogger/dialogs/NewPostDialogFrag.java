package com.example.blogger.dialogs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.blogger.R;
import com.example.blogger.activities.MainActivity;
import com.example.blogger.activities.PostActivity;
import com.example.blogger.models.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class NewPostDialogFrag extends DialogFragment {

    private MaterialToolbar tool_bar;

    private ImageView postImage;
    private TextInputEditText postDescr;
    private TextView postDate;

    private MaterialTextView postAuthor;
    private CircleImageView profileCircleView;

    private MaterialButton btnSubmitPost;

    private Uri imageUri = null;

    //firebase
    private StorageReference imageStorage;
    private DatabaseReference reference, userRef;


    private static final int GALLERY_REQUEST = 1;

    public NewPostDialogFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        //set dialog width and heght
        getDialog()
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_post, container, false);

        //call methods here
        init(view);
        onSubmitPost(view);
        //selectImage(view);

        return view;
    }

    private void init(View view)
    {

        postImage = view.findViewById(R.id.postImage) ;
        postDescr = view.findViewById(R.id.postDescription);
        postDate = view.findViewById(R.id.postDate);

        postAuthor = view.findViewById(R.id.postUsername);
        profileCircleView = view.findViewById(R.id.postProfilePic);

        btnSubmitPost = view.findViewById(R.id.newPostBtn);
    }

    private void onSubmitPost(View view)
    {
        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startPosting(imageUri,v);

                Toast.makeText(v.getContext(), "It works bruh!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    private void selectImage(View view)
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data !=null)
        {
            imageUri = data.getData();

            postImage.setImageURI(imageUri);
        }
    }

    private void startPosting(Uri uri, final View view)
    {
        imageStorage = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Blog");
        userRef = FirebaseDatabase.getInstance().getReference().child("Blog");

        final String description_val = postDescr.getText().toString().trim();

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
                                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
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

                                        //startActivity(new Intent(view.getContext(), MainActivity.class));
                                        dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(view.getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(view.getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private String getFileExtention(Uri mUri)
    {
        Context context = getView().getContext();

        ContentResolver cr = context.getContentResolver();

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}