package kr.co.vguard2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import kr.co.koscom.omp.SplashActivity;
import kr.co.koscom.omp.view.MyDialog;
import kr.co.koscom.omp.view.ViewUtils;
import kr.co.sdk.vguard2.ScanData;
import kr.co.sdk.vguard2.VGuard;

public class TaskScan extends AsyncTask<Integer, Integer, Integer> {
	public static String ResultScan = "";
	private ArrayList<ScanData> alist;

	private int totalFiles;
	private MyDialog mProgress;
	private int mValue;
	private String result;
	private String currFile;
	private Context AsyncContext;
	// 2016/08/23 수정.
	// 대체 API Checkup_TotalFile(int), Checkup_ScanThread(int)를 적용하게 되면,
	// 앱 리스트를 전달할 필요가 없도록 SDK가 수정됨에 따라 파라메터 제거 적용.
	//public TaskScan(Context context, int _option, String app_path_list){
	//private String app_list;
	private int option;

	// 2016/08/23 수정.
	// 대체 API Checkup_TotalFile(int), Checkup_ScanThread(int)를 적용하게 되면,
	// 앱 리스트를 전달할 필요가 없도록 SDK가 수정됨에 따라 파라메터 제거 적용.
	//public TaskScan(Context context, int _option, String app_path_list){
	public TaskScan(Context context, int _option){
		super();
		AsyncContext = context;
		option = _option;
		//app_list = app_path_list;
		// 2015/05/27
		// 검사 진행 시 재검사 버튼 비활성화 추가
		ScanActivity.isScanning =1;
	}


	protected void onPreExecute(){
		
		
		if(!ResultScan.equals("") || result != null)
			ResultScan = ""; result = null;

		mValue = 0;

		mProgress = ViewUtils.Companion.showProgressDialog(AsyncContext, "검사중", "검사가 준비중입니다.\n잠시만 기다려 주시기 바랍니다.", false);

		mProgress.show();
		
		// totalFiles = VGuard.getInstance().Checkup_TotalFile(option, app_list);
		// 2016/08/23 수정
		// 위 메소드는 곧 Deprecated 될 예정이므로, 동일 기능을 가진 대체 API인 Checkup_TotalFile(int)를 사용하도록 수정.
		totalFiles = VGuard.getInstance().Checkup_TotalFile(option);
		//Log.e("taskscan_totalcount",Integer.toString(totalFiles));
		
		//mProgress.setMax(totalFiles);
		
		//VGuard.getInstance().Checkup_ScanThread(option, app_list);
		// 2016/08/23 수정
		// 위 메소드는 곧 Deprecated 될 예정이므로, 동일 기능을 가진 대체 API인 Checkup_ScanThread(int)를 사용하도록 수정.
		VGuard.getInstance().Checkup_ScanThread(option);
		//포어그라운드 검사가 실행이면 상태를 true로 
		VGuard.getInstance().setScanStatus("onCheck", true, 0);
		
		try { Thread.sleep(10); } catch (InterruptedException e) { Log.v("Error", e.toString()); }
		
	}

	protected Integer doInBackground(Integer... arg0){
		while ( isCancelled() == false ){
			if(result != null){
				if(result.charAt(0) == ',')
				{
					try {
						if(!result.split(",")[1].equals(""))
						{
							currFile = result.split(",")[1];
							currFile = currFile.substring(0, currFile.indexOf('/', 1)) + "/..." + currFile.substring(currFile.length() - 16) + "\n";

							mValue = Integer.parseInt(result.split(",")[2]);
						}
					} catch (Exception e) {
						Log.i("Java Error!!!!", "doInBackground" + e.toString());
					}
				}
				else if(!result.equals("0,0,0"))
				{   
					ResultScan = result;
				}

				if(!ResultScan.equals(""))
				{
					publishProgress( totalFiles );
					break;
				}
				
				if ( mValue <= totalFiles ){
					publishProgress( mValue );
					//20150105 테스트코드
					// 검사가 다 끝났음에도 불구하고 화면을 빠져나오지 못하는 증상관련, 자바코드로 처리가능여부 확인.
					// 화면은 넘어갈 수 있으나, 재검사 시에 엔진이 초기화 되지 않았다면 문제가 발생(재검사기능이 안됨).
//					if(mValue == totalFiles){
//						// 끝난거....기 때문에 while문 빠져나오기
//						break;
//					}
				}

				try { Thread.sleep(50); } catch (InterruptedException e) { Log.v("Error", e.toString()); }
			}
			result = VGuard.getInstance().Checkup_CurrentStatus(0);
			//testcode 20150105
			System.out.println("totalcoutn = " + Integer.toString(totalFiles) + "result = [" + result + "]");
		}
		alist = VGuard.getInstance().Checkup_ResultToList(ResultScan);
		
		if(ResultScan != null && ResultScan.length() > 10 && !ResultScan.startsWith("*")) {
			//서버로 바이러스 로그 보내기
			if(ResultScan != null && ResultScan.length() > 10 && !ResultScan.startsWith("*")) {
				//서버로 바이러스 로그 보내기
				VGuard.getInstance().LogVirus(VGuard.AuthKey, VGuard.getInstance().DeviceId(), alist, AsyncContext.getApplicationInfo().packageName);
			}
		}
		
		return mValue;
	}

