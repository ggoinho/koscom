package kr.co.koscom.omp.adapter.holder

import android.app.Activity
import android.os.Handler
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.item_main_header.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.MainPagerContractAdapter
import kr.co.koscom.omp.adapter.MainPagerNegoAdapter
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.extension.*
import kr.co.koscom.omp.util.ActivityUtil

class MainHeaderHolder(parent: ViewGroup, private var mainTotalData: MainTotalData, val fm: FragmentManager, val l: Lifecycle) : BaseViewHolder(parent, R.layout.item_main_header) {



    private val adapterNegoViewPager: MainPagerNegoAdapter by lazy{
        MainPagerNegoAdapter(context)
    }
    private val adapterContractViewPager: MainPagerContractAdapter by lazy{
        MainPagerContractAdapter(context)
    }
    /*
    private val adapterNegoViewPager: MainPagerNegoAdapter by lazy{
        MainPagerNegoAdapter(fm, l, object: OnNegoPagerClickListener{
            override fun onRejectClick(item: Any, position: Int) {
                negoClickListener?.onRejectClick(item, position)
            }

            override fun onAcceptClick(item: Any, position: Int) {
                negoClickListener?.onAcceptClick(item, position)
            }
        })
    }
    private val adapterContractViewPager: MainPagerContractAdapter by lazy{
        MainPagerContractAdapter(fm, l, object: OnContractPagerClickListener{
            override fun onContractClick(item: Any, position: Int) {
                contractClickListener?.onContractClick(item, position)
            }

            override fun onConfirmClick(item: Any, position: Int) {
                contractClickListener?.onConfirmClick(item, position)
            }
        })
    }
     */

    private var negoClickListener: OnNegoPagerClickListener? = null
    private var contractClickListener: OnContractPagerClickListener? = null

    private var currentTab = -1
    private var isHiddenView = false


    enum class PAGER_STATUS{
        NEGO,
        CONTRACT,
        EMPTY
    }


    init {
        initView()
        initListener()
    }

    fun onBind(item: Any, position: Int, isRefresh: Boolean) {
        super.onBind(item, position)

        if(item is MainTotalData){
            mainTotalData = item
        }


        with(mainTotalData){

            main?.let {mainData->

                itemView.tvHiddenTopContents.toTextfromHtml(R.string.main_hidden_top_contents.toResString(), mainData.datas?.myNegoList?.size ?: 0, mainData.datas?.myContList?.size ?: 0)

                mainData.datas?.let {
                    if(!it.upBoardList?.CLNT_TTL.isNullOrEmpty()){
                        itemView.tvNotice.text = it.upBoardList?.CLNT_TTL
                        itemView.layoutNoticeZone.toVisible()
                    }else{
                        itemView.layoutNoticeZone.toGone()
                    }
                }

            }
        }

    }

    private fun initView(){


//        LogUtil.e("33.pxToDp() ${33.dpToPx()}")
        itemView.vpNego.apply {
            pageMargin = (-34).dpToPx()
//            setPageTransformer{page, position ->
//                page.translationX = position * -(34.dpToPx())
//            }
            offscreenPageLimit = 2

            adapter = adapterNegoViewPager
        }

        itemView.vpContract.apply {
            pageMargin = (-34).dpToPx()
//            setPageTransformer{page, position ->
//                page.translationX = position * -(34.dpToPx())
//            }
            offscreenPageLimit = 2

            adapter = adapterContractViewPager
        }


//        if(isRefresh){


//        }


        itemView.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if(tab?.position == 1){
                    if(adapterContractViewPager.isEmptyData()){
                        viewPagerViewStatus(PAGER_STATUS.EMPTY)
                    }else{
                        viewPagerViewStatus(PAGER_STATUS.CONTRACT)
                    }
                }else{
                    if(adapterNegoViewPager.isEmptyData()){
                        viewPagerViewStatus(PAGER_STATUS.EMPTY)
                    }else{
                        viewPagerViewStatus(PAGER_STATUS.NEGO)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position?: 0
                if(tab?.position == 1){
                    if(adapterContractViewPager.isEmptyData()){
                        viewPagerViewStatus(PAGER_STATUS.EMPTY)
                    }else{
                        viewPagerViewStatus(PAGER_STATUS.CONTRACT)
                    }
                }else{
                    if(adapterNegoViewPager.isEmptyData()){
                        viewPagerViewStatus(PAGER_STATUS.EMPTY)
                    }else{
                        viewPagerViewStatus(PAGER_STATUS.NEGO)
                    }
                }
            }
        })
    }

    private fun viewPagerViewStatus(type: PAGER_STATUS){
        itemView.layoutHiddenContents.toGone()
        itemView.layoutUpArrow.toVisible()
        itemView.layoutViewPagerFrame.toVisible()
        when(type){
            PAGER_STATUS.NEGO->{
                itemView.vpNego.toVisible()
                itemView.vpContract.toGone()
                itemView.layoutPagerEmpty.toGone()
            }
            PAGER_STATUS.CONTRACT->{
                itemView.vpNego.toGone()
                itemView.vpContract.toVisible()
                itemView.layoutPagerEmpty.toGone()
            }
            PAGER_STATUS.EMPTY->{
                itemView.vpNego.toGone()
                itemView.vpContract.toGone()
                itemView.layoutPagerEmpty.toVisible()
            }
        }



    }

    private fun initListener(){

        itemView.tvNotice.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(context as Activity, 0)
        }


        adapterNegoViewPager.setOnNegoPagerClickListener(object: OnNegoPagerClickListener{
            override fun onRejectClick(item: Any, position: Int) {
                negoClickListener?.onRejectClick(item, position)
            }

            override fun onAcceptClick(item: Any, position: Int) {
                negoClickListener?.onAcceptClick(item, position)
            }
        })

        adapterContractViewPager.setOnContractPagerClickListener(object: OnContractPagerClickListener{
            override fun onContractClick(item: Any, position: Int) {
                contractClickListener?.onContractClick(item, position)
            }

            override fun onConfirmClick(item: Any, position: Int) {
                contractClickListener?.onConfirmClick(item, position)
            }
        })

    }

    fun setAdapterData(data: MainTotalData){
        mainTotalData = data

        adapterNegoViewPager.addAll(data.main?.datas?.myNegoList)
        adapterContractViewPager.addAll(data.main?.datas?.myContList)

        adapterNegoViewPager.notifyDataSetChanged()
        adapterContractViewPager.notifyDataSetChanged()


        if(currentTab<0){
            when{
                adapterNegoViewPager.isEmptyData() && adapterContractViewPager.isEmptyData() ->{
                    itemView.layoutDealPager.toGone()
                }
                adapterNegoViewPager.isEmptyData() ->{
                    itemView.layoutDealPager.toVisible()
                    itemView.tabLayout.getTabAt(1)?.select()
                }
                adapterContractViewPager.isEmptyData() ->{
                    itemView.layoutDealPager.toVisible()
                    itemView.tabLayout.getTabAt(0)?.select()
                }
                else -> {
                    itemView.layoutDealPager.toVisible()
                    itemView.tabLayout.getTabAt(0)?.select()
                }
            }
        }else{
            itemView.layoutDealPager.toVisible()
            itemView.tabLayout.getTabAt(currentTab)?.select()
        }


        itemView.layoutUpArrow.setOnClickListener {
            itemView.layoutViewPagerFrame.toGone()
            itemView.layoutPagerEmpty.toGone()
            itemView.layoutUpArrow.toGone()

            itemView.layoutHiddenContents.toVisible()
        }


    }

    /**
     * 협상관리 리스너 셋팅
     */
    fun setOnNegoPagerClickListener(listener: OnNegoPagerClickListener?){
        negoClickListener = listener
    }

    /**
     * 계약 및 체결관리 리스너 셋팅
     */
    fun setOnContractPagerClickListener(listener: OnContractPagerClickListener?){
        contractClickListener = listener
    }


    /**
     * 협상관리 클릭 리스너
     */
    interface OnNegoPagerClickListener{
        fun onRejectClick(item: Any, position: Int)
        fun onAcceptClick(item: Any, position: Int)
    }

    /**
     * 계약 및 체결관리 클릭 리스너
     */
    interface OnContractPagerClickListener{
        fun onContractClick(item: Any, position: Int)
        fun onConfirmClick(item: Any, position: Int)
    }
}