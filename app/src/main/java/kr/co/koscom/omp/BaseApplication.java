package kr.co.koscom.omp;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.sendbird.android.SendBird;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.utils.PreferenceUtils;

import kr.co.sdk.vguard2.VGuard;

public class BaseApplication extends MultiDexApplication {

    private static Context context;

    private static final String APP_ID = "32724A64-3846-4A86-A24B-9455A2524F71"; // US-1 Demo
    public static final String VERSION = "3.0.40";
    private boolean mIsSyncManagerSetup = false;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        PreferenceUtils.init(getApplicationContext());

        SendBird.init(APP_ID, getApplicationContext());
        SendBirdSyncManager.setLoggerLevel(98765);

        createNotificationChannel();

    }

    public boolean isSyncManagerSetup() {
        return mIsSyncManagerSetup;
    }

    public void setSyncManagerSetup(boolean syncManagerSetup) {
        mIsSyncManagerSetup = syncManagerSetup;
    }

    public static Context getAppContext() {
        return context;
    }


    /**
     * Oreo 이상 버전 Notification 채널 생성작업.
     */
    private void createNotificationChannel(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            if(notificationManager.getNotificationChannel(CHANNEL_ID) == null){
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
                mChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(mChannel);
            }
        }
    }
}