	protected void onProgressUpdate( Integer... progress ){
		//mProgress.setProgress(progress[0]);
		if(currFile != null && !currFile.equals(""))
		{
			mProgress.getMessage().setText(currFile);
		}
	}

	protected void onPostExecute(Integer result){
		//검사가 끝났음을 표시함
		//MainActivity.isProcessing = 0;
		// 2015/05/27
		// 검사 종료 시 재검사 버튼 활성화(초기화)
		ScanActivity.isScanning=0;
		
		if(alist.size() < 1) { 	//검출된게 있는 상태에서 다시 검사시 0개라면 화면 초기화가 안되서 수정
			mProgress.cancel();
			mProgress.dismiss();
			
			Intent intent = new Intent();
			intent.putExtra("list", "");
			((ScanActivity)AsyncContext).setResult(-1, intent); //-1 RESULT_OK // mainActivity onActivityResult에 결과 넘기려고 설정하는 부분
			((ScanActivity)AsyncContext).finish(); //화면 없애려고 만든 코드.
			System.out.println("scanact finish call : " + Long.toString(System.currentTimeMillis()));
			
			
			//----------- 03/31 금융결제원 요청 
			//---------------- 첫 검사 수행 시, 바이러스가 탐지되고, 재 검사 이후에 동작 부분 
			
			if(VGuard.NonUIScanActivityMode == true){ //----- 초기에만 동작하게 끔, 금결원용 소스 재 호출 방지
				VGuard.NonUIScanActivityMode = false;;
				Log.d("재검사 끝","메인으로 돌아감");
				Intent Returnintent = new Intent();
				Returnintent.putExtra("list","");
				//MainActivity.NonUIProcessingResult(-1, Returnintent); //--- -1 RESULT_OK, 메인액티비티로 돌아감
				(SplashActivity.Companion.getMainContext()).NonUIProcessingResult(-1, Returnintent); //4/23 금융결제원 요청으로 인하여 메서드의 자료형을 변경
				}
				
	
		}
		else
		{
			Intent intent = new Intent();
			intent.putExtra("list", ResultScan);
			((ScanActivity)AsyncContext).setResult(-1, intent); // -1 RESULT_OK
			((ScanActivity)AsyncContext).ShowNDel(alist);
			
			String virusCount = ResultScan.split("\\n")[ResultScan.split("\\n").length -1].split(",")[0];
			String virusResult = "총 검사 개수 : " + totalFiles + "\n" + "바이러스 수  : " + virusCount;
			mProgress.getTitle().setText("검사완료");
			mProgress.getMessage().setText(virusResult);
			
			String sendData = VGuard.getInstance().DeviceId() + "\n" + ResultScan;
			
			Log.i("Log", virusResult);
			if(alist.size() > 0) {
				VGuard.getInstance().LogtoFile(1, virusResult);
			}
			
			mProgress.cancel();
			mProgress.dismiss();
			
//			Button myButton = mProgress.getButton(ProgressDialog.BUTTON_POSITIVE);
//			myButton.setText("닫기");
		}
		
		//포어그라운드 바이러스 검사가 끝나면 fasle 상태로 만든다.
		VGuard.getInstance().setScanStatus("onCheck", false, 0);
	}

	protected void onCancelled(){
		SplashActivity.Companion.setProcessing(0);
		VGuard.getInstance().Checkup_CurrentStatus(1);
		mProgress.dismiss();
		//포어그라운드 바이러스 검사가 끝나면 fasle 상태로 만든다.
		VGuard.getInstance().setScanStatus("onCheck", false, 0);
	}

}