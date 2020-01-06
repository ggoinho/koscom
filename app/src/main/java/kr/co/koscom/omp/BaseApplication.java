package kr.co.koscom.omp;


import android.app.Application;
import android.content.Context;
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
}
