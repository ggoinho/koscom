package kr.co.koscom.omp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_contract_confirm.*
import kr.co.koscom.omp.extension.enableView
import kr.co.koscom.omp.extension.toDrawable
import kr.co.koscom.omp.extension.toNumberFormat
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ContractFragment : Fragment() {

    private val numberFormat = DecimalFormat("#,###")
    private val dateFormat = SimpleDateFormat("yyyy. MM. dd. HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contract_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myname.text = null
        hisname.text = null
        count.text = null
        price.text = null
        amount.text = null
        payEndDate.text = null

        btnClose.setOnClickListener {
            ContractDetailActivity.contractDetailActivity?.hideNavigation()
        }

        btnSign.setOnClickListener {
            (activity as ContractDetailActivity).signContract(Runnable {
//                btnSign.visibility = View.INVISIBLE
                mySign.setTextColor(Color.parseColor("#3348ae"))
                mySign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                mySign.text = "서명 완료했습니다"

                btnSign.isEnabled = (!mySign.text.contains("완료"))
                btnSign.enableView(btnSign.isEnabled)
            })
        }

        btnIssue.setOnClickListener {

            if(ContractDetailActivity.contractDetailActivity!!.myDealTp == "10"){
                if(chkConfirm.isChecked){
                    (activity as ContractDetailActivity).requestPaper(){
                        ViewUtils.alertDialog(activity!!, it){}
                    }
                }
                else{
                    ViewUtils.alertDialog(activity!!, "체크박스를 선택하시기 바랍니다."){}
                }
            }
            else{
                (activity as ContractDetailActivity).requestPaper(){
                    ViewUtils.alertDialog(activity!!, it){}
                }
            }
        }

        (activity as ContractDetailActivity).apiListeners.add(object : Runnable{
            override fun run() {

                for (member in (activity as ContractDetailActivity).contract!!.MEMBERSLIST!!){
                    if(PreferenceUtils.getUserId().equals(member.user_id)){
                        mygubnTitle.text = if("order".equals(member.order_req)){"주문게시자"}else{"협상상대자"}
                        myname.text = member.nickname

                        if("10".equals(member.deal_tp)){
                            mygubn.apply {
                                text = "매도"
                                background = R.drawable.shape_rect_fill12.toDrawable()
                                setTextColor(Color.parseColor("#3348ae"))
                            }
                        }else{
                            mygubn.apply {
                                text = "매수"
                                background = R.drawable.shape_rect_fill11.toDrawable()
                                setTextColor(Color.parseColor("#e8055a"))
                            }
                        }

                        mySign.text = if(member.sign_yn == "205"){
                            mySign.setTextColor(Color.parseColor("#3348ae"))
                            mySign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                            "서명 완료했습니다"
                        }else if(member.sign_yn == "204"){"서명 대기중입니다"}else{""}

//                        if(member.sign_yn != "204"){
//                            btnSign.visibility = View.INVISIBLE
//                        }

                        if("10".equals(member.deal_tp)){
                            sellerZone.visibility = View.VISIBLE
                            buyerZone.visibility = View.GONE
                        }else{
                            sellerZone.visibility = View.GONE
                            buyerZone.visibility = View.VISIBLE
                        }

                        ContractDetailActivity.contractDetailActivity!!.myDealTp = member.deal_tp

                        Log.d("ContractFragment", "my deal tp setted : " + member.deal_tp)
                    }
                    else{
                        hisGubnTitle.text = if("order".equals(member.order_req)){"주문게시자"}else{"협상상대자"}
                        hisname.text = member.nickname
                        if("10".equals(member.deal_tp)){
                            hisGubn.apply {
                                text = "매도"
                                background = R.drawable.shape_rect_fill12.toDrawable()
                                setTextColor(Color.parseColor("#3348ae"))
                            }

                        }else{
                            hisGubn.apply {
                                text = "매수"
                                background = R.drawable.shape_rect_fill11.toDrawable()
                                setTextColor(Color.parseColor("#e8055a"))
                            }
                        }

                        hisSign.text = if(member.sign_yn == "205"){
                            hisSign.setTextColor(Color.parseColor("#3348ae"))
                            hisSign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                            "서명 완료했습니다"
                        }else if(member.sign_yn == "204"){"서명 대기중입니다"}else{""}
                    }
                }

                count.text = (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.DEAL_QTY
                count.text = numberFormat.format(count.text.toString().toFloat())
                price.text = (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.PSTK_TRD_AMT
                price.text = numberFormat.format(price.text.toString().toFloat())

                amount.text = (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.TRD_AMT
                amount.text = numberFormat.format(amount.text.toString().toFloat())

                payEndDate.text = (activity as ContractDetailActivity).contract!!.SETT_FIN_DT
                payEndDate.text = StringUtils.substring(payEndDate.text.toString(), 0, 4) +
                        "." + StringUtils.substring(payEndDate.text.toString(), 4, 6) +
                        "." + StringUtils.substring(payEndDate.text.toString(), 6, 8)

                val sellerWarningFormatted = String.format(getString(R.string.seller_warning),
                    (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.TRSF_PSN_NM,
                    (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.ENTP_HANGL_NM,
                    (activity as ContractDetailActivity).contract!!.SEC_KIND_TP_CODE_NM,
                    (activity as ContractDetailActivity).contract!!.HLD_QTY?.toNumberFormat(),
                    (activity as ContractDetailActivity).contract!!.HLD_QTY?.toNumberFormat(),
                    (activity as ContractDetailActivity).contract!!.RESULT_GETTRANDTLINFOVIEW!!.ENTP_HANGL_NM)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sellerWarning.text = Html.fromHtml(sellerWarningFormatted, Html.FROM_HTML_MODE_LEGACY)
                }
                else{
                    sellerWarning.text = Html.fromHtml(sellerWarningFormatted)
                }

                btnSign.isEnabled = false
                if((activity as ContractDetailActivity).contract!!.RESULT_CONFIRMUNISSUEDVO != null){


                    if((activity as ContractDetailActivity).contract!!.RESULT_CONFIRMUNISSUEDVO!!.UNISSUED_STAT_CODE == "01"){
                        rightStatusReady.visibility = View.VISIBLE
                        rightStatusDeny.visibility = View.GONE
                        rightStatusOk.visibility = View.GONE
                    }
                    else if((activity as ContractDetailActivity).contract!!.RESULT_CONFIRMUNISSUEDVO!!.UNISSUED_STAT_CODE == "02"){
                        rightStatusDeny.visibility = View.VISIBLE
                        rightStatusReady.visibility = View.GONE
                        rightStatusOk.visibility = View.GONE
                    }
                    else if((activity as ContractDetailActivity).contract!!.RESULT_CONFIRMUNISSUEDVO!!.UNISSUED_STAT_CODE == "03"){
                        rightStatusOk.visibility = View.VISIBLE
                        rightStatusReady.visibility = View.GONE
                        rightStatusDeny.visibility = View.GONE

                        btnSign.isEnabled = (!mySign.text.contains("완료"))
                    }
                }


                btnSign.enableView(btnSign.isEnabled)

            }
        })

    }

    fun switchZone(tab: Int){
        if(tab == 0){
            contractZone.visibility = View.VISIBLE
            rightZone.visibility = View.GONE
        }
        else if(tab == 1){
            contractZone.visibility = View.GONE
            rightZone.visibility = View.VISIBLE
        }
    }

}
