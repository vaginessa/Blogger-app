package com.example.blogger.adapters;

import android.annotation.SuppressLint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.blogger.R;

import com.example.blogger.dialogs.CommentsDialogFragment;
import com.example.blogger.models.PostsModel;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import interfaces.PostClickListener;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> implements PostClickListener {

    PostClickListener listener;
    private final List<PostsModel> postList;
    private final Context context;
    private FragmentManager fm;
    //private final CommentClickListener commentClickListener;

    public PostsAdapter(Context context,List<PostsModel> postsList) {
        this.postList = postsList;
        this.context = context;
        //fm = fragmentTransaction;
        //this.commentClickListener = commentClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item_row, parent, false);
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

       /*Picasso.get()
               .load(posts.getProfile_pic())
                .centerCrop()
                .into(holder.authorImage);*/

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

    }

    @Override
    public int getItemCount()
    {
        return postList.size();
    }

    View mView;

    @Override
    public void clickedLikes() {
        CommentsDialogFragment dlg = new CommentsDialogFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    dlg.show(ft, "Comments");
    }

    @Override
    public void clickedComments() {

        CommentsDialogFragment dlg = new CommentsDialogFragment();
        FragmentTransaction ft = fm.beginTransaction();
        dlg.show(ft, "Comments");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final MaterialTextView txtDescription;
        private final ImageView postImage;
        private final MaterialTextView txtPostTime;
        private final MaterialTextView txtAuthor;
        private final CircleImageView authorImage;

        private final ImageView comments_iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            txtDescription= itemView.findViewById(R.id.blogDescription);
            postImage = itemView.findViewById(R.id.postImage);
            txtPostTime = itemView.findViewById(R.id.postDate);
            txtAuthor = itemView.findViewById(R.id.postUsername);
            authorImage = itemView.findViewById(R.id.postProfilePic);

            comments_iv = itemView.findViewById(R.id.blog_comment);

            comments_iv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == comments_iv.getId()) {

                int pos =  getAdapterPosition();

                try
                {
                    //call the dialog to write post content on
                    Toast.makeText(v.getContext(), String.valueOf(pos), Toast.LENGTH_LONG).show();

                    /*CommentsDialogFragment dlg = new CommentsDialogFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    dlg.show(ft, "Comments");*/
                listener.clickedLikes();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
