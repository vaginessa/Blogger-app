package com.example.blogger.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogger.R;
import com.example.blogger.adapters.CommentAdapter;
import com.example.blogger.models.CommentModel;
import com.example.blogger.models.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;


public class CommentsDialogFragment extends DialogFragment {

    //dialog variables
    private MaterialToolbar comments_tool_bar;

    private EditText comment_et;
    private FloatingActionButton comment_post_fab;

    private RecyclerView comment_list;
    private List<CommentModel> commentsList;
    //private CommentAdapter commentAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String blog_post_id;
    private String current_userId;

    private ArrayList<PostsModel> Items;
    private CommentAdapter commentAdapter;

    Context context;

    public CommentsDialogFragment() {
        // Required empty public constructor
    }

    String comment_id;
    public CommentsDialogFragment(String comment_id) {
        this.comment_id = comment_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize firebase objects
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //set dialog width and height
        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments_dialog, container, false);
        //call methods here
        init(view);
        setUpToolBar(view);
        postComment(view);
        loadComments(view);

        return view;
    }

    private void init(View view)
    {
        comments_tool_bar = view.findViewById(R.id.comments_tool_bar);
        comment_et = view.findViewById(R.id.InputComment);
        comment_post_fab = view.findViewById(R.id.BtnSendComment);
        comment_list = view.findViewById(R.id.comments_rv);
    }

    private void setUpToolBar(View view)
    {
        comments_tool_bar.setNavigationOnClickListener(v -> {
            //dismiss dialog
            dismiss();
            view.getContext();
        });
    }

    private void postComment(final View view)
    {
        context = view.getContext();

        Items = new ArrayList<>();

        comment_post_fab.setOnClickListener(v -> {

            final Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("message", comment_et.getText().toString().trim());
            commentsMap.put("user_id", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
            commentsMap.put("timestamp", FieldValue.serverTimestamp());
            commentsMap.put("comment_id", comment_id);

            Toast.makeText(getActivity(), comment_id, Toast.LENGTH_LONG).show();

            //another try here
            FirebaseFirestore
                    .getInstance()
                    .collection("Posts/" + comment_id + "/Comments")
                    .add(commentsMap)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(getActivity(), "Comment successful!", Toast.LENGTH_LONG).show();
                        //clear the edit text after it is posted
                        comment_et.getText().clear();
                        documentReference.update("comment_id", documentReference.getId());
                    }).addOnFailureListener(e ->
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show());
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadComments(View view)
    {
        RecyclerView recycler = view.findViewById(R.id.comments_rv);
        final ArrayList<CommentModel> Items = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final CommentAdapter adapter = new CommentAdapter(Items);
        recycler.setAdapter(adapter);

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("Posts/"+comment_id+"/Comments")
                    .addSnapshotListener((value, error) -> {
                        if(value != null){
                            for (final DocumentChange dc : value.getDocumentChanges()){
                                switch (dc.getType()) {
                                    case ADDED:
                                        Items.add(dc.getDocument().toObject(CommentModel.class));
                                        adapter.notifyDataSetChanged();
                                        break;
                                    case MODIFIED:
                                        Items.set(dc.getOldIndex(), dc.getDocument().toObject(CommentModel.class));
                                        adapter.notifyDataSetChanged();
                                        break;
                                    case REMOVED:
                                        //to remove item
                                        Items.remove(dc.getOldIndex());
                                        adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });

        }
        catch (Exception ex){
            Toast.makeText(view.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}