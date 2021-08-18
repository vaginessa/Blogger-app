package com.example.blogger.dialogs;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogger.R;
import com.example.blogger.activities.MainActivity;
import com.example.blogger.activities.SigninActivity;
import com.example.blogger.activities.SignupActivity;
import com.example.blogger.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDialogFragment extends DialogFragment {

    private Context context;
    private MaterialToolbar toolbar;
    private TextInputEditText name_txt, surname_txt, email_txt;
    private MaterialTextView account_tv;
    private CircleImageView profileCircleView;
    private FloatingActionButton addImage_fab;
    private MaterialButton logout_btn, update_btn;

    private StorageReference storageReference;

    private static final int GALLERY_REQUEST = 1;

    private Uri reg_imageUri = null;

    public ProfileDialogFragment() {
        // Required empty public constructor
    }

    String title;

    public ProfileDialogFragment(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        /*getDialog()
                .getWindow()
                .setBackgroundDrawableResource(R.drawable.dialog_bg);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_profile_dialog, container, false);

        //call methods here
        init(view);
        myToolbar(view);
        dialogControls(view);
        getUserDetails(view);
        selectImage(view);

        return view;
    }

    private void init(ViewGroup view)
    {
        toolbar = view.findViewById(R.id.tool_bar);

        profileCircleView = view.findViewById(R.id.profileImage);

        addImage_fab = view.findViewById(R.id.addImg_fab);

        account_tv = view.findViewById(R.id.AccountDateTv);
        name_txt = view.findViewById(R.id.profile_name_et);
        surname_txt = view.findViewById(R.id.profile_surname_et);
        email_txt = view.findViewById(R.id.profile_email_et);

        logout_btn = view.findViewById(R.id.close_btn);
        update_btn = view.findViewById(R.id.update_btn);
    }

    private void myToolbar(ViewGroup view) {

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle(getString(R.string.profile_text));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    private void getUserDetails(ViewGroup view)
    {
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {

                    if (value.exists())
                    {
                        name_txt.setText(value.get("name").toString());
                        surname_txt.setText(value.get("surname").toString());
                        email_txt.setText(value.get("email").toString());

                        if (value.get("profile").toString() !=null) {
                            try {
                                Picasso
                                        .get()
                                        .load(value.get("profile").toString())
                                        .into(profileCircleView);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void dialogControls(ViewGroup view)
    {
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display the alert dialog
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()));
                builder.setTitle(getString(R.string.alert_title));
                builder.setMessage(getString(R.string.logout_message_text));
                builder.setPositiveButton(getString(R.string.position_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismiss();
                                    startActivity(new Intent(getActivity(), SigninActivity.class));
                                }
                            }, 3000);
                        } else {
                            Toast.makeText(getActivity(), "An error occurred while trying to logout", Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton(getString(R.string.negative_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel the dialog
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateUserAccount(reg_imageUri);
                //display this message
                Toast.makeText(getActivity(), getString(R.string.profile_successful_text), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectImage(View view)
    {
        addImage_fab.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,GALLERY_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null)
        {
            reg_imageUri = data.getData();
            profileCircleView.setImageURI(reg_imageUri);
        }
    }

    private void UpdateUserAccount(final Uri uri)
    {
        //final String description_val = postDescr.getText().toString().trim();
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Creating new user...please wait");
        progress.setCancelable(false);

        String name = name_txt.getText().toString().trim();
        String surname = surname_txt.getText().toString().trim();
        String email = email_txt.getText().toString().trim();

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
                            public void onSuccess(Uri uri)
                            {
                                try
                                {
                                    UserModel newuser = new UserModel();
                                    /*newuser.setName(name);
                                    newuser.setSurname(surname);
                                    newuser.setEmail(email);
                                    newuser.setProfile(uri.toString());*/

                                    Map<String, String> user = new HashMap<String, String>();
                                    user.put("profile", uri.toString());
                                    user.put("name", name);
                                    user.put("surname", surname);

                                    try
                                    {
                                        //reference.child("Users").child(uid).setValue(newuser);
                                        FirebaseFirestore
                                                .getInstance()
                                                .collection("Users")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .update("profile",uri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                    }catch(Exception e)
                                    {
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtention(Uri mUri)
    {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}