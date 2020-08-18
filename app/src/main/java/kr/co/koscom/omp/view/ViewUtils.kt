package kr.co.koscom.omp.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import kr.co.koscom.omp.LoginActivity
import kr.co.koscom.omp.R
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.util.ActivityUtil

class ViewUtils {

    companion object{

        fun showProgressDialog(context: Context, title: String, message: String, cancelable: Boolean): MyDialog{
            var myDialog = MyDialog(context);

            myDialog!!.setCancelable(cancelable)

            myDialog!!.title?.setText(title)
            myDialog!!.message?.setText(message)

            // 커스텀 다이얼로그를 노출한다.
            myDialog!!.show();

            return myDialog
        }

        fun showErrorMsg(activity: Activity, rCode: String?, rMsg: String?){

            alertDialog(activity, rMsg ?: ""){

                if(rCode == "9988" || rCode == "9977"){


                    ActivityUtil.startLoginActivity(activity)
//                    val intent = Intent(activity, LoginActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    activity.startActivity(intent)

                    activity.finish()
                }

            }
        }

        fun alertDialog(context: Context, info: String?, listener: () -> Unit){
            var dlg = Dialog(context)
            dlg.setCancelable(true)

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert2);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            message.setText(info?:"")
            Log.d("viewutils", "message settext.. " + info)

            btnOk.setOnClickListener {
                dlg.dismiss()
                listener.invoke()
            }
            btnConfirm.setOnClickListener {
                dlg.dismiss()
                listener.invoke()
            }
            btnCancel.setOnClickListener {
                dlg.dismiss()
                dlg.cancel()
            }
            dlg.setOnCancelListener {
                listener.invoke()
            }
        }

        fun alertLogoutDialog(context: Context, info: String?, listener: () -> Unit){
            var dlg = Dialog(context);
            dlg.setCancelable(true)

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert2);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var confirmZone = dlg.findViewById<LinearLayout>(R.id.confirmZone)
            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var btnOk = dlg.findViewById<TextView>(R.id.btnOk)
            var message = dlg.findViewById<TextView>(R.id.message)
            message.setText(info?: "")
            Log.d("viewutils", "message settext.. " + info)

            confirmZone.visibility = View.VISIBLE
            btnOk.visibility = View.GONE

            btnConfirm.setOnClickListener {
                dlg.dismiss()
                listener.invoke()
            }
            btnCancel.setOnClickListener {
                dlg.dismiss()
            }
        }

        fun alertReceiveDialog(context: Context, cancelListener: () -> Unit, confirmListener: () -> Unit){
            var dlg = Dialog(context);
            dlg.setCancelable(false)

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert3);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)

            btnConfirm.setOnClickListener {
                dlg.dismiss()
                confirmListener.invoke()
            }
            btnCancel.setOnClickListener {
                dlg.dismiss()
                cancelListener.invoke()
            }
        }

        fun alertUpdateDialog(context: Context, info: String?, cancelListener: () -> Unit, confirmListener: () -> Unit){
            var dlg = Dialog(context);
            dlg.setCancelable(false)

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert4);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show();

            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var message = dlg.findViewById<TextView>(R.id.message)
            message.setText(info?: "")

            btnConfirm.setOnClickListener {
                dlg.dismiss()
                confirmListener.invoke()
            }
            btnCancel.setOnClickListener {
                dlg.dismiss()
                cancelListener.invoke()
            }
        }


        fun alertCustomDialog(context: Context, info: String?, canCelContents: String?, confirmContents: String?, cancelListener: () -> Unit, confirmListener: () -> Unit){
            var dlg = Dialog(context);
            dlg.setCancelable(false)

            // 액티비티의 타이틀바를 숨긴다.
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);

            // 커스텀 다이얼로그의 레이아웃을 설정한다.
            dlg.setContentView(R.layout.activity_alert5);

            // 커스텀 다이얼로그를 노출한다.
            dlg.show()

            var btnCancel = dlg.findViewById<TextView>(R.id.btnCancel)
            var btnConfirm = dlg.findViewById<TextView>(R.id.btnConfirm)
            var message = dlg.findViewById<TextView>(R.id.message)
            message.text = info?: ""

            btnCancel.text = canCelContents?: R.string.cancel.toResString()
            btnConfirm.text = confirmContents?: R.string.confirm.toResString()


            btnConfirm.setOnClickListener {
                dlg.dismiss()
                confirmListener.invoke()
            }
            btnCancel.setOnClickListener {
                dlg.dismiss()
                cancelListener.invoke()
            }
        }
    }
}