package kr.co.koscom.omp.adapter.holder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_nego_management.*
import kotlinx.android.synthetic.main.item_nego_management.view.*
import kotlinx.android.synthetic.main.item_orderdetail_nego.layoutNegoBottomInfo
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvAccept
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvContentsCount
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvContentsPrice
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.extension.*

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "orderItem"
private const val ARG_PARAM3 = "totalCount"

class ContractManagePagerFragment : Fragment() {
    private var position: Int = 0
    private var contractData: Main.NegoContract? = null
    private var totalCount: Int = 0

    private var listener: MainHeaderHolder.OnContractPagerClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_PARAM1, 0)
            contractData = it.getSerializable(ARG_PARAM2) as Main.NegoContract
            totalCount = it.getInt(ARG_PARAM3, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_nego_management, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initListener()
        onBind()
    }


    fun onBind() {
        tvPageCount.toTextfromHtml(R.string.main_viewpager_indicator_count.toResString(), position+1, totalCount)

        contractData?.let {

            tvStockName.text = it.STK_NM
            tvStockGubun.text = it.STOCK_TP_CODE_NM + when {
                (it.SEC_KIND_DTL_TP_CODE == "02") -> {R.string.unit_repayment.toResString()}
                (it.SEC_KIND_DTL_TP_CODE == "03") -> {R.string.unit_conversion_payment.toResString()}
                else -> {""}
            }
            tvContentsCount.text = it.DEAL_QTY
            tvContentsPrice.text = it.DEAL_UPRC
            tvHost.text = String.format(R.string.main_viewpager_opponent.toResString(), it.NEGO_REQST_CLNT_NM)

            if(it.PUBLIC_YN.isY()){
                ivSecret.toGone()
            }else{
                ivSecret.toVisible()
            }

            when(it.NEGO_SETT_STAT_CODE){
                "204"->{
                    tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_11_success.toDrawable(), null, null, null)
                    tvConclusion.text = R.string.contract.toResString()
                }
                "206"->{
                    tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_11_success.toDrawable(), null, null, null)
                    tvConclusion.text = R.string.contract.toResString()
                }
                else ->{
                    tvConclusion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_state_12_complete.toDrawable(), null, null, null)
                    tvConclusion.text = R.string.conclusion.toResString()
                }
            }

        }
    }

    private fun initView(){
        layoutContractBottomInfo.toVisible()
    }

    private fun initListener(){
        layoutConclusion.setOnClickListener {
            contractData?.let {data->
                listener?.onContractClick(data, position)
            }
        }

        tvContractConfirm.setOnClickListener {
            contractData?.let {data->
                listener?.onConfirmClick(data, position)
            }
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(position: Int, data: Main.NegoContract, totalCount: Int, listener: MainHeaderHolder.OnContractPagerClickListener) =
            ContractManagePagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                    putSerializable(ARG_PARAM2, data)
                    putInt(ARG_PARAM3, totalCount)
                }

                this.listener = listener
            }
    }
}
