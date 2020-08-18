package kr.co.koscom.omp.adapter.holder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_orderdetail_nego.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.OrderDetailPagerAdapter
import kr.co.koscom.omp.data.model.Order
import kr.co.koscom.omp.enums.OrderStatusType
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.util.ActivityUtil
import orderStatus

private const val ARG_PARAM1 = "position"
private const val ARG_PARAM2 = "orderItem"

class OrderDetailFragment : Fragment() {
    private var param1: Int? = null
    private var negoData: Order.OrderItem? = null
    private var listener: OrderDetailPagerAdapter.OnNegoPagerClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getInt(ARG_PARAM1, 0)
            negoData = it.getSerializable(ARG_PARAM2) as Order.OrderItem
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_orderdetail_nego, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }


    private fun initView(){

        negoData?.let { it ->

            if(it.NICE_YN.isY()) tvNice.toVisible() else tvNice.toGone()
            if(it.KIBO_YN.isY()) tvNotation.toVisible() else tvNotation.toGone()
            if(it.PROF_YN.isY()) tvExpert.toVisible() else tvExpert.toGone()

            if(it.PUBLIC_YN.isY()){
                ivSecret.toInvisible()
                tvContentsCount.text = (it.DEAL_QTY ?: 0).toStringNumberFormat()
                tvContentsTotalCount.text = it.FLTN_QTY.toStringNumberFormat()
                tvContentsPrice.text = (it.DEAL_UPRC ?: 0).toStringNumberFormat()
            }else{
                ivSecret.toVisible()
                tvContentsCount.text = R.string.star6.toResString()
                tvContentsTotalCount.text = R.string.star6.toResString()
                tvContentsPrice.text = R.string.star6.toResString()
            }

            layoutAccecpt.enableView(it.orderStatus() != OrderStatusType.NEGOTIATING_NO_REMAIN && it.orderStatus() != OrderStatusType.NONE)

            tvCompanyInfo.setOnClickListener {
                ActivityUtil.startInvestmentActivity(requireActivity())
            }

            layoutAccecpt.setOnClickListener { _ ->
                listener?.onContractRequestClick(it)
            }


        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainCardFragment.
         */
        @JvmStatic
        fun newInstance(param1: Int, param2: Order.OrderItem, listener: OrderDetailPagerAdapter.OnNegoPagerClickListener) =
            OrderDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                }
                this.listener = listener
            }
    }
}
