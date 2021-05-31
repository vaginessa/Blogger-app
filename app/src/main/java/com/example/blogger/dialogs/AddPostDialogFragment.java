package com.example.blogger.dialogs;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.example.blogger.R;
import com.example.blogger.models.PostsModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class AddPostDialogFragment extends DialogFragment {

    private Uri img_uri = null;

    public AddPostDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_add_post, container, false);
        ConnectView(view);
        return view;
    }
    ImageView img_post;
    AppCompatImageView remove_post_img;

    private void ConnectView(final View view) {

        MaterialButton btn_post_feed = view.findViewById(R.id.btn_post_feed);
        img_post = view.findViewById(R.id.img_post);
        remove_post_img = view.findViewById(R.id.remove_post_img);
        AppCompatImageView chose_post_img = view.findViewById(R.id.chose_post_img);
        MaterialToolbar toolbar =  view.findViewById(R.id.tool_bar_add_post);
        final TextInputEditText input_message = view.findViewById(R.id.input_post_message);

        remove_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_post.setVisibility(View.GONE);
                img_uri = null;
                remove_post_img.setVisibility(View.GONE);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        chose_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(AddPostDialogFragment.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(512, 512)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });



        btn_post_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(Objects.requireNonNull(input_message.getText()).toString().isEmpty())
                {
                    input_message.setError("Type something");
                    return;
                }
                //String currentDate = new SimpleDateFormat("ddd/MMM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                //PostsModel posts = new PostsModel(null, null, null,null,input_message.getText().toString());

                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists())
                                {
                                    //get user's details
                                    String author = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                    String profile = Objects.requireNonNull(snapshot.child("profile").getValue()).toString();
                                    String surname = Objects.requireNonNull(snapshot.child("surname").getValue()).toString();

                                    String fullNames = author+" "+surname;
                                    Toast.makeText(v.getContext(), "author details are:"+author, Toast.LENGTH_LONG).show();
                                    //post feed
                                    PostsModel postsModel = new PostsModel();
                                    postsModel.setAuthor(fullNames);
                                    postsModel.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    postsModel.setProfile_pic(profile);
                                    postsModel.setTimeStamp(FieldValue.serverTimestamp().toString());
                                    postsModel.setDesc(input_message.getText().toString());

                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("Feeds")
                                            .add(postsModel)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(final DocumentReference documentReference) {

                                                    documentReference.update("key",documentReference.getId());

                                                    if (img_uri != null)
                                                    {
                                                        FirebaseStorage
                                                                .getInstance()
                                                                .getReference()
                                                                .child("Feeds")
                                                                .child(documentReference.getId())
                                                                .putFile(img_uri)
                                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                        taskSnapshot
                                                                                .getStorage()
                                                                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {

                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection("Feeds")
                                                                                        .document(documentReference.getId())
                                                                                        .update("url", uri.toString());

                                                                                Toast.makeText(v.getContext(), "successful", Toast.LENGTH_LONG).show();
                                                                                dismiss();
                                                                            }
                                                                        });
                                                                    }
                                                                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                SweetAlertDialog pDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                                pDialog.setTitleText("Success!");
                                                                pDialog.setContentText("Post successfully added!");
                                                                pDialog.setConfirmText("Ok");
                                                                pDialog.show();
                                                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        dismiss();
                                                                        sweetAlertDialog.dismissWithAnimation();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == RESULT_OK){

            img_uri = data.getData();
            img_post.setImageURI(img_uri);
            img_post.setVisibility(View.VISIBLE);
            remove_post_img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}