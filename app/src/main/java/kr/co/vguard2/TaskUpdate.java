package kr.co.vguard2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import kr.co.koscom.omp.SplashActivity;
import kr.co.sdk.vguard2.VGuard;
import kr.co.sdk.vguard2.VGuard.UpdateResult;
import kr.co.sdk.vguard2.VGuard.UpdateType;

public class TaskUpdate extends AsyncTask<ArrayList<Object>, String, UpdateResult> {
	private UpdateResult result;
	private Context AsyncContext;
	private ProgressDialog mProgress;
	private UpdateType updatetype;
	private int scantype;

	public TaskUpdate(Context context, UpdateType type, int scanmode){
		super();
		AsyncContext = context;
		updatetype = type;
		scantype = scanmode;
	}
	
	protected void onPreExecute(){
//		mProgress = new ProgressDialog( AsyncContext );
//		mProgress.setProgressStyle( ProgressDialog.STYLE_SPINNER );
//		mProgress.setTitle( "업데이트중" );
//		mProgress.setMessage( "업데이트가 진행중입니다.\n잠시만 기다려 주시기 바랍니다." );
//		mProgress.setCancelable( false );
//		mProgress.setButton( "취소", new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int whichButton){
//				cancel(true);
//				((UpdateActivity)AsyncContext).finish();
//				MainActivity.isProcessing = 0;
//			}
//		});
//		mProgress.show();
	}

	@Override
	protected UpdateResult doInBackground(ArrayList<Object>... arg0) {

		try {
			SocketAddress sock_addr = new InetSocketAddress(VGuard.hostIP, VGuard.hostPort);
			Socket m_client_socket = new Socket();
			m_client_socket.connect(sock_addr, 2000);

			//Updatetype 0 : Update ,  1 : Version check
			if(m_client_socket != null && m_client_socket.isConnected())
			{
				if(updatetype == UpdateType.VERSION_CHECK)
				{
					result = VGuard.getInstance().CheckVersion();
				}
				else
				{
					result = VGuard.getInstance().Update(AsyncContext); // 수정부분 ----- 외부에서 context를 받아오도록 변경, 이 Context는 MainActivity이어야 한다.
				}
			}
		} catch (Exception e) {
			result = UpdateResult.ERROR_SOCKET;
			Log.e("TEST", e.getMessage());
		} finally {

		}
		return result;
	}

	@Override
	protected void onPostExecute(UpdateResult result) {
//		mProgress.cancel();
//		mProgress.dismiss();	

		try{
			// 업데이트
			if(updatetype == UpdateType.UPDATE || updatetype == UpdateType.AUTO || updatetype == UpdateType.NONUI || updatetype == UpdateType.BackgroundNonUI )
			{
				switch (result) {
				case ERROR_UNKNOWN:
				case ERROR_SOCKET:
				case ERROR_TIMEOUT:
					Log.v("VGuard2", "통신 상태가 원활하지 않아 업데이트에 실패 하였습니다.");
					Toast.makeText(AsyncContext, "통신 상태가 원활하지 않아 업데이트에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
					break;	
				case OK:
					Log.v("VGuard2", "최신 버전으로 업데이트 하였습니다.");
					Toast.makeText(AsyncContext, "최신 버전으로 업데이트 하였습니다.", Toast.LENGTH_SHORT).show();
					break;
				case LAST_VERSION:
					Log.v("VGuard2", "이미 최신 버전입니다.");
					Toast.makeText(AsyncContext, "이미 최신 버전입니다.", Toast.LENGTH_SHORT).show();
					break;
				case ERROR_LICENSE:
					Log.v("VGuard2", "라이선스 체크에 실패하였습니다.");
					Toast.makeText(AsyncContext, "라이선스 체크에 실패하였습니다.", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
			// 버전체크
			else if(updatetype == UpdateType.VERSION_CHECK)
			{
				switch (result) {
				case ERROR_UNKNOWN:
				case ERROR_SOCKET:
				case ERROR_TIMEOUT:
					Log.v("VGuard2", "통신 상태가 원활하지 않아 버전 체크에 실패 하였습니다.");
					Toast.makeText(AsyncContext, "통신 상태가 원활하지 않아 버전 체크에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
					break;	
				case OLD_VERSION:
					Log.v("VGuard2", "버전이 오래되어 업데이트가 필요합니다.");
					Toast.makeText(AsyncContext, "버전이 오래되어 업데이트가 필요합니다.", Toast.LENGTH_SHORT).show();
					break;
				case LAST_VERSION:
					Log.v("VGuard2", "최신버전 입니다.");
					Toast.makeText(AsyncContext, "최신버전 입니다.", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			Log.e("EXCEPTION", "UpdateAsync onPostExecute NullPointerException");
		}
		
		//검사가 끝났음을 표시함
		SplashActivity.Companion.setProcessing(0);

		if(updatetype == updatetype.BackgroundNonUI){

			// 2014.12.02수정
			// 메리츠종금증권 앱 취약점 관련 처리사항
			// ScanActivity진입 시점에서 행해지던 루팅검사를 패턴업데이트가 완료된 시점에서 시행하도록 수정.
			// 따라서, 이 부분에서 루팅검사를 진행하고, 루팅이 판명되면 ScanActivity화면을 닫고, MainActivity를 종료함으로써, 앱이 종료되도록 처리.
			boolean isRootingcheck;
			try {
				isRootingcheck = VGuard.getInstance().RootedCheck(0);
				if(isRootingcheck){
					AlertDialog.Builder alert = new AlertDialog.Builder(AsyncContext);
					alert.setMessage("루팅폰 입니다");
					// 뒤로가기 버튼 터치를 통한 다이얼로그 취소 비활성화
					alert.setCancelable(false);
					alert.setPositiveButton("확인",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((ScanActivity)AsyncContext).finish();
							(SplashActivity.Companion.getMainContext()).finish();
						}
					});
					alert.show();
				}
				else{
					//VGuard.getInstance().NonUIScan(VGuard.ServiceName,  VGuard.SCAN_OPT_USER | VGuard.SCAN_OPT_SYSTEM); // ui가 없는 백그라운드 스레드 검사, 04/01--- 금융결제원 요청
					// 04/23-- NonUIScan모드의 메서드호출 및 시간 단축을 위하여 스레드 관련 코드를 따로 빼냄
					int option = VGuard.SCAN_OPT_RUNNING;
					String paths = VGuard.getInstance().VarietyAppList(option);
					ArrayList<Object> buf = new ArrayList<Object>();
					buf.add(AsyncContext);
					buf.add("NONUI_SCAN");
					buf.add(paths);
					buf.add(option);
					TaskRealscan async = new TaskRealscan();
					async.execute(buf);	
				}
			} catch (Exception e) {
				// 루팅검사에 문제가 발생한 경우, 샘플소스상에서는 앱 종료를 시행하며, 예외상항 발생 시 이후 동작은 고객사의 정책을 적용 필요.
				((ScanActivity)AsyncContext).finish();
				(SplashActivity.Companion.getMainContext()).finish();
			}
		}
		super.onPostExecute(result);
	}
}
