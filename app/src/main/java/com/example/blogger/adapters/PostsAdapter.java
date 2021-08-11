package com.example.blogger.adapters;

import android.annotation.SuppressLint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogger.R;

import com.example.blogger.dialogs.CommentsDialogFragment;
import com.example.blogger.models.PostsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import interfaces.PostClickListener;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final List<PostsModel> postList;
    private final Context context;
    private FragmentManager fm;
    private final ClickListener listener1;

    public PostsAdapter(Context context, List<PostsModel> postsList, ClickListener listener1) {
        this.postList = postsList;
        this.context = context;
        this.listener1 = listener1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.blog_item_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        PostsModel posts = postList.get(position);

        holder.txtDescription.setText(posts.getDesc());
        holder.txtPostTime.setText(posts.getTimeStamp());
        holder.txtAuthor.setText(posts.getAuthor());

        Glide.with(context)
                .load(posts.getProfile_pic())
                .into(holder.authorImage);

        RequestOptions placeholderOption = new RequestOptions();

        if (posts.getUrl() != null)
        {
            Glide.with(context).applyDefaultRequestOptions(placeholderOption)
                    .load(posts.getUrl())
                    .into(holder.postImage);
        }else
        {
            placeholderOption.placeholder(R.drawable.ic_account_circle_black_24dp);
        }

        //check number of like for each post
        FirebaseFirestore
                .getInstance()
                .collection("Posts/"+posts.getId()+"/Likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                                holder.txtCountLikes.setText(String.valueOf(count));
                            }
                        } else {
                            holder.txtCountLikes.setText("0");
                        }
                    }
                });
    }

    @Override
    public int getItemCount()
    {
        return postList.size();
    }

    View mView;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final MaterialTextView txtDescription;
        private final ImageView postImage;
        private final MaterialTextView txtPostTime;
        private final MaterialTextView txtAuthor;
        private final CircleImageView authorImage;
        private final MaterialTextView txtCountLikes;

        private final ImageView comments_iv;
        private final ImageView blog_like_iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            txtDescription= itemView.findViewById(R.id.blogDescription);
            postImage = itemView.findViewById(R.id.postImage);
            txtPostTime = itemView.findViewById(R.id.postDate);
            txtAuthor = itemView.findViewById(R.id.postUsername);
            authorImage = itemView.findViewById(R.id.postProfilePic);
            txtCountLikes = itemView.findViewById(R.id.blog_like_count);

            blog_like_iv = itemView.findViewById(R.id.blog_like);
            comments_iv = itemView.findViewById(R.id.blog_comment);

            comments_iv.setOnClickListener(this);
            blog_like_iv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos =  getAdapterPosition();
            if (v.getId() == comments_iv.getId()) {
                    listener1.clickedComments(pos);
            }
            if (v.getId() == blog_like_iv.getId())
            {
                PostsModel model = postList.get(pos);

                likeListener(model.getId(),blog_like_iv); //like post
                //get number of likes
            }
        }
    }

    private void countLike(String postId, ViewHolder holder)
    {
        //check number of like for each post
        FirebaseFirestore
                .getInstance()
                .collection("Posts/"+postId+"/Likes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String likeCount = String.valueOf(queryDocumentSnapshots.getDocuments().size());
                        //Toast.makeText(mView.getContext(),"No of like is "+likeCount,Toast.LENGTH_LONG).show();
                        holder.txtCountLikes.setText(likeCount);
                    }
                });
    }

    private void likeListener(String postId, ImageView imageView)
    {
        FirebaseFirestore
                .getInstance()
                .collection("Posts/" + postId + "/Likes")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("time", FieldValue.serverTimestamp());

                            FirebaseFirestore
                                    .getInstance()
                                    .collection("Posts/" + postId + "/Likes").document(FirebaseAuth.getInstance().getUid())
                                    .set(likesMap);

                            Toast.makeText(mView.getContext(), "liked", Toast.LENGTH_LONG).show();
                            imageView.setImageResource(R.drawable.ic_like_btn);
                            imageView.setTag("Liked");
                        } else {
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("Posts/" + postId + "/Likes").document(FirebaseAuth.getInstance().getUid())
                                    .delete();

                            Toast.makeText(mView.getContext(), "Unliked", Toast.LENGTH_LONG).show();
                            imageView.setImageResource(R.drawable.action_like_btn_gray);
                            imageView.setTag("");
                        }
                    }
                });
    }

    public interface ClickListener
    {
        //void clickedLikes(int pos);
        void clickedComments(int pos);
    }
}
