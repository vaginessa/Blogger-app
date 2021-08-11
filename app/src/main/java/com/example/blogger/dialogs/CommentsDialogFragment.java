package com.example.blogger.dialogs;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
        //loadComments(view);

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
        comments_tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog
                dismiss();
            }
        });
    }

    private void postComment(final View view)
    {

        context = view.getContext();

        Items = new ArrayList<>();

        comment_post_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_et.getText().toString().trim());
                commentsMap.put("user_id", firebaseAuth.getCurrentUser().getUid());
                commentsMap.put("timestamp", FieldValue.serverTimestamp());
                commentsMap.put("comment_id", comment_id);

                Toast.makeText(getActivity(), comment_id, Toast.LENGTH_LONG).show();

                //another try here
                FirebaseFirestore
                        .getInstance()
                        .collection("Posts/" + comment_id + "/Comments")
                        .add(commentsMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(final DocumentReference documentReference) {

                                Toast.makeText(getActivity(), "Comment successful!", Toast.LENGTH_LONG).show();
                                //clear the edit text after it is posted
                                comment_et.getText().clear();
                                documentReference.update("comment_id", documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }
    private void loadComments(View view)
    {
        context = view.getContext();
        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentsList);

        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        comment_list.setAdapter(commentAdapter);

        //.collection("Feeds/" + commentId + "/Comments")
        Query query = firebaseFirestore
                .collection("Posts/" + comment_id + "/Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        query.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots != null){
                    for (final DocumentChange dc : documentSnapshots.getDocumentChanges()){
                        switch (dc.getType()) {
                            case ADDED:
                                try
                                {
                                    CommentModel mc = dc.getDocument().toObject(CommentModel.class);
                                    commentsList.add(mc);
                                    commentAdapter.notifyDataSetChanged();

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    if(commentsList.removeIf(new Predicate<CommentModel>() {
                                        @Override
                                        public boolean test(CommentModel post) {
                                            return post.getComment_id().contains(dc.getDocument().getId());
                                        }
                                    }))
                                    {
                                        //adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                        }
                    }
                }else
                {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}