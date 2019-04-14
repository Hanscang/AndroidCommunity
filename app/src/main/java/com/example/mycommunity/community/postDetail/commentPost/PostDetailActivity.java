package com.example.mycommunity.community.postDetail.commentPost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.mycommunity.R;
import com.example.mycommunity.community.CommunityPost;

import java.text.DateFormat;
import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

    private CommunityPost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        post = (CommunityPost) intent.getSerializableExtra("postDetail");
        if (post != null) {

            Button follow = findViewById(R.id.post_detail_follow);
            TextView userId = findViewById(R.id.post_detail_user_name);
            TextView date = findViewById(R.id.post_detail_pub_date);
            TextView postDetailText = findViewById(R.id.post_detail_newsDetail);
            RecyclerView postImg = findViewById(R.id.post_detail_img_container);
            RecyclerView comments = findViewById(R.id.post_detail_user_comments_container);

            userId.setText(String.valueOf(post.getUserId()));
            date.setText(
                    DateFormat.getDateInstance(2).format(new Date(post.getPosted()))
            );
            postDetailText.setText(post.getNewsDetail());
        }
    }

    private void netRequest(){

    }
}