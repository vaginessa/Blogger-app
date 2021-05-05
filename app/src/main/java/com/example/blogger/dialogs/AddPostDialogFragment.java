package com.example.blogger.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blogger.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class AddPostDialogFragment extends DialogFragment {


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
    AppCompatImageView img_post;
    AppCompatImageView remove_post_img;

    private void ConnectView(View view) {

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
            public void onClick(View v) {

                if(input_message.getText().toString().isEmpty()){
                    input_message.setError("Type something");
                    return;
                }
                String currentDate = new SimpleDateFormat("ddd/MMM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                //PostsModel posts = new PostsModel(null, null, null,null,input_message.getText().toString());

            }
        });


    }

    Uri img_uri;//= new Uri();

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