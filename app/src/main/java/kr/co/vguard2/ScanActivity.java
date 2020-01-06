package kr.co.vguard2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.co.koscom.omp.R;
import kr.co.koscom.omp.SplashActivity;
import kr.co.sdk.vguard2.ScanData;
import kr.co.sdk.vguard2.VGuard;
import kr.co.sdk.vguard2.VGuard.UpdateType;

public class ScanActivity extends Activity implements OnClickListener {
	private static ListView mListview;
	private static Context context;
	private static ArrayList<ScanData> alist = new ArrayList<ScanData>();
	private static int default_scan_option = VGuard.SCAN_OPT_USER | VGuard.SCAN_OPT_SYSTEM;
	
	private Button btnExit;
	private Button btnStartScan;
	private RelativeLayout nofileLayout;
	private ImageView nofileIcon;
	private ScanResultAdapter adapter;
	
	// 20140522 추가
	// 기기관리자 삭제시 필요한 변수
	// DPMAPP_REMOVE_REQUEST : 기기관리자 삭제용 고유 REQUST 값
	// DPMApp_POS : 기기관리자 해제 후 바로 삭제를 하기 위해, 리스트상의 포지션 값을 저장하고 있는 변수
	private static int DPMAPP_REMOVE_REQUEST = 900;
	private static int DPMApp_POS;
	
	// 2015/05/27 검사 진행 시 재검사버튼 비활성화 추가
	public static int isScanning =0;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scandefault);
		context = this;
		
		// 2015/05/27
		// 액티비티 생성 시 비활성화 플래그 값 초기화
		isScanning =0;
		
		btnExit 			= (Button)findViewById(R.id.exit_button);
		btnStartScan 	= (Button)findViewById(R.id.search_button);
		nofileIcon 		= (ImageView)findViewById(R.id.nofile_icon);
		nofileLayout 	= (RelativeLayout)findViewById(R.id.nofile_textlayout);
		mListview 		= (ListView)findViewById(R.id.listView);
		
		btnExit.setOnClickListener(this);
		btnStartScan.setOnClickListener(this);
		
		Intent intent = getIntent();
		if(intent == null)
			return;
		
		String real_scan  			= intent.getStringExtra("RealScanResult");
		String scan_type 			= intent.getStringExtra("TYPE");
		String rooting  			= intent.getStringExtra("Rooting");
		String AbnomalExit  	= intent.getStringExtra("AbnomalExit");
		//2014/11/21 샘플 수정
		int scan_option			= intent.getIntExtra("OPTION", VGuard.SCAN_OPT_RUNNING);

		
		try{
			//실시간 감시에서 루팅 진단되어 ScanActivity를 호출 한 경우
			if(rooting != null)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setMessage("루팅폰 입니다");
				// 뒤로가기 버튼 터치를 통한 다이얼로그 취소 비활성화
				alert.setCancelable(false);
				alert.setPositiveButton("확인",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				alert.show();
				intent.removeExtra("Rooting"); //rotate시 재검사 방지
			}
			//실시간 감시에서 ScanActivity를 호출 한 경우
			else if(real_scan != null){
				ArrayList<ScanData> list = VGuard.getInstance().Checkup_ResultToList(real_scan);
				ShowNDel(list);
				intent.removeExtra("RealScanResult"); //rotate시 재검사 방지
			}
			//자동 검사를 통해 호출한 경우
			//업데이트 -> 루팅 체크 -> 악성 코드 검사 -> 실시간 감시 실행
			else if(scan_type != null && scan_type.equals("AUTO")){
				// 2015/05/27
				// 검사모드 진행 시 플래그 값 설정으로 재검사 버튼 비활성화
				isScanning =1;
				
				//버전 체크 및 업데이트
				TaskUpdate update_task = (TaskUpdate) new TaskUpdate(context, UpdateType.AUTO, scan_option).execute();
				intent.removeExtra("TYPE");	//raotate시 재검사 방지

			}
			//서비스가 비정상적으로 종료된 경우
			else if(AbnomalExit != null) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setMessage("서비스가 비정상 종료 되었습니다.");
				// 뒤로가기 버튼 터치를 통한 다이얼로그 취소 비활성화
				alert.setCancelable(false);
				alert.setPositiveButton("확인",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
						//System.exit(0);
					}
				});
				alert.show();
				intent.removeExtra("AbnomalExit"); //raotate시 재검사 방지
			}
			
		}catch(NullPointerException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		//Intent intent = getIntent();
		//Home 키로 내려갔을 때 실시간 감시에서 ScanActivity를 호출 한 경우
		int count = 0;
		String real_scan = intent.getStringExtra("RealScanResult");
		if(intent != null && real_scan != null){
			ArrayList<ScanData> list = VGuard.getInstance().Checkup_ResultToList(real_scan);
			if(alist != null) {
				for(int i = 0; i < list.size(); i++) {
					boolean couple = false;
					for(int j = 0; j < alist.size(); j++) {
						if(list.get(i).getPath().equals(alist.get(j).getPath())){
							couple = true;
							break;
						}
					}
					if(!couple) {
						alist.add(0, new ScanData(list.get(i).getPkgName(), list.get(i).getPath(), list.get(i).getVirusName(), list.get(i).getType()));
						count++;
					}
				}
			}
			if(count > 0)
				ShowNDel(alist);
		}
		
		super.onNewIntent(intent);
	}


	public void onClick(View v) {
		switch(v.getId())
		{
		//다시 검사 버튼
		case R.id.search_button:
			if (isScanning > 0) {
				System.err.println("Scan is Processing");
				break;
			}
			// 2016/08/23 수정.
			// 대체 API Checkup_TotalFile(int), Checkup_ScanThread(int)를 적용하게 되면,
			// 앱 리스트를 전달할 필요가 없도록 SDK가 수정됨에 따라 파라메터 제거 적용.
			//new TaskScan(this, default_scan_option, VGuard.getInstance().VarietyAppList(default_scan_option)).execute();
			new TaskScan(this, default_scan_option).execute();
			break;
		
		//종료 버튼
		case R.id.exit_button:
			//검사 중지 명령
			VGuard.getInstance().Checkup_CurrentStatus(1);
			SplashActivity.Companion.setProcessing(0);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setMessage("악성코드를 발견하였습니다.\n악성코드를 설치 제거하시고 다시 검사 버튼을 눌러주세요.");
			alert.setCancelable(false);
			alert.setPositiveButton("확인",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//finish();
				}
			});
			alert.show();
			
			//finish();
			break;
		}
	}

	/**
	 * 검사 결과를 보여주고 선택된 항목을 삭제 할 수 있도록 삭제 도우미를 보여준다. 
	 * ScanResultAdapter에 (Context, ArrayList<ScanData>, PackageManager) 를 보내주면 내부적으로 처리하여 화면에 뿌려준다.
	 * @param ArrayList 형태의 검사 결과를 넘겨준다.
	 */
	public void ShowNDel(ArrayList<ScanData> _list)
	{
		nofileIcon.setVisibility(View.INVISIBLE);
		nofileLayout.setVisibility(View.INVISIBLE);

		alist = _list;
		adapter = new ScanResultAdapter(this, alist, getPackageManager());
		mListview.setAdapter(adapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, final int pos, long id) {
				removeApp(pos, adapter);
			}
		});
	}

