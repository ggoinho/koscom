package kr.co.vguard2;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.koscom.omp.SplashActivity;
import kr.co.sdk.vguard2.ScanData;
import kr.co.sdk.vguard2.VGuard;


//실시간 감시 AsyncTask
public class TaskRealscan extends AsyncTask<ArrayList<Object>, Integer, Integer> {
	
	private Context context;
	private String scan_result;
	private String strOpt;
	private static Intent CalleeIntent;

	@Override
	protected Integer doInBackground(ArrayList<Object>... arg0) {
		context = (Context)arg0[0].get(0);
		strOpt = (String)arg0[0].get(1);
		
		//앱 설치시 검사 실행
		if(strOpt.equals("INSTAPP_SCAN")) {
			System.out.println("[TASK-REAL] INSTALL APP SCANNING");
			Scan(1, (String)arg0[0].get(2));
		}
//		//실행중인 앱  실행
//		else if(strOpt.equals("RUNAPP_SCAN")) {
//			//MainActivity.isProcessing = 5;
//
//			// 초기 한번만 검사가 실행되도록, 이후 지속적인 루팅검사 진행
//			if(VGuard.isFirstRealscanEnded == false){
//				VGuard.isFirstRealscanEnded = true;
//				if(!isRealScanning)
//				{
//					isRealScanning = true;
//					VGuard.getInstance().setScanStatus("usingEngine", true, 0);	//엔진 사용 시작 상태
//					Scan(1, (String)arg0[0].get(2));
//					isRealScanning = false;
//				}
//			}
//
//		}
		// 실시간 감시 서비스 구동 시 최초 1회에 한하여 호출되어 설치된 앱 검사 진행 및 루팅검사 진행.
		else if(strOpt.equals("INITIAL_SCAN")) {
			System.out.println("[TASK-REAL] INITIAL APP SCANNING");
			Scan(1, (String)arg0[0].get(2));

		}
		//Activity를 사용하지 않는 검사 실행
		else if(strOpt.equals("NONUI_SCAN")) {
			Scan(1, (String)arg0[0].get(2));
		}

		
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		SplashActivity.Companion.setProcessing(0);
		// 검사 결과가 null이 아니고, 제대로 된 결과값일때 처리
		boolean isRooting = false;
		try {
			isRooting = VGuard.getInstance().RootedCheck(0);
		} catch (Exception e1) {
			// 루팅검사에 문제가 발생한 경우, 샘플소스상에서는 일반폰임을 나타내는 결과를 리턴, 예외발생 시 이후 동작은 고객사의 정책을 적용 필요.
			isRooting = false;
		}
		
		if((scan_result != null && scan_result.length() > 9) || isRooting) { //---- 바이러스 검출 또는 루팅이 확인된 경우
			//UI가 없는 검사의 결과 처리

			if(SplashActivity.Companion.getMainProgress() !=null){
				SplashActivity.Companion.getMainProgress().cancel();
				SplashActivity.Companion.getMainProgress().dismiss();
			}
			ScanActivity scanAct = (ScanActivity)ScanActivity.getScanActivity();
			//ScanActivity가 실행된적이 없거나, Back키에 의해 꺼져 없는 경우 새로 실행
			if(scanAct == null)
			{
				CalleeIntent = new Intent(context, ScanActivity.class);
				CalleeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isRooting) {
					CalleeIntent.putExtra("Rooting", "Rooting");
				}
				CalleeIntent.putExtra("RealScanResult", scan_result);
				context.startActivity(CalleeIntent);
			}
			//ScanActivity가 실행된 적이 있어  Home키에 의해 뒤로 사라 졌을 때 실행
			else {
				Intent intent = new Intent(context, ScanActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				if (isRooting) {
					intent.putExtra("Rooting", "Rooting");
				}
				intent.putExtra("RealScanResult", scan_result);
				PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				try {
					pi.send();
				} catch (CanceledException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			
			if(VGuard.NonUIScanActivityMode ==true){ //---- 첫 검사 시, 바이러스가 없는 경우
				VGuard.NonUIScanActivityMode = false; //----초기에만 동작하게 끔 처리 , 금결원용 소스 재 호출 방지 .
				Intent Returnintent = new Intent();
				Returnintent.putExtra("list","");
				//MainActivity.NonUIProcessingResult(-1, Returnintent); //--- -1 RESULT_OK, 메인 액티비티로 돌아감
				(SplashActivity.Companion.getMainContext()).NonUIProcessingResult(-1, Returnintent);// 4/23 금융결제원 요청으로 인하여 메서드의 자료형을 변경
				
			}
		}


		// 2019/05/23 Initial스캔(서비스 실행 후 최초 설치된  앱검사 진행) 완료 시점인 경우, 주기적인 루팅검사의 알람을 등록.
		if(strOpt.equals("INITIAL_SCAN")){
			if(RealtimeScanningService.getBindService()!= null){
				RealtimeScanningService.getBindService().setPeriodicalRootingScanAlarm();
			}
		}


		super.onPostExecute(result);
	}


	/**
	 * @param opt	1: package scan
	 * 						2: sdcard scan	
	 * 						4: full scan (패키지 목록을 구하지 않기 때문에 패키지 검사는 하지 않는다)
	 * 						5: package + full scan 
	 * @param paths	패지키 경로들의 목록.
	 * 							옵션 1, 5일 때 패키지 경로의 목록 필요
	 */
	private void Scan(int opt, String paths) {
		
		scan_result = VGuard.getInstance().RealAsync_ScanThread(opt, paths);
	
		
		SplashActivity.Companion.setProcessing(0);
		
		if(scan_result == null) {
			scan_result = "";
		}
		else if(scan_result.length() == 0)
		{
		}
		else if(scan_result.charAt(0) == '*')
		{
		}
		else if(scan_result.length() > 12)
		{
			ArrayList<ScanData> logList = VGuard.getInstance().Checkup_ResultToList(scan_result);
			//서버로 바이러스 로그 보내기
			VGuard.getInstance().LogVirus(VGuard.AuthKey, VGuard.getInstance().DeviceId(), logList, context.getApplicationInfo().packageName);
		}
	}

}
