package kr.co.vguard2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import kr.co.sdk.vguard2.VGuard;


/**
 * 앱 설치시 이벤트를 받기 위한 브로드캐스트 리시버
 */
public class AppInstallCompleteReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			System.out.println("[APPINST RECEIVER] Called !");
			String packagePath = "";
			Uri uri = intent.getData();
			String packageName = uri.toString().substring(8);
			try {
				//설치된 앱의 경로를 받는다.
				PackageManager pm = context.getPackageManager();
				ApplicationInfo pi = pm.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
				packagePath = pi.sourceDir;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			if(RealtimeScanningService.getBindService() !=null && (VGuard.getInstance() != null)){
				RealtimeScanningService.getBindService().scanForAppInstall(packagePath);
			}
		}
	}
}
