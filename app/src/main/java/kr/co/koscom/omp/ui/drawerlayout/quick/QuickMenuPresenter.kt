package kr.co.koscom.omp.ui.drawerlayout.quick

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.scsoft.boribori.data.viewmodel.*
import com.sendbird.syncmanager.utils.DateUtils
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_write.*
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.MainListData
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.data.model.*
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toYN
import kr.co.koscom.omp.ui.drawerlayout.quick.contract.QuickMenuContract
import kr.co.koscom.omp.ui.order.contract.OrderListNewContract
import kr.co.koscom.omp.ui.order.contract.OrderRegistContract
import kr.co.koscom.omp.util.LogUtil
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils

class QuickMenuPresenter(private val view: QuickMenuContract.View?): QuickMenuContract.Presenter {
    internal val tag = this.javaClass.simpleName


    private val disposable = CompositeDisposable()

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var orderViewModel: OrderViewModel


    private val activity: AppCompatActivity
        get() = (view as AppCompatActivity)

    private val context: Context
        get() = (view as Context)



    override fun detachView() {
        disposable.clear()
    }




}