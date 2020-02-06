package kr.co.koscom.omp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_setting_chat.*

class SettingChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnHide.setOnClickListener {
            GroupChannelActivity.groupChannelActivity.hideNavigation()
        }

        btnExit.setOnClickListener {
            (activity as GroupChannelActivity).exitChannel {
                activity?.finish()
            }
        }

        (activity as GroupChannelActivity).apiListeners.add(object: Runnable{
            override fun run() {

                for (member in (activity as GroupChannelActivity).channel.membersList!!){
                    if(PreferenceUtils.getUserId().equals(member.user_id)){
                        mygubnTitle.text = if("order".equals(member.order_req)){"주문게시자"}else{"협상상대자"}
                        myname.text = member.nickname
                        mygubn.text = if("10".equals(member.deal_tp)){"매도"}else{"매수"}
                        if("10".equals(member.deal_tp)){
                            mygubn.setTextColor(Color.parseColor("#3348ae"))
                            mygubn.setLinkTextColor(Color.parseColor("#3348ae"))
                            mygubn.setBackgroundResource(R.drawable.shape_rect_fill12)
                        }else{
                            mygubn.setTextColor(Color.parseColor("#e8055a"))
                            mygubn.setLinkTextColor(Color.parseColor("#e8055a"))
                            mygubn.setBackgroundResource(R.drawable.shape_rect_fill11)
                        }
                        (activity as GroupChannelActivity).myDealTp = member.deal_tp

                        Log.d("SettingChatFragment", "myDealTp setted : " + member.deal_tp)
                    }
                    else{
                        hisGubnTitle.text = if("order".equals(member.order_req)){"주문게시자"}else{"협상상대자"}
                        hisname.text = member.nickname
                        hisGubn.text = if("10".equals(member.deal_tp)){"매도"}else{"매수"}
                        if("10".equals(member.deal_tp)){
                            hisGubn.setTextColor(Color.parseColor("#3348ae"))
                            hisGubn.setLinkTextColor(Color.parseColor("#3348ae"))
                            hisGubn.setBackgroundResource(R.drawable.shape_rect_fill12)
                        }else{
                            hisGubn.setTextColor(Color.parseColor("#e8055a"))
                            hisGubn.setLinkTextColor(Color.parseColor("#e8055a"))
                            hisGubn.setBackgroundResource(R.drawable.shape_rect_fill11)
                        }
                    }
                }
            }
        })

    }

}
