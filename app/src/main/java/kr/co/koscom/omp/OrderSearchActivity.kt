package kr.co.koscom.omp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kr.co.koscom.omp.data.viewmodel.OrderViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_search.*
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Stock
import kr.co.koscom.omp.enums.DealType
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.view.ViewUtils
import java.util.*

/**
 * 종목검색
 */

class OrderSearchActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var orderViewModel: OrderViewModel

    private val disposable = CompositeDisposable()

    private val listData = ArrayList<Stock.ResultMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_search)

        btnClose.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)

        }

        btnCloseSearch.setOnClickListener {
            search.setText("")
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        orderViewModel = ViewModelProviders.of(this, viewModelFactory).get(OrderViewModel::class.java)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                listData.clear()
                list!!.adapter?.notifyDataSetChanged()

                when(tab?.position){
                    0 ->{
                        layoutSearch.toVisible()
                        svQuick.toVisible()
                    }
                    else ->{
                        layoutSearch.toGone()
                        svQuick.toGone()
                    }
                }


                search()
            }
        })

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

        search.setOnEditorActionListener { textView, i, keyEvent ->
            Log.d("OrderSearchActivity", "OnEditorActionListener : " + i)

            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    btnSearch.callOnClick()
                }
            }

            return@setOnEditorActionListener false
        }

        btnSearch.setOnClickListener {
            search()
        }

        list!!.layoutManager = LinearLayoutManager(this)
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider2)!!)
        list!!.addItemDecoration(horizontalDivider)

        list!!.adapter = OrderSearchAdapter(listData)

        search()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
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

//        if(tabLayout.selectedTabPosition != 1){type()}else{null},
//        if(tabLayout.selectedTabPosition != 1){search.text.toString()}else{null})
        disposable.add(orderViewModel.searchStock(PreferenceUtils.getUserId(),
            if(tabLayout.selectedTabPosition == 0){"A"}else if(tabLayout.selectedTabPosition == 1){"P"}else{"F"},
            if(tabLayout.selectedTabPosition == 0) type() else null,
            if(tabLayout.selectedTabPosition == 0) search.text.toString() else "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){
                    listData.clear()
                    listData.addAll(it.datas?.resultList!!)
                    list!!.adapter?.notifyDataSetChanged()

                    if(listData.size == 0){
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
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
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)

    }

    internal inner class OrderSearchAdapter(val list: ArrayList<Stock.ResultMap>) :
        RecyclerView.Adapter<OrderSearchAdapter.ViewHolder>() {


        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var code = itemView.findViewById<TextView>(R.id.code)
            var name = itemView.findViewById<TextView>(R.id.name)
            var btnFavorite = itemView.findViewById<AppCompatImageView>(R.id.btnFavorite)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSearchAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_search_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderSearchAdapter.ViewHolder, position: Int) {
            val data = list!![position]

            holder.code.text = data.STK_CODE
            holder.name.text = data.STK_NM
            holder.name.setOnClickListener {
                var intent = Intent()
                intent.putExtra(Keys.INTENT_STOCK, data)
                intent.putExtra(Keys.INTENT_DEAL_TYPE, if(tabLayout.selectedTabPosition == 1){DealType.SELL}else{DealType.BUYING})
//                intent.putExtra("medoYn", if(tabLayout.selectedTabPosition == 1){"Y"}else{"N"})
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
            }

            if(!data.FAV_CORP_NO.isNullOrEmpty()){
                holder.btnFavorite.setImageResource(R.drawable.ico_start_y)
            }
            else{
                holder.btnFavorite.setImageResource(R.drawable.ico_start_g)
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }
    }

    companion object {
    }
}
