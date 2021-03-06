package com.example.mycommunity.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mycommunity.NetworkModule;
import com.example.mycommunity.R;
import com.example.mycommunity.UserNotice;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommunityListActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Gson gson = new Gson();
    private RecyclerView recyclerView;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            progressBar.setVisibility(View.INVISIBLE);
            if (msg.what == 0) {
                ReturnMsg returnMsg = (ReturnMsg) msg.obj;
                switch (returnMsg.getStatus()) {
                    case 10001:
                        List<Data> data = returnMsg.getData();
                        CommunityListAdapter adapter = new CommunityListAdapter(data);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityListActivity.this));
                        recyclerView.setAdapter(adapter);
                        break;
                    default:
                        UserNotice.showToast(CommunityListActivity.this, returnMsg.getMessage());
                }
            }
            else {
                UserNotice.showToast(CommunityListActivity.this, UserNotice.UNEXPECTED_STATE);
            }
            return false;
        }
    });
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Message message = handler.obtainMessage(1);
            handler.sendMessage(message);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            ReturnMsg returnMsg = gson.fromJson(response.body().string(), ReturnMsg.class);
            Message message = handler.obtainMessage(0, returnMsg);
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);
        String url = "/community/listall";
        progressBar = findViewById(R.id.community_list_progress_bar);
        recyclerView = findViewById(R.id.community_list_recycler_view);
        new NetworkModule().get(url, callback);

    }
}
