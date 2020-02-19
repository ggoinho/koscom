package kr.co.koscom.omp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.scsoft.boribori.data.viewmodel.OrderViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search2.*
import kotlinx.android.synthetic.main.activity_search2.btnClose
import kotlinx.android.synthetic.main.activity_search2.btnCloseSearch
import kotlinx.android.synthetic.main.activity_search2.btnSearch
import kotlinx.android.synthetic.main.activity_search2.nothing
import kotlinx.android.synthetic.main.activity_search2.progress_bar_login
import kotlinx.android.synthetic.main.activity_search2.quick
import kotlinx.android.synthetic.main.activity_search2.search
import kotlinx.android.synthetic.main.activity_search2.tabLayout
import kotlinx.android.synthetic.main.list_item_recent_list.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * 종목검색
 */

class Search2Activity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private val listData = ArrayList<String>()
    private val searchData = ArrayList<Stock.ResultMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search2)

        btnClose.setOnClickListener {
            finish()
        }

        btnCloseSearch.setOnClickListener {
            search.setText("")
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        for (i in 0 until tabLayout.tabCount) {

            val tab = tabLayout.getTabAt(i)
            if (tab != null) {

                val tabTextView = TextView(this)
                tab.customView = tabTextView

                tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

                tabTextView.text = tab.text
                tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD)
                }

            }

        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text = tab?.customView as TextView?

                text?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                search()

                val text = tab?.customView as TextView?

                text?.setTypeface(null, Typeface.BOLD)
            }
        })

        search.setOnEditorActionListener { textView, i, keyEvent ->
            Log.d("Search2Activity", "OnEditorActionListener : " + i)

            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    btnSearch.callOnClick()
                }
            }

            return@setOnEditorActionListener false
        }

        btnSearch.setOnClickListener {

            if(!search.text.toString().isNullOrEmpty()){
                Preference.addRecentStock(this, search.text.toString())
            }

            search()
        }

        quick.post {
            var quickItems = listOf("가", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하", "A-Z")
            for((index, item) in quickItems.withIndex()){
                var layout = layoutInflater.inflate(R.layout.view_searchtext, null) as LinearLayout
                var textView = layout.getChildAt(0) as TextView
                textView.setOnClickListener {
                    var preStatus = textView.tag

                    for(index in (0 .. quick.childCount-1)){
                        val tv = quick.getChildAt(index).findViewById<TextView>(R.id.word)
                        tv.setTextColor(Color.parseColor("#666666"))
                        tv.setBackgroundColor(Color.parseColor("#f2f2f2"))

                        tv.tag = 0
                    }

                    if(preStatus != 1){
                        textView.setTextColor(Color.parseColor("#ffffff"))
                        textView.setBackgroundColor(Color.parseColor("#484d62"))

                        textView.tag = 1
                    }

                    search()
                }
                textView.text = item
                quick.addView(layout)
            }
        }

        recentList!!.layoutManager = LinearLayoutManager(this)
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider2)!!)
        recentList!!.addItemDecoration(horizontalDivider)

        recentList!!.adapter = RecentAdapter(listData)

        searchList!!.layoutManager = LinearLayoutManager(this)
        searchList!!.addItemDecoration(horizontalDivider)

        searchList!!.adapter = SearchAdapter(searchData)

        loadRecentList()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun loadRecentList(){
        var recentStock = Preference.getRecentStock(this)
        Log.d("RecentList", "loadRecentList : $recentStock")
        if(!recentStock.isNullOrEmpty()){
            listData.clear()
            listData.addAll(StringUtils.split(recentStock, ","))
            Log.d("RecentList", "loadRecentList2 : $listData")
            recentList!!.adapter!!.notifyDataSetChanged()
        }
        else{
            listData.clear()
            recentList!!.adapter!!.notifyDataSetChanged()
        }
    }

    private fun type(): String?{
        for(index in (0 .. quick.childCount-1)){
            var textView = quick.getChildAt(index).findViewById<TextView>(R.id.word)
            if(textView.tag == 1){
                return index.toString()
            }
        }

        return null
    }

    private fun search(){
        progress_bar_login.visibility = View.VISIBLE

        recentZone.visibility = View.GONE
        searchZone.visibility = View.VISIBLE

        disposable.add(orderViewModel.searchStock(
            PreferenceUtils.getUserId(),
            if(tabLayout.selectedTabPosition == 0){"A"}else{"F"},
            type(),
            search.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){
                    searchData.clear()
                    searchData.addAll(it.datas?.resultList!!)
                    searchList!!.adapter?.notifyDataSetChanged()

                    if(searchData.size == 0){
                        nothing.visibility = View.VISIBLE
                    }
                    else{
                        nothing.visibility = View.GONE
                    }

                }else{
                    ViewUtils.showErrorMsg(this, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(this, "네트워크상태를 확인해주세요."){}
            }))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    internal inner class RecentAdapter(val list: ArrayList<String>) :
        RecyclerView.Adapter<RecentAdapter.ViewHolder>() {


        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var word = itemView.findViewById<TextView>(R.id.word)
            var btnRemove = itemView.findViewById<LinearLayoutCompat>(R.id.btnRemove)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_recent_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecentAdapter.ViewHolder, position: Int) {
            val data = list[position]

            holder.word.text = data
            holder.word.setOnClickListener {
                search.setText(data)
                btnSearch.callOnClick()
            }
            holder.btnRemove.setOnClickListener {
                Preference.removeRecentStock(this@Search2Activity, position)
                loadRecentList()
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    internal inner class SearchAdapter(val list: ArrayList<Stock.ResultMap>) :
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var code = itemView.findViewById<TextView>(R.id.code)
            var name = itemView.findViewById<TextView>(R.id.name)
            var btnFavorite = itemView.findViewById<AppCompatImageView>(R.id.btnFavorite)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_search_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
            val data = list[position]

            holder.code.text = data.STK_CODE
            holder.name.text = data.STK_NM
            holder.name.setOnClickListener {
                var intent = Intent()
                intent.putExtra("stock", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            if(!data.FAV_CORP_NO.isNullOrEmpty()){
                holder.btnFavorite.setImageResource(R.drawable.ico_start_y)
            }
            else{
                holder.btnFavorite.setImageResource(R.drawable.ico_start_g)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    companion object
}
