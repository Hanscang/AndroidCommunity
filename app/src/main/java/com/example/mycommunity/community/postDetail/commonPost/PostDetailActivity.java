package com.example.mycommunity.community.postDetail.commonPost;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mycommunity.NetworkModule;
import com.example.mycommunity.R;
import com.example.mycommunity.UserNotice;
import com.example.mycommunity.community.CommunityPost;
import com.example.mycommunity.community.postDetail.Comment;
import com.example.mycommunity.community.postDetail.CommentsAdapter;
import com.example.mycommunity.community.star.Star;
import com.example.mycommunity.userInf.UserInfService;
import com.example.mycommunity.userInf.UserInformation;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    private CommunityPost post;
    private RecyclerView commentsRecycleView;
    private UserInformation userInformation = null;
    private EditText postCommentContent;
    private LinearLayout postCommentComponent;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            List<Comment> comments = null;
            ReturnPostMsg returnPostMsg = null;
            try {
                returnPostMsg = new Gson().fromJson((String) msg.obj, ReturnPostMsg.class);
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
                    netRequest(post.getIdx());
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

            netRequest(post.getIdx());
            final Button follow = findViewById(R.id.post_detail_follow);
            CircleImageView userIc = findViewById(R.id.post_detail_user_ic);
            TextView userId = findViewById(R.id.post_detail_user_name);
            TextView date = findViewById(R.id.post_detail_pub_date);
            TextView postDetailText = findViewById(R.id.post_detail_newsDetail);
            RecyclerView postImg = findViewById(R.id.post_detail_img_container);
            LinearLayout heartComponent = findViewById(R.id.interaction_heart_component);
            LinearLayout commentsComponent = findViewById(R.id.interaction_comments_component);
            final ImageView heart = findViewById(R.id.community_icon_heart);
            final TextView heartCount = findViewById(R.id.community_heart_count_text_view);
            TextView commentsCount = findViewById(R.id.community_comments_count_text_View);
            UserInfService userInfService = new UserInfService(this);
            userInfService.getUserInfById(post.getUserId(), userIc, userId, userInformation);
            commentsRecycleView = findViewById(R.id.post_detail_user_comments_container);
            postCommentComponent = findViewById(R.id.post_detail_post_comment_component);
            postCommentContent = findViewById(R.id.post_detail_comment_content);
            final TextView postComment = findViewById(R.id.post_detail_post_comment);
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow.setVisibility(View.INVISIBLE);
                    if (userInformation != null) {
                        new NetworkModule().post("/news/follow/poster/" + userInformation.getNickname(),
                                "",
                                new Star(PostDetailActivity.this, userInformation).getFollowHandler(),
                                PostDetailActivity.this
                        );
                    }
                }
            });
            heartComponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NetworkModule().post("/news/collect/" + post.getIdx(),
                            "",
                            new Star(PostDetailActivity.this, heart, heartCount, post).getStarHandler(),
                            PostDetailActivity.this
                    );
                }
            });
            heartCount.setText(String.valueOf(post.getStar()));
            commentsCount.setText(String.valueOf(post.getComments()));
            commentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
            userId.setText(String.valueOf(post.getUserId()));
            date.setText(
                    DateFormat.getDateInstance(2).format(new Date(post.getPosted()))
            );
            commentsComponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postCommentComponent.setVisibility(View.VISIBLE);
                    postCommentContent.setFocusable(true);
                    postCommentContent.setFocusableInTouchMode(true);
                    postCommentContent.requestFocus();
                    showInput(postCommentContent);
                }
            });
            postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideInput();
                    Comment comment = new Comment();
                    comment.setContent(postCommentContent.getText().toString());
                    comment.setPostId(post.getIdx());
                    new NetworkModule().post(
                            "/news/reply/create",
                            new Gson().toJson(comment),
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message msg) {
                                    postCommentComponent.setVisibility(View.GONE);
                                    switch (msg.what) {
                                        case 0:
                                            netRequest(post.getIdx());
                                            break;
                                        case 1:
                                            UserNotice.showToast(PostDetailActivity.this, UserNotice.NETWORK_CONNECT_FAILURE);
                                            break;
                                        case 2:
                                            UserNotice.showToast(PostDetailActivity.this, UserNotice.USER_AUTHENTICATION_FAILURE);
                                            break;
                                        case 3:
                                            netRequest(post.getIdx());
                                            break;
                                        case 4:
                                            UserNotice.showToast(PostDetailActivity.this, "请输入评论");
                                            break;
                                        default:
                                            UserNotice.showToast(PostDetailActivity.this, UserNotice.UNEXPECTED_STATE);
                                    }
                                    return false;
                                }
                            }),
                            PostDetailActivity.this);
                }
            });
            postDetailText.setText(post.getDescription());
        }
    }

    private void netRequest(int id) {
        new NetworkModule().get("/news/detail/" + id, handler, PostDetailActivity.this);
    }

    public void showInput(EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
