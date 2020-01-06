package kr.co.vguard2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;

import kr.co.sdk.vguard2.VGuard;

public class RealtimeScanningService extends Service {
	private Context mContext = null;
	private AppInstallCompleteReceiver mInstallReceiver = null;
	private static RealtimeScanningService mService;
	private final IBinder mBinder = new ReatlmeScanningServiceBinder();
	private boolean isUnBinded = false;


	private AlarmManager alManager;
	private Intent periodicalIntent;
	private PendingIntent periodicalPendingIntent;


	public class ReatlmeScanningServiceBinder extends Binder {
		RealtimeScanningService getService() {
			return RealtimeScanningService.this;
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		String action = intent.getStringExtra("VGuard");
		System.out.println("[RealtimeScanningService] onBind Called : " + action);
		if(action.equals("START_VGUARD_SERVICE") && (VGuard.getInstance() != null)){
			String paths = VGuard.getInstance().VarietyAppList(VGuard.SCAN_OPT_USER| VGuard.SCAN_OPT_SYSTEM);
			ArrayList<Object> buf = new ArrayList<Object>();
			buf.add(getApplicationContext());
			buf.add("INITIAL_SCAN");
			buf.add(paths);
			TaskRealscan async = new TaskRealscan();
			async.execute(buf);
		}
		return mBinder;
	}

	public static RealtimeScanningService getBindService(){
		return mService;
	}

	public void scanForAppInstall(String apkPath){
		if(VGuard.getInstance() != null){
			System.out.println("[ScanForAppInstall] Called with :" + apkPath);
			ArrayList<Object> buf = new ArrayList<Object>();
			buf.add(getApplicationContext());
			buf.add("INSTAPP_SCAN");
			buf.add(apkPath);
			TaskRealscan async = new TaskRealscan();
			async.execute(buf);
		}
	}

	public void scanForRooting(){
		if(VGuard.getInstance() != null){
			System.out.println("[ScanForRooting] Called ");
			ArrayList<Object> buf = new ArrayList<Object>();
			buf.add(getApplicationContext());
			buf.add("ROOTING_SCAN");
			TaskRealscan async = new TaskRealscan();
			async.execute(buf);
		}
	}


	protected void setPeriodicalRootingScanAlarm(){
		alManager = (AlarmManager)mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		periodicalIntent = new Intent(mContext, RealtimeScanningReceiver.class);
		periodicalPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(),  0,  periodicalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Android OS의 정책에 따라 알람간격이 최소 1분이상으로 재 조정 될 수 있음.
		// 서비스 구동 시 최초 1회의 설치된 앱 검사가 완료 시점에서 30초 뒤 부터 반복적으로 구동되는 알람을 등록.
		//alManager.setRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis()+30000, 30000, periodicalPendingIntent);
		// elapsed_time으로 하면 어느정도 주기적인 구동이 보장되지가 않음.
		alManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+30000, 30000, periodicalPendingIntent);
		System.out.println("[setPeriodicalRootingScanAlarm] Set Alarm!!");
	}
	private void unSetPeriodicalRootingScanAlarm(){
		try{
			if(alManager == null){
				alManager = (AlarmManager)mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			}
			if(periodicalIntent == null){
				periodicalIntent = new Intent(mContext, RealtimeScanningReceiver.class);
			}
			if(periodicalPendingIntent == null){
				periodicalPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(),  0,  periodicalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			}
			alManager.cancel(periodicalPendingIntent);
			periodicalPendingIntent.cancel();
			System.out.println("[unSetPeriodicalRootingScanAlarm] Cancel Alarm!!");
		}catch(Exception e){
			System.out.println("[unSetPeriodicalRootingScanAlarm] Cancel Alarm Failed by " +e.getMessage());
		}
		alManager = null;
		periodicalIntent = null;
		periodicalPendingIntent = null;
	}


	private void setAppInstallReceiver(){
		System.out.println("[setAppInstallReceiver] setting called");
		if(mInstallReceiver == null){
			mInstallReceiver = new AppInstallCompleteReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.PACKAGE_ADDED");
			filter.addDataScheme("package");
			mContext.registerReceiver(mInstallReceiver,filter);
		}
	}

	private void unSetAppInstallReceiver(){
		System.out.println("[unSetAppInstallReceiver] called");
		if(mInstallReceiver != null){
			mContext.unregisterReceiver(mInstallReceiver);
			mInstallReceiver = null;
		}
	}

	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
		setAppInstallReceiver();
		mService= RealtimeScanningService.this;
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("[RealtimeScanningService] onUnBind Called");
		isUnBinded = true;
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		System.out.println("[RealtimeScanningService] onReBind Called");
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		System.out.println("[RealtimeScanningService] onDestroy check Unbinded : " + isUnBinded);
		unSetPeriodicalRootingScanAlarm();
		unSetAppInstallReceiver();
		System.out.println("[RealtimeScanningService] onDestroy Called");
		super.onDestroy();
	}

}
