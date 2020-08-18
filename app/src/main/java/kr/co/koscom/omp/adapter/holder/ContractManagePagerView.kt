package kr.co.koscom.omp.adapter.holder

import android.content.Context
import android.view.*
import kotlinx.android.synthetic.main.item_nego_management.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.MainPagerContractAdapter
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.extension.*


/**
 *
 */
class ContractManagePagerView constructor(val context: Context, val container: ViewGroup,
                                          private val contractData: Main.NegoContract?, val position: Int, val listener: MainHeaderHolder.OnContractPagerClickListener?,
                                          private val totalCount: Int) {
    private var view: View

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.item_nego_management, container, false)

        initView()
        initListener()
    }


    fun onBind() {


        view.tvPageCount.toTextfromHtml(R.string.main_viewpager_indicator_count.toResString(), position+1, totalCount)

        contractData?.let {

            view.tvStockName.text = it.STK_NM
            view.tvStockGubun.text = it.STOCK_TP_CODE_NM + when {
                (it.SEC_KIND_DTL_TP_CODE == "02") -> {R.string.unit_repayment.toResString()}
                (it.SEC_KIND_DTL_TP_CODE == "03") -> {R.string.unit_conversion_payment.toResString()}
                else -> {""}
            }
            view.tvContentsCount.text = it.DEAL_QTY
            view.tvContentsPrice.text = it.DEAL_UPRC
            view.tvHost.text = String.format(R.string.main_viewpager_opponent.toResString(), it.NEGO_REQST_CLNT_NM)

            if(it.PUBLIC_YN.isY()){
                view.ivSecret.toGone()
            }else{
                view.ivSecret.toVisible()
            }

            when(it.NEGO_SETT_STAT_CODE){
                "204"->{
                    view.tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_11_success.toDrawable(), null, null, null)
                    view.tvConclusion.text = R.string.contract.toResString()
                }
                "206"->{
                    view.tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_11_success.toDrawable(), null, null, null)
                    view.tvConclusion.text = R.string.contract.toResString()
                }
                else ->{
                    view.tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_12_complete.toDrawable(), null, null, null)
                    view.tvConclusion.text = R.string.conclusion.toResString()
                }
            }

        }

        container.addView(view)
        
    }


    private fun initView(){
        view.layoutContractBottomInfo.toVisible()
    }

    private fun initListener(){
        view.layoutConclusion.setOnClickListener {
            contractData?.let {data->
                listener?.onContractClick(data, position)
            }
        }

        view.tvContractConfirm.setOnClickListener {
            contractData?.let {data->
                listener?.onConfirmClick(data, position)
            }
        }

    }


    fun getView(): View {
        return view
    }


}