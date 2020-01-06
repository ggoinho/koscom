package kr.co.koscom.omp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupChannelActivity extends AppCompatActivity {

    private Button mNextButton, mCreateButton, mJoinButton;
    private TextInputEditText mTargetUserId;
    private TextInputEditText mTargetChannel;

    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_group_channel);

        mTargetUserId = findViewById(R.id.edittext_target_user_id);
        mTargetChannel = findViewById(R.id.edittext_target_channel);

        mCreateButton = (Button) findViewById(R.id.button_create_group_channel_create);
        mJoinButton = (Button) findViewById(R.id.button_join_group_channel_join);

        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGroupChannel(mTargetChannel.getText().toString());
            }
        });
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> userIds = new ArrayList<>();
                userIds.add(PreferenceUtils.getUserId());
                userIds.add(mTargetUserId.getText().toString());

                createGroupChannel(userIds, true);
            }
        });

        mToolbar = findViewById(R.id.toolbar_create_group_channel);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp);
        }
    }

    private void joinGroupChannel(String channel) {
        Log.d(CreateGroupChannelActivity.class.getSimpleName(), "channel : " + channel);

        Intent intent = new Intent(CreateGroupChannelActivity.this, GroupChannelActivity.class);
        intent.putExtra("groupChannelUrl", channel);
        startActivity(intent);

        finish();
    }

    private void createGroupChannel(List<String> userIds, boolean distinct) {
        Log.d(CreateGroupChannelActivity.class.getSimpleName(), "userids : " + userIds);

        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                else{
                    Intent intent = new Intent(CreateGroupChannelActivity.this, GroupChannelActivity.class);
                    intent.putExtra("groupChannelUrl", groupChannel.getUrl());
                    startActivity(intent);

                    finish();
                }
            }
        });
    }
}
