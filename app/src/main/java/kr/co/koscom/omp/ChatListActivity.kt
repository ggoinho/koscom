package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.scsoft.boribori.data.viewmodel.ChatViewModel
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.Member
import com.sendbird.android.SendBird
import com.sendbird.syncmanager.ConnectionManager
import com.sendbird.syncmanager.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chatlist.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.view.ViewUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.time.DateFormatUtils


/**
 * 협상채팅목록
 */

class ChatListActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val orderedData = arrayListOf<GroupChannel>()

    private val disposable = CompositeDisposable()
    private lateinit var chatViewModel: ChatViewModel

    var isFirstComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatlist)

        var loginId = intent.getStringExtra("loginId")
        if(!loginId.isNullOrEmpty()){
            Snackbar.make(container, "LOGIN_ID : " + loginId, Snackbar.LENGTH_SHORT).show();
        }

        viewModelFactory = Injection.provideViewModelFactory(this)
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)

        btnClose.setOnClickListener {
            onBackPressed()
        }

        btnSearch.setOnClickListener {
            defaultToolbar.visibility = View.GONE
            searchToolbar.visibility = View.VISIBLE

            inputSearch.setFocusableInTouchMode(true)
            inputSearch.requestFocus();
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(inputSearch,0);
        }

        backSearch.setOnClickListener {
            defaultToolbar.visibility = View.VISIBLE
            searchToolbar.visibility = View.GONE

            inputSearch.setText("")
            listGroup()
        }

        goSearch.setOnClickListener {
            listGroup()
        }

        clearSearch.setOnClickListener {
            inputSearch.setText("")
        }

        inputSearch.setOnEditorActionListener { textView, i, keyEvent ->
            Log.d(ChatListActivity::class.simpleName, "OnEditorActionListener : " + i)

            when (i) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    listGroup()
                }
            }

            return@setOnEditorActionListener false
        }

        initChatLogin()
    }

    private fun initChatLogin(){

        val userId = PreferenceUtils.getUserId()
        chatLogin(userId) {accessToken : String ->

            ConnectionManager.login(userId,accessToken) { user, e ->

                if (e != null) {
                    Snackbar.make(container, "chatting login failed", Snackbar.LENGTH_LONG).show();

                    e.printStackTrace()
                    PreferenceUtils.setConnected(false)
                } else {

                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                        Log.d(ChatListActivity::class.java.simpleName,"firebase token : " + instanceIdResult.token)

                        Preference.setPushToken(this, instanceIdResult.token)

                        registToken(userId, instanceIdResult.token, object: Runnable{
                            override fun run() {
                                Log.d(ChatListActivity::class.java.simpleName,"registerPushTokenForCurrentUser : " + instanceIdResult.token)

                                //sendPush(userId, object: Runnable{
                                //    override fun run() {

                                SendBird.registerPushTokenForCurrentUser(instanceIdResult.token,
                                    SendBird.RegisterPushTokenWithStatusHandler { pushTokenRegistrationStatus, e ->
                                        if (e != null) {

                                            //Toast.makeText(applicationContext, "registerPushTokenForCurrentUser failed",Toast.LENGTH_LONG).show()

                                            e.printStackTrace()
                                            return@RegisterPushTokenWithStatusHandler
                                        }

                                        if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                                            //Toast.makeText(applicationContext, "Connection required to register push token.", Toast.LENGTH_LONG).show()

                                            Log.d(ChatListActivity::class.java.simpleName, "Connection required to register push token.")
                                        } else {
                                            Log.d(ChatListActivity::class.java.simpleName, "registerPushTokenForCurrentUser success.")
                                        }
                                    })


                                PreferenceUtils.setConnected(true)
                                // Update the user's nickname
                                /*SendBird.updateCurrentUserInfo(chatId.text.toString(), null,
                                                SendBird.UserInfoUpdateHandler { e ->
                                                    if (e != null) {
                                                        e.printStackTrace()
                                                        // Error!
                                                        Snackbar.make(window.decorView, "Update user nickname failed", Snackbar.LENGTH_LONG).show();

                                                        return@UserInfoUpdateHandler
                                                    }

                                                    PreferenceUtils.setUserId(chatId.text.toString())
                                                    //PreferenceUtils.setNickname(chatId.text.toString())
                                                })*/


                                listChat!!.layoutManager = LinearLayoutManager(this@ChatListActivity)
                                val horizontalDivider = DividerItemDecoration(this@ChatListActivity, DividerItemDecoration.VERTICAL)
                                horizontalDivider.setDrawable(ContextCompat.getDrawable(this@ChatListActivity, R.drawable.my_divider2)!!)

                                listChat!!.addItemDecoration(horizontalDivider)

                                listChat!!.adapter = OrderAdapter()
                                //    }
                                //})

                                isFirstComplete = true
                                if(intent.getBooleanExtra("detailMove", false)){

                                    ActivityUtil.startGroupChannelActivity(this@ChatListActivity, intent.getStringExtra("groupChannelUrl"), intent.getStringExtra("groupChannelTitle"), intent.getStringExtra("orderNo"))

//                                    Intent(this@ChatListActivity, GroupChannelActivity::class.java).apply {
//                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                        putExtra("groupChannelUrl", intent.getStringExtra("groupChannelUrl"))
//                                        putExtra("groupChannelTitle", intent.getStringExtra("groupChannelTitle"))
//                                        putExtra("orderNo", intent.getStringExtra("orderNo"))
//                                        startActivity(this)
//                                    }
                                }else{
                                    listGroup()

                                }
                            }
                        })


                    }

                }
            }
        }
    }

    private fun chatLogin(chatId: String, listener: (accessToken : String) -> Unit){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.login(chatId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(ChatListActivity::class.java.simpleName,"chatLogin : success")

                    if(listener != null){
                        listener.invoke(it.ACCESS_TOKEN!!)
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

    private fun registToken(userId: String, deviceToken: String, listener: Runnable?){
        progress_bar_login.visibility = View.VISIBLE
        disposable.add(chatViewModel.registPushToken(userId, deviceToken, Preference.getPushYn(this) == "Y", Preference.getAdYn(this) == "Y")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if("0000".equals(it.rCode)){
                    Log.d(ChatListActivity::class.java.simpleName,"registPushToken : success")
                }

                if(listener != null){
                    listener.run()
                }

                progress_bar_login.visibility = View.INVISIBLE
            }, {
                it.printStackTrace()

                if(listener != null){
                    listener.run()
                }
            }))
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getCurrentFocus() != null) {
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()!!.getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }

    override fun onResume() {
        super.onResume()

        if(isFirstComplete){
            listGroup()
        }

    }

    private fun listGroup(){
        Log.d("ChatListActivity", "listGroup(${inputSearch.text.toString()})")

        val channelListQuery = GroupChannel.createMyGroupChannelListQuery()
        channelListQuery.isIncludeEmpty = true
        channelListQuery.channelNameContainsFilter = inputSearch.text.toString()
        channelListQuery.next(GroupChannelListQuery.GroupChannelListQueryResultHandler { list, e ->
            if (e != null) {    // Error.
                e.printStackTrace()
                return@GroupChannelListQueryResultHandler
            }
            else{
                Log.d(ChatListActivity::class.simpleName, "group list("+list.size+") : ")

                orderedData.clear()

                if(list.size > 0){
                    for (item in list){
                        Log.d(ChatListActivity::class.simpleName, "group item : " + ToStringBuilder.reflectionToString(item))
                    }

                    orderedData.addAll(list)
                    listChat.adapter?.notifyDataSetChanged()

                    emptyZone.visibility = View.GONE
                    dataZone.visibility = View.VISIBLE
                }
                else{
                    emptyZone.visibility = View.VISIBLE
                    dataZone.visibility = View.GONE
                }
            }
        })
    }

    internal inner class OrderAdapter() : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

        inner class ViewHolder
        internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            var mRow = itemView.findViewById<LinearLayout>(R.id.llRow)
            var mProfileImage = itemView.findViewById<AppCompatImageView>(R.id.profileImage)
            var mUserid = itemView.findViewById<TextView>(R.id.userid)
            var mNicname = itemView.findViewById<TextView>(R.id.nicname)
            var mCount = itemView.findViewById<TextView>(R.id.count)
            var mLastDate = itemView.findViewById<TextView>(R.id.lastDate)
            var mChannelUrl = itemView.findViewById<TextView>(R.id.channelUrl)
            var ivSecret = itemView.findViewById<ImageView>(R.id.ivSecret)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
            val context = parent.context
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = inflater.inflate(R.layout.list_item_chat_list, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
            val groupChannel = orderedData!![position]

            var topMember: Member? = null
            for(member in groupChannel.members){
                Log.d(ChatListActivity::class.simpleName, "reflectionToString(member) : " + ToStringBuilder.reflectionToString(member))
                if(member.userId != PreferenceUtils.getUserId()){
                    topMember = member
                    break
                }
            }

            topMember?.let {
                Glide.with(holder.mProfileImage)
                    .load(it.profileUrl).transition(GenericTransitionOptions.with(R.anim.load_image))
                    .into(holder.mProfileImage)

                holder.mUserid.text = it.nickname ?: ""
            }
            holder.mNicname.text = groupChannel.name

            holder.mCount.text = groupChannel.unreadMessageCount.toString()
            if(groupChannel.unreadMessageCount > 0){
                holder.mCount.visibility = View.VISIBLE
            }
            else{
                holder.mCount.visibility = View.INVISIBLE
            }

            holder.mChannelUrl.text = groupChannel.url
            if(groupChannel.lastMessage != null){
                var lastDate = groupChannel.lastMessage.createdAt
                if(lastDate < groupChannel.lastMessage.updatedAt){
                    lastDate = groupChannel.lastMessage.updatedAt
                }
                holder.mLastDate.text = DateFormatUtils.format(lastDate, "yyyy-MM-dd HH:mm")
            }

            var isPublic = true
            val split = groupChannel.data.split("|").toTypedArray()
            split?.let {
                isPublic = when{
                    1 < it.size ->{
                        it[1] != "N"
                    }
                    else ->{
                        true
                    }
                }
            }
            if(isPublic) holder.ivSecret.toGone() else holder.ivSecret.toVisible()


            holder.mRow.setOnClickListener {

                val split = groupChannel.data.split("|").toTypedArray()
                var orderNo = if(split != null && split.isNotEmpty()){
                    split[0]
                }else{
                    ""
                }

                ActivityUtil.startGroupChannelActivity(this@ChatListActivity, groupChannel.url, groupChannel.name, orderNo)

//                val intent = Intent(this@ChatListActivity, GroupChannelActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("groupChannelUrl", groupChannel.url)
//                intent.putExtra("groupChannelTitle", groupChannel.name)
//                intent.putExtra("orderNo", orderNo)
//                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return orderedData!!.size
        }
    }

    override fun onStop() {
        super.onStop()

        disposable.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)

    }

    companion object {
    }
}
