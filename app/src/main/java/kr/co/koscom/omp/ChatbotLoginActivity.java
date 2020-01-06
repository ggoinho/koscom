package kr.co.koscom.omp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.syncmanager.ConnectionManager;
import com.sendbird.syncmanager.utils.PreferenceUtils;
import com.sendbird.syncmanager.utils.PushUtils;

// kr.co.koscom.omp
// BeMyUNICORN

public class ChatbotLoginActivity extends AppCompatActivity {

    private ContentLoadingProgressBar mProgressBar;
    private TextInputEditText edittext_login_user_id;
    private TextInputEditText edittext_login_user_nickname;
    private CoordinatorLayout mLoginLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatlogin);

        mProgressBar = findViewById(R.id.progress_bar_login);
        mLoginLayout = findViewById(R.id.layout_login);
        edittext_login_user_id = findViewById(R.id.edittext_login_user_id);
        edittext_login_user_nickname = findViewById(R.id.edittext_login_user_nickname);

        edittext_login_user_id.setText(PreferenceUtils.getUserId());
        edittext_login_user_nickname.setText(PreferenceUtils.getNickname());

        findViewById(R.id.button_login_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = edittext_login_user_id.getText().toString();
                // Remove all spaces from userID
                userId = userId.replaceAll("\\s", "");

                String userNickname = edittext_login_user_nickname.getText().toString();

                PreferenceUtils.setUserId(userId);
                PreferenceUtils.setNickname(userNickname);

                connectToSendBird(userId, userNickname);
            }
        });
    }


    /**
     * Attempts to connect a user to SendBird.
     * @param userId    The unique ID of the user.
     * @param userNickname  The user's nickname, which will be displayed in chats.
     */
    private void connectToSendBird(final String userId, final String userNickname) {

        showProgressBar(true);
        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.
                showProgressBar(false);

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            ChatbotLoginActivity.this, "" + e.getCode() + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();

                    // Show login failure snackbar
                    showSnackbar("Login to SendBird failed");
                    PreferenceUtils.setConnected(false);
                }
                else{

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {

                            Log.d(ChatbotLoginActivity.class.getSimpleName(), "firebase token : " + instanceIdResult.getToken());

                            SendBird.registerPushTokenForCurrentUser(instanceIdResult.getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                                @Override
                                public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                        return;
                                    }

                                    if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                                        Log.d(ChatbotLoginActivity.class.getSimpleName(), "Connection required to register push token.");
                                    }
                                    else{
                                        Log.d(ChatbotLoginActivity.class.getSimpleName(), "registerPushTokenForCurrentUser success.");
                                    }
                                }
                            });
                        }
                    });


                    PreferenceUtils.setConnected(true);

                    // Update the user's nickname
                    updateCurrentUserInfo(userNickname);
                    updateCurrentUserPushToken();

                    // Proceed to MainActivity
                    Intent intent = new Intent(ChatbotLoginActivity.this, CreateGroupChannelActivity.class);
                    startActivity(intent);

                    finish();
                }

            }
        });
    }

    /**
     * Update the user's push token.
     */
    private void updateCurrentUserPushToken() {
        PushUtils.registerPushTokenForCurrentUser(ChatbotLoginActivity.this, null);
    }

    /**
     * Updates the user's nickname.
     * @param userNickname  The new nickname of the user.
     */
    private void updateCurrentUserInfo(final String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(
                            ChatbotLoginActivity.this, "" + e.getCode() + ":" + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();

                    // Show update failed snackbar
                    showSnackbar("Update user nickname failed");

                    return;
                }

                PreferenceUtils.setNickname(userNickname);
            }
        });
    }


    // Displays a Snackbar from the bottom of the screen
    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(mLoginLayout, text, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    // Shows or hides the ProgressBar
    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.show();
        } else {
            mProgressBar.hide();
        }
    }
}
