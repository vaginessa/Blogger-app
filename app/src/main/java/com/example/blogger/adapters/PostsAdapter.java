package com.example.blogger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogger.R;
import com.example.blogger.models.PostsModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<PostsModel> postslList;
    private Context context;

    public PostsAdapter(Context context,List<PostsModel> postslList) {
        this.postslList = postslList;
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

        PostsModel posts = postslList.get(position);
        holder.txtDescription.setText(posts.getDesc());
        holder.txtPostTime.setText(posts.getTimeStamp());

        Picasso.get()
                .load(posts.getImage_url())
                .centerCrop(50)
                .into(holder.postImage);
    }

    @Override
    public int getItemCount()
    {
        return postslList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtDescription;
        private ImageView postImage;
        private TextView txtPostTime;
        private TextView txtAuthor;
        private CircleImageView authorImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDescription=(TextView)itemView.findViewById(R.id.blogDescription);
            postImage = (ImageView)itemView.findViewById(R.id.blogImage);
            txtPostTime = (TextView)itemView.findViewById(R.id.blogDate);
            txtAuthor = (TextView)itemView.findViewById(R.id.commentUsername);
            authorImage = (CircleImageView)itemView.findViewById(R.id.commentProfilePic);
        }
    }
}
