package kr.co.koscom.omp.adapter.holder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_nego_management.*
import kotlinx.android.synthetic.main.item_orderdetail_nego.layoutNegoBottomInfo
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvAccept
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvContentsCount
import kotlinx.android.synthetic.main.item_orderdetail_nego.tvContentsPrice
import kr.co.koscom.omp.R
import kr.co.koscom.omp.data.model.Main
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toTextfromHtml
import kr.co.koscom.omp.extension.toVisible

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "orderItem"
private const val ARG_PARAM3 = "totalCount"

class NegoManagePagerFragment : Fragment() {
    private var position: Int = 0
    private var negoData: Main.Nego? = null
    private var totalCount: Int = 0

    private var listener: MainHeaderHolder.OnNegoPagerClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_PARAM1, 0)
            negoData = it.getSerializable(ARG_PARAM2) as Main.Nego
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

        negoData?.let {

            tvStockName.text = it.CORP_HANGL_NM
            tvStockGubun.text = it.STOCK_TP_CODE_NM + when {
                (it.SEC_KIND_DTL_TP_CODE == "02") -> {R.string.unit_repayment.toResString()}
                (it.SEC_KIND_DTL_TP_CODE == "03") -> {R.string.unit_conversion_payment.toResString()}
                else -> {""}
            }
            tvContentsCount.text = it.DEAL_QTY
            tvContentsPrice.text = it.DEAL_UPRC
            tvHost.text = String.format(R.string.main_viewpager_opponent.toResString(), it.NEGO_REQST_CLNT_NM)

        }

    }

    private fun initView(){
        layoutNegoBottomInfo.toVisible()
    }

    private fun initListener(){
        tvReject.setOnClickListener {
            negoData?.let {data->
                listener?.onRejectClick(data, position)
            }
        }

        tvAccept.setOnClickListener {
            negoData?.let {data->
                listener?.onAcceptClick(data, position)
            }
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(position: Int, data: Main.Nego, totalCount: Int, listener: MainHeaderHolder.OnNegoPagerClickListener) =
            NegoManagePagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
                    putSerializable(ARG_PARAM2, data)
                    putInt(ARG_PARAM3, totalCount)
                }

                this.listener = listener
            }
    }
}
