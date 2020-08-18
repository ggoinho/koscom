package kr.co.koscom.omp.adapter.holder

import android.app.Activity
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_main_footer.view.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.base.BaseViewHolder
import kr.co.koscom.omp.constants.Constants
import kr.co.koscom.omp.data.local.MainTotalData
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.util.ActivityUtil

class MainFooterHolder(parent: ViewGroup, private var mainTotalData: MainTotalData) : BaseViewHolder(parent, R.layout.item_main_footer) {

    init {
        initListener()
    }

    override fun onBind(item: Any, position: Int) {
        super.onBind(item, position)

        if(item is MainTotalData){
            mainTotalData = item

            if(item.listMain.isNullOrEmpty()){
                itemView.layoutEmpty.toVisible()
            }else{
                itemView.layoutEmpty.toGone()
            }
        }
    }


    private fun initListener(){
        itemView.tvUseTerms.setOnClickListener {
            ActivityUtil.startWebActivity(context as Activity, R.string.web_title_useterm.toResString(), Constants.URL_POLICY_USETERMS)
        }

        itemView.tvUserInfoPolicy.setOnClickListener {
            ActivityUtil.startWebActivity(context as Activity, R.string.web_title_userinfo_policy.toResString(), Constants.URL_POLICY_USERINFO)
        }

        itemView.tvCopyRightPolicy.setOnClickListener {
            ActivityUtil.startWebActivity(context as Activity, R.string.web_title_copyright_policy.toResString(), Constants.URL_POLICY_COPYRIGHT)
        }
    }
}