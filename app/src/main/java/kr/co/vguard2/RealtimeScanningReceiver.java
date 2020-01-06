package kr.co.vguard2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.co.sdk.vguard2.VGuard;

/**
 * 주기적인 루팅검사를 진행하기 위해 약 30초 마다 알람을 이용하여 이벤트를 받는다.
 */
public class RealtimeScanningReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 실시간 감시 서비스가 구동 중인 상황이라면, 루팅검사를 진행한다.
		if(RealtimeScanningService.getBindService() != null && (VGuard.getInstance() != null)){
			RealtimeScanningService.getBindService().scanForRooting();
		}else{ // 실시간 감시 서비스가 구동되지 않는 상황이라면, 불 필요한 알람의 해제를 진행.
			System.out.println("[BindRootScanReceiver] Service isn't Running State....");
			deleteAlarmForPreventAutoStarting(context);
		}
	}

	// 2019.05.23 수정.
	// 프로그램이 강제종료 또는 종료시점에서 알람이 정상해제 되지 않았을 경우, BroadcastReceiver에서 알람해제를 지원하도록 추가된 코드.
	private void deleteAlarmForPreventAutoStarting(Context context){
		AlarmManager alManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent periodicalIntent = new Intent(context, RealtimeScanningReceiver.class);
		PendingIntent periodicalPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),  0,  periodicalIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alManager.cancel(periodicalPendingIntent);
		periodicalPendingIntent.cancel();
		System.out.println("[deleteAlarmForPreventAutoStarting] Cancel Alarm!!");
	}

}