//	/**
//	 * @param 리스트 항목의 인덱스
//	 * @param 결과 화면의 어댑터
//	 * @return 삭제에 성공하였다면 true를 리턴한다.
//	 */
//	private boolean removeApp(final int Num, final ScanResultAdapter adapter ) {
//		AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
//		alertDlg.setTitle("확인");
//		alertDlg.setMessage("정말 삭제합니까?");
//		alertDlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				if(alist.get(Num).getType().equals("1"))
//				{
//					Uri uri = Uri.fromParts("package", alist.get(Num).getPkgName(), null);
//					Intent it = new Intent(Intent.ACTION_DELETE, uri);
//					startActivityForResult(it, Num);
//				}
//				else
//				{
//					File file = new File(alist.get(Num).getPath());
//					file.delete();
//					alist.remove(Num);
//					adapter.notifyDataSetChanged();
//				}
//			}
//		}) ;        
//		alertDlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {  
//			}
//		}) ;        
//		alertDlg.show();
//		return true;
//	}
	

	/**
	 * @param Num : 리스트 항목의 인덱스
	 * @param adapter : 결과 화면의 어댑터
	 * @return 삭제에 성공하면 true를 리턴한다.
	 */
	//20140522
	//기기관리자 삭제기능이 추가 된 앱 삭제 메서드
	private boolean removeApp(final int Num, final ScanResultAdapter adapter){
		//검출된 결과가 앱일 경우 
		if(alist.get(Num).getType().equals("1")){
			
			//해당 앱이 기기관리자 권한을 가지고 있는지 체크
			if(checkDPMgranted(alist.get(Num).getPkgName())){
				//해당 앱이 기기관리자 권한을 가지고 있다면 그 권한을 해제
				
				//기기관리자 해제 후 삭제를 위해, 해제 이전에 삭제하려는 앱의 리스트 상의 인덱스 값을 저장한다.
				DPMApp_POS = Num;
				removeDPMadmin(alist.get(Num).getPkgName());
			}
			else{
				// 2014/11/21 샘플수정
				// Android 5.0 태블릿에서 악성앱을 완전삭제하도록 기능 추가 
				Uri uri = Uri.fromParts("package", alist.get(Num).getPkgName(), null);
				Intent it = new Intent(Intent.ACTION_DELETE, uri);
				if (!context.getApplicationInfo().dataDir.contains("/data/user/")) { // my knox에서는 설치한 앱 위치가 /data/data가 아님.
					it.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true);
				}
				startActivityForResult(it, Num);
			}
		}
		//검출된 결과가 앱이 아닌 악성 파일인 경우
		else{
			File file = new File(alist.get(Num).getPath());
			file.delete();
			if (file.exists()) {
				// 2015/05/28 수정
				// 파일이 삭제되지 않았을 경우, 리스트에서 제거 X
				// 외장 sdcard파일 삭제일 경우
				boolean deleteresult = VGuard.getInstance().deleteContentProvider(context, file.getAbsolutePath());
				if(deleteresult){
					alist.remove(Num);
					adapter.notifyDataSetChanged();
				}
			}
			// 2015/05/28 수정
			// 파일이 삭제되지 않았을 경우, 리스트에서 제거 X
			else{
				alist.remove(Num);
				adapter.notifyDataSetChanged();
			}
		}
		
		return true;
	}
	
	// 20140522 추가
	// 기기관리자 등록 앱의 삭제를 위하여 추가함
	// 해당 앱의 기기관리자 권한 해제 메서드
	 private void removeDPMadmin(String package_name) {
         DevicePolicyManager devicePM = null;
         devicePM = ((DevicePolicyManager) getSystemService("device_policy"));
        List<ComponentName> localList = devicePM.getActiveAdmins();
        if(localList == null) {return;}
        Iterator<ComponentName> iter = localList.iterator();
        ComponentName cnn = null;
        while(iter.hasNext()) {
                   cnn = (ComponentName) iter.next();
                   if(cnn.getPackageName().equals(package_name)) {
                             Intent localIntent = new Intent();
                             localIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminAdd"));
                             localIntent.putExtra("android.app.extra.DEVICE_ADMIN", cnn);
                             startActivityForResult(localIntent, ScanActivity.DPMAPP_REMOVE_REQUEST);
                   }
        	}
	 }
	 // 20140522 추가
	 // 기기관리자 등록 앱의 삭제를 위하여 추가함
	// 해당 앱의 기기관리자 권한 보유 확인 메서드
	private boolean checkDPMgranted(String package_name){
	        DevicePolicyManager devicePM = null;
	        devicePM = ((DevicePolicyManager) getSystemService("device_policy"));
	        List<ComponentName> localList = devicePM.getActiveAdmins();
	        if(localList == null) {return false;}
	        Iterator<ComponentName> iter = localList.iterator();
	        ComponentName cnn = null;
	        while(iter.hasNext()) {
	                   cnn = (ComponentName) iter.next();
	                   if(cnn.getPackageName().equals(package_name)) {
	                             return true;
	                   }
	        }  
	        return false;
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		// 20140522 수정
		// 기기관리자 등록 악성 앱의 처리를 위해 수정
		// 해당 requestCode는 기기관리자 해제 시 에서만 리턴되는 고유지정 값.
		// 관리자권한이 해제 되지 않았을 경우, 진단결과로 돌아감
		if(requestCode == ScanActivity.DPMAPP_REMOVE_REQUEST){
			PackageManager pm = getPackageManager();
			ScanData scndata = (ScanData)mListview.getItemAtPosition(DPMApp_POS); // startForResult를 통해서 받아서 날아온 인덱스
			if( !checkDPMgranted(scndata.getPkgName())){ //권한이 해제 되었다면 삭제 진행
				removeApp(DPMApp_POS, adapter);
			}
			// 권한이 해제되지 않았다면 내버려둠 
			
		}
		else{
			
			PackageManager pm = getPackageManager();
			ScanData scndata = (ScanData)mListview.getItemAtPosition(requestCode); // startForResult를 통해서 받아서 날아온 인덱스
			//PackageInfo pkm = pm.getPackageArchiveInfo(scndata.getPath(), 0); //단말기 패키지매니져에서 해당 아이템 정보가 있는지 확인
			// 2016/04/28 수정사항.
			// 갤럭시S7(Android6.0.1)단말기에서는 앱을 삭제한 이후 진단리스트가 갱신되지 않는 문제가 확인되어 아래와 같이 getPackageInfo로 변경되었습니다.
			PackageInfo pkm = null;;
			try {
				pkm = pm.getPackageInfo(scndata.getPkgName(), 0);
			} catch (NameNotFoundException e) {
				pkm = null;
			}
			
			if(pkm == null)
			{
				alist.remove(requestCode);
				adapter.notifyDataSetChanged();
			}
		}
		
	
	}
	
	public static Context getScanActivity() {
		return context;
	}
	
	@Override
	protected void onDestroy() {
		context = null;
		// 2015/05/27
		// 액티비티 종료 시 비활성화 플래그 값 초기화
		isScanning =0;
		//검사 화면은 닫혔지만 엔진이 정지하지 않는 경우 예방
		//VGuard.getInstance().Checkup_CurrentStatus(1);
		super.onDestroy();
	}

	//BackKey 사용 가능 여부 처리
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}
}
