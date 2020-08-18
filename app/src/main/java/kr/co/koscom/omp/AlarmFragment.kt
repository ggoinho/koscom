/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.koscom.omp

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scsoft.boribori.data.viewmodel.AlarmViewModel
import com.scsoft.boribori.data.viewmodel.CodeViewModel
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chatlist.view.*
import kotlinx.android.synthetic.main.fragment_alarm.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.model.Alarm
import kr.co.koscom.omp.data.model.Code
import kr.co.koscom.omp.extension.enableView
import kr.co.koscom.omp.view.PaginationListener
import kr.co.koscom.omp.view.ViewUtils
import java.util.*


/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * [androidx.viewpager.widget.ViewPager].
 */
class AlarmFragment : Fragment() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var codeViewModel: CodeViewModel

    private val disposable = CompositeDisposable()

    private val PAGE_SIZE = 30
    private var loading = false
    private var lastPage = false
    private val listData = ArrayList<Alarm.AlarmItem>()
    private val codeList = ArrayList<Code.CodeItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.d(AlarmFragment::class.simpleName, "onCreateView()")
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(AlarmFragment::class.simpleName, "onViewCreated()")

        viewModelFactory = Injection.provideViewModelFactory(activity!!)
        alarmViewModel = ViewModelProviders.of(this, viewModelFactory).get(AlarmViewModel::class.java)
        codeViewModel = ViewModelProviders.of(this, viewModelFactory).get(CodeViewModel::class.java)

        btnTop.setOnClickListener {
            alarmList.scrollToPosition(0)
        }

        alarmList!!.layoutManager = LinearLayoutManager(activity)
        val horizontalDivider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(activity as AppCompatActivity, R.drawable.my_divider3)!!)

        alarmList!!.addItemDecoration(horizontalDivider)

        alarmList!!.adapter = AlarmAdapter()
        alarmList!!.addOnScrollListener(object : PaginationListener(alarmList!!.layoutManager!! as LinearLayoutManager, PAGE_SIZE) {
            override fun loadMoreItems() {
                loading = true
                search()
            }

            override fun isLastPage(): Boolean {
                return lastPage;
            }

            override fun isLoading(): Boolean {
                return loading;
            }

            override fun viewItem(itemIndex: Int) {
                Log.d(AlarmFragment::class.simpleName, "viewItem($itemIndex)")
            }
        });

        readAll.setOnClickListener {
            updateRead("all")
            listData.clear();
            search()
        }

        listView.setOnClickListener {
            var alertBuilder = AlertDialog.Builder(activity!!)

            var adapter = ArrayAdapter<String>(activity!!, android.R.layout.select_dialog_singlechoice);
            adapter.add("전체")

            var selected = 0
            for((index, code) in codeList.withIndex()){
                adapter.add(code.ATTR_NM)

                if(code.ATTR_NM == codeName.text){
                    selected = index + 1
                }
            }

            alertBuilder.setSingleChoiceItems(adapter, selected){dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()

                listData.clear()

                if(i > 0){

                    Log.d(AlarmFragment::class.simpleName, "${codeList.get(i - 1).ATTR_NM} == ${codeList.get(i - 1).ATTR_CODE}")

                    codeName.text = codeList.get(i - 1).ATTR_NM

                    search(codeList.get(i - 1).ATTR_CODE)
                }
                else{
                    codeName.text = "전체"

                    search()
                }
            }
            alertBuilder.show();

        }

        searchCode()
        search()
    }

    private fun updateRead(seqno: String){

        disposable.add(alarmViewModel.updateRead(PreferenceUtils.getUserId(), seqno)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    (activity as MyPageActivity).refreshAlarmCount()

                }else{
                    ViewUtils.showErrorMsg(activity!!, it.rCode, it.rMsg)

                }
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun searchCode(){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(codeViewModel.searchCode("0052")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.d(LoginActivity::class.simpleName, "response : " + it)

                if("0000".equals(it.rCode)){

                    if(it.datas!!.resultList != null){
                        codeList.addAll(it.datas!!.resultList!!)
                    }

                }else{
                    ViewUtils.showErrorMsg(activity!!, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            }))
    }

    private fun search(code: String? = null){
        progress_bar_login.visibility = View.VISIBLE

        disposable.add(alarmViewModel.searchList(PreferenceUtils.getUserId(), listData.size, PAGE_SIZE, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map{
                searchAlarm()
                return@map it
            }
            .subscribe({
                Log.d(LoginActivity::class.simpleName, "response(${it.datas?.resultList!!.size}) : " + it)

                if("0000".equals(it.rCode)){

                    listData.addAll(it.datas?.resultList!!)
                    alarmList!!.adapter?.notifyDataSetChanged()

                    if(it.datas?.resultList!!.size < PAGE_SIZE){
                        lastPage = true
                    }

                    loading = false

                    if(listData.size == 0){
                        nothing.visibility = View.VISIBLE
                    }
                    else{
                        nothing.visibility = View.GONE
                    }

                }else{
                    ViewUtils.showErrorMsg(activity!!, it.rCode, it.rMsg)

                }
                progress_bar_login.visibility = View.INVISIBLE
            }, {
                progress_bar_login.visibility = View.INVISIBLE
                it.printStackTrace()
                ViewUtils.alertDialog(activity!!, "네트워크상태를 확인해주세요."){}
            }))
    }


    /**
     * Alarm 안읽은 메시지 카운트
     */
    fun searchAlarm() {
        if(!PreferenceUtils.getUserId().isNullOrEmpty()){
            disposable.add(alarmViewModel.searchNotReadCount(PreferenceUtils.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Log.d(LoginActivity::class.simpleName, "response : " + it)

                    if("0000".equals(it.rCode)){
                        it.datas?.let {data->
                            if(0 < data.result?: 0){
                                readAll.enableView(true)
                            }else{
                                readAll.enableView(false)
                            }
                        }?: run{
                            readAll.enableView(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                }))
        }
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    internal inner class AlarmAdapter() :
        RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var gubnNotice = itemView.findViewById<LinearLayoutCompat>(R.id.gubnNotice)
            var gubnMessage = itemView.findViewById<LinearLayoutCompat>(R.id.gubnMessage)
            var gubn = itemView.findViewById<TextView>(R.id.gubn)
            var date = itemView.findViewById<TextView>(R.id.date)
            var readYn = itemView.findViewById<TextView>(R.id.readYn)
            var message = itemView.findViewById<TextView>(R.id.message)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmAdapter.ViewHolder {
            val context = parent.context
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_alarm_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: AlarmAdapter.ViewHolder, position: Int) {
            val data = listData[position]
            Log.d("AlarmFragment", "data : " + data)

            holder.gubnNotice.visibility = View.GONE
            holder.gubnMessage.visibility = View.VISIBLE
            holder.gubn.text = data.ANC_TP_NAME
            holder.date.text = data.REG_DTTM_FORM
            holder.readYn.text = data.ANC_YN
            holder.message.text = data.ANC_CNTS

            if(data.ANC_YN != "Y"){
                updateRead(data.SEQNO!!)
            }

            /*if(!data.LANDING_URL.isNullOrEmpty()){
                holder.mRow.setOnClickListener {
                    var intent = Intent(activity, WebActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("title", data.ANC_CNTS)
                    intent.putExtra("url", BuildConfig.SERVER_URL + data.LANDING_URL)
                    startActivity(intent)
                }
            }
            else{
                holder.mRow.setOnClickListener(null)
            }*/
        }

        override fun getItemCount(): Int {
            return listData.size
        }
    }
}
