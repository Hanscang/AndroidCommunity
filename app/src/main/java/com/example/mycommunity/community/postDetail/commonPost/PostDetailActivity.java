package com.example.mycommunity.community.postDetail.commonPost;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mycommunity.NetworkModule;
import com.example.mycommunity.R;
import com.example.mycommunity.UserNotice;
import com.example.mycommunity.community.CommunityPost;
import com.example.mycommunity.community.newPost.NewPostActivity;
import com.example.mycommunity.community.postDetail.Comment;
import com.example.mycommunity.community.postDetail.CommentsAdapter;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private CommunityPost post;
    private RecyclerView commentsRecycleView;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            List<Comment> comments = null;
            ReturnPostMsg returnPostMsg = null;
            try {
                returnPostMsg = new Gson().fromJson((String) msg.obj, ReturnPostMsg.class);
                post = returnPostMsg.getData().getPost_news();
                comments = returnPostMsg.getData().getPost_replys();
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (msg.what) {
                case 0:
                    commentsRecycleView.setAdapter(new CommentsAdapter(comments));
                    break;
                case 1:
                    UserNotice.showToast(PostDetailActivity.this, UserNotice.NETWORK_CONNECT_FAILURE);
                    break;
                case 2:
                    UserNotice.showToast(PostDetailActivity.this, UserNotice.USER_AUTHENTICATION_FAILURE);
                    break;
                case 3:
                    netRequest(post.getId());
                    break;
                case 4:
                    if (returnPostMsg != null)
                        UserNotice.showToast(PostDetailActivity.this, returnPostMsg.getMessage());
                    break;
                default:
                    UserNotice.showToast(PostDetailActivity.this, UserNotice.UNEXPECTED_STATE);

            }
            return false;
        }
    });

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
            netRequest(post.getId());
            Button follow = findViewById(R.id.post_detail_follow);
            TextView userId = findViewById(R.id.post_detail_user_name);
            TextView date = findViewById(R.id.post_detail_pub_date);
            TextView postDetailText = findViewById(R.id.post_detail_newsDetail);
            RecyclerView postImg = findViewById(R.id.post_detail_img_container);
            LinearLayout heartComponent = findViewById(R.id.interaction_heart_component);
            LinearLayout commentsComponent = findViewById(R.id.interaction_comments_component);
            TextView heartCount = findViewById(R.id.community_heart_count_text_view);
            TextView commentsCount = findViewById(R.id.community_comments_count_text_View);
            commentsRecycleView = findViewById(R.id.post_detail_user_comments_container);
            commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
            userId.setText(String.valueOf(post.getUserId()));
            date.setText(
                    DateFormat.getDateInstance(2).format(new Date(post.getPosted()))
            );
            postDetailText.setText(post.getDescription());
            // heartComponent.
        }
    }

    private void netRequest(int id) {
        new NetworkModule().get("/news/detail/" + id, handler, PostDetailActivity.this);
    }
}
