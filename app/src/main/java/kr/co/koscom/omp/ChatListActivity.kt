package kr.co.koscom.omp

import android.content.Context
import android.content.Intent
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
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_chatlist.*
import kotlinx.android.synthetic.main.list_item_chat_list.*
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import java.util.*
import com.sendbird.android.SendBirdException
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.Member
import com.sendbird.syncmanager.utils.PreferenceUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.time.DateFormatUtils
import java.nio.file.attribute.GroupPrincipal


/**
 * 협상채팅목록
 */

class ChatListActivity : AppCompatActivity() {

    private val mHandler = Handler()

    private lateinit var viewModelFactory: ViewModelFactory

    private val orderedData = arrayListOf<GroupChannel>()

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatlist)

        var loginId = intent.getStringExtra("loginId")
        if(!loginId.isNullOrEmpty()){
            Snackbar.make(container, "LOGIN_ID : " + loginId, Snackbar.LENGTH_SHORT).show();
        }

        viewModelFactory = Injection.provideViewModelFactory(this)

        btnClose.setOnClickListener {
            finish()
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

        listChat!!.layoutManager = LinearLayoutManager(this)
        val horizontalDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        horizontalDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider2)!!)

        listChat!!.addItemDecoration(horizontalDivider)

        listChat!!.adapter = OrderAdapter()
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

        listGroup()
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
            var mCount = itemView.findViewById<AppCompatTextView>(R.id.count)
            var mLastDate = itemView.findViewById<TextView>(R.id.lastDate)
            var mChannelUrl = itemView.findViewById<TextView>(R.id.channelUrl)
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
            holder.mRow.setOnClickListener {
                val intent = Intent(this@ChatListActivity, GroupChannelActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("groupChannelUrl", groupChannel.url)
                intent.putExtra("groupChannelTitle", groupChannel.name)
                intent.putExtra("orderNo", groupChannel.data)
                startActivity(intent)
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

    companion object {
    }
}
