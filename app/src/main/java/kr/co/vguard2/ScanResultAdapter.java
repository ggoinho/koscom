package kr.co.vguard2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.koscom.omp.R;
import kr.co.sdk.vguard2.ScanData;

public class ScanResultAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ScanData> mAppList;
	private PackageManager mPackManager;

	public ScanResultAdapter(Context c, ArrayList<ScanData> list, PackageManager pm) {
		mContext = c;
		mAppList = list;
		mPackManager = pm;
	}

	//@Override
	public int getCount() {
		return mAppList.size();
	}

	//@Override
	public Object getItem(int position) {
		//ApplicationInfo appinfo = mPackManager.getPackageArchiveInfo(mListAppInfo.get(position).getPath(), 0).applicationInfo;
		return mAppList.get(position);
	}

	//@Override
	public long getItemId(int position) {
		return position;
	}

	//@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String appPath 	= mAppList.get(position).getPath();
		String virusName = mAppList.get(position).getVirusName();
		 
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = inflater.inflate(R.layout.list_raw_vguard, null);
		}

		ImageView ivAppIcon 	= (ImageView)v.findViewById(R.id.virus_img);
		TextView tvAppName 	= (TextView)v.findViewById(R.id.package_name);
		TextView tvPkgName 	= (TextView)v.findViewById(R.id.file_path);
		TextView tvVirusName 	= (TextView)v.findViewById(R.id.virus_name);
		
		TextView tvAppName_bg 	= (TextView)v.findViewById(R.id.package_name_bg);
		TextView tvPkgName_bg 	= (TextView)v.findViewById(R.id.file_path_bg);
		TextView tvVirusName_bg 	= (TextView)v.findViewById(R.id.virus_name_bg);
		
		PackageInfo pkgInfo = mPackManager.getPackageArchiveInfo(mAppList.get(position).getPath(), 0);
		if(pkgInfo != null)
		{
			ApplicationInfo appinfo = pkgInfo.applicationInfo;
			appinfo.sourceDir = appPath;
			appinfo.publicSourceDir = appPath;
		
			//이미지 모서리 둥글게 처리하기
			Drawable drabl = appinfo.loadIcon(mPackManager);
			Bitmap bitmap = drawableToBitmap(drabl);
			bitmap = getRoundedCornerBitmap(bitmap);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
			Drawable softDrawable = (Drawable)bitmapDrawable;
			
			ivAppIcon.setImageDrawable(softDrawable);
			tvAppName.setText(appinfo.loadLabel(mPackManager));
			tvPkgName.setText(appinfo.sourceDir);
			
			tvAppName_bg.setText(appinfo.loadLabel(mPackManager));
			tvPkgName_bg.setText(appinfo.sourceDir);
		}
		else
		{
			Drawable drawable = mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
			ivAppIcon.setImageDrawable(drawable);
			tvAppName.setText("FILE.ANDROID");
			tvPkgName.setText(appPath);
			
			tvAppName_bg.setText("FILE.ANDROID");
			tvPkgName_bg.setText(appPath);
		} 
		tvVirusName.setText(virusName);
		tvVirusName_bg.setText(virusName);

		return v;
	}
	
	private Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap((drawable.getIntrinsicWidth()<0)? 10:drawable.getIntrinsicWidth(),
	    													(drawable.getIntrinsicHeight()<0)? 10:drawable.getIntrinsicHeight(),
	    													Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Bitmap.Config.ARGB_4444);
	    Canvas canvas = new Canvas(output);
	 
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 12;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	  }
}

