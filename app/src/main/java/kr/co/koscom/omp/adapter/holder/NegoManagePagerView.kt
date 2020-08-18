package kr.co.koscom.omp.adapter.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_nego_management.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.extension.toNumberFormat
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toTextfromHtml
import kr.co.koscom.omp.extension.toVisible


/**
 *
 */
class NegoManagePagerView constructor(val context: Context, val container: ViewGroup,
                                      private val negoData: Main.Nego?, val position: Int, val listener: MainHeaderHolder.OnNegoPagerClickListener?,
                                      private val totalCount: Int) {
//    private var context: Context = context
    private var view: View
//    private var container: ViewGroup = container

//    private val position = position
//    private var negoData: Main.Nego? = data
//    private var listener: MainHeaderHolder.OnNegoPagerClickListener? = listener


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.item_nego_management, container, false)

        initView()
        initListener()
    }


    fun onBind() {


        view.tvPageCount.toTextfromHtml(R.string.main_viewpager_indicator_count.toResString(), position+1, totalCount)

        negoData?.let {

            view.tvStockName.text = it.CORP_HANGL_NM
            view.tvStockGubun.text = it.STOCK_TP_CODE_NM + when {
                (it.SEC_KIND_DTL_TP_CODE == "02") -> {R.string.unit_repayment.toResString()}
                (it.SEC_KIND_DTL_TP_CODE == "03") -> {R.string.unit_conversion_payment.toResString()}
                else -> {""}
            }
            view.tvContentsCount.text = it.DEAL_QTY
            view.tvContentsPrice.text = it.DEAL_UPRC
            view.tvHost.text = String.format(R.string.main_viewpager_opponent.toResString(), it.NEGO_REQST_CLNT_NM)

        }

        container.addView(view)
        
    }

    private fun initView(){
        view.layoutNegoBottomInfo.toVisible()
    }

    private fun initListener(){
        view.tvReject.setOnClickListener {
            negoData?.let {data->
                listener?.onRejectClick(data, position)
            }
        }

        view.tvAccept.setOnClickListener {
            negoData?.let {data->
                listener?.onAcceptClick(data, position)
            }
        }

    }



    fun getView(): View {
        return view
    }

}