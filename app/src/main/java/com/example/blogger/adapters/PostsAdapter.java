package com.example.blogger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogger.R;
import com.example.blogger.dialogs.AddPostDialogFragment;
import com.example.blogger.dialogs.CommentsDialogFragment;
import com.example.blogger.models.PostsModel;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final List<PostsModel> postList;
    private final Context context;

    public PostsAdapter(Context context,List<PostsModel> postsList) {
        this.postList = postsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        PostsModel posts = postList.get(position);

        holder.txtDescription.setText(posts.getDesc());
        holder.txtPostTime.setText(posts.getTimeStamp());
        holder.txtAuthor.setText(posts.getAuthor());

        Picasso.get()
                .load(posts.getUrl())
                .centerCrop(50)
                .into(holder.postImage);
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

        private ImageView comments_iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            txtDescription=(MaterialTextView)itemView.findViewById(R.id.blogDescription);
            postImage = (ImageView)itemView.findViewById(R.id.postImage);
            txtPostTime = (MaterialTextView)itemView.findViewById(R.id.postDate);
            txtAuthor = (MaterialTextView)itemView.findViewById(R.id.postUsername);
            authorImage = (CircleImageView)itemView.findViewById(R.id.postProfilePic);

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

                    /*CommentsDialogFragment commentsDialogFragment = new CommentsDialogFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    commentsDialogFragment.showNow(v.getContext().get, "comments");*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
