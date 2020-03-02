package kr.co.koscom.omp.view

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
        val bottom_navigation_view =
            view.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottom_navigation_view.menu.setGroupCheckable(0, false, true)

        for (i in 0 until bottom_navigation_view.menu.size()) {
            bottom_navigation_view.menu.getItem(i).isChecked = false
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.navigation_chat) {
            var intent = Intent(context, ChatListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_orders) {
            var intent = Intent(context, OrderListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_order) {
            var intent = Intent(context, OrderWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_info) {
            var intent = Intent(context, InvestmentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            return true
        } else if (item.itemId == R.id.navigation_mypage) {
            var intent = Intent(context, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            return true
        }

        return true
    }


}