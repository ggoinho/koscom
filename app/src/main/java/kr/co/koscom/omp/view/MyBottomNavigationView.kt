package kr.co.koscom.omp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.co.koscom.omp.*
import kr.co.koscom.omp.util.ActivityUtil

class MyBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener {
    init {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.view_bottom_navigation, null, false)
        addView(view)

        initNavigation(view)
    }

    private fun initNavigation(view: View) {
        val custom_bottom_navigation_view = view.findViewById<BottomNavigationView>(R.id.custom_bottom_navigation_view)

        custom_bottom_navigation_view.menu.setGroupCheckable(0, false, true)

        for (i in 0 until custom_bottom_navigation_view.menu.size()) {
            custom_bottom_navigation_view.menu.getItem(i).isChecked = false
        }

        custom_bottom_navigation_view.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.navigation_chat) {
            ActivityUtil.startChatListActivity(context as Activity)
//            var intent = Intent(context, ChatListActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_orders) {
            ActivityUtil.startOrderListActivity(context as Activity)
//            var intent = Intent(context, OrderListActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_order) {

            ActivityUtil.startOrderRegistActivity(context as Activity)

//            ActivityUtil.startOrderWriteActivity(context as Activity)
//            var intent = Intent(context, OrderWriteActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_info) {
            ActivityUtil.startInvestmentActivity(context as Activity)
//            var intent = Intent(context, InvestmentActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_mypage) {
            ActivityUtil.startMyPageActivity(context as Activity)
//            var intent = Intent(context, MyPageActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
            return true
        }

        return true
    }


}