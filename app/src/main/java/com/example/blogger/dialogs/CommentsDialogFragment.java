package com.example.blogger.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blogger.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;


public class CommentsDialogFragment extends DialogFragment {

    //dialog variables
    private MaterialToolbar comments_tool_bar;
    private RecyclerView comments_rv;

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


        return view;
    }

    private void init(View view)
    {
        comments_tool_bar = view.findViewById(R.id.comments_tool_bar);
        //comments_tool_bar = view.findViewById(R.id.comments_rv);
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
}