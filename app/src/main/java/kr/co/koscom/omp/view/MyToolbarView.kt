package kr.co.koscom.omp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.scsoft.boribori.data.viewmodel.AlarmViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_toolbar.view.*
import kr.co.koscom.omp.LoginActivity
import kr.co.koscom.omp.MyPageActivity
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.Constants
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import org.apache.commons.lang3.StringUtils

class MyToolbarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var alarmViewModel: AlarmViewModel
    private val disposable = CompositeDisposable()
    private var mContext: Context
    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("", Constants.pushMessageReceived)

            searchAlarm()
        }
    }

    init {
        mContext = context
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.view_toolbar, null, false)
        addView(view)

        LocalBroadcastManager.getInstance(mContext).registerReceiver(messageReceiver, IntentFilter(Constants.pushMessageReceived))
    }

    fun initData(activity: FragmentActivity){

        btnBack.setOnClickListener {
            activity.finish()
        }
        btnAlarm.setOnClickListener {
            var intent = Intent(context, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(intent)
        }

        btnMenu.setOnClickListener {
            getRootView().findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.END)
        }

        viewModelFactory = Injection.provideViewModelFactory(context)
        alarmViewModel = ViewModelProviders.of(activity, viewModelFactory).get(AlarmViewModel::class.java)

        searchAlarm()

    }

    fun initTitle(title: String){
        subject.text = title
        Log.d("MyToolbarView", "initTitle : ${title}")
    }

    fun hideMenu(){
        btnAlarm.visibility = View.INVISIBLE
        btnMenu.visibility = View.INVISIBLE
        alarm.visibility = View.INVISIBLE
    }

    fun searchAlarm() {
        if(!PreferenceUtils.getUserId().isNullOrEmpty()){
            disposable.add(alarmViewModel.searchNotReadCount(PreferenceUtils.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.d(LoginActivity::class.simpleName, "response : " + it)

                    if("0000".equals(it.rCode)){
                        alarm.text = it.datas!!.result.toString()
                    }
                }, {
                    it.printStackTrace()
                }))

        }
    }

    fun dispose(){
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(messageReceiver)

        disposable.clear()
    }
}