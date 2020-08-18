package kr.co.koscom.omp.ui.drawerlayout

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sendbird.syncmanager.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_drawer_layout.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.DrawerLeftMenuAdapter
import kr.co.koscom.omp.adapter.DrawerRelationSearchAdapter
import kr.co.koscom.omp.adapter.DrawerRightMenuAdapter
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.CustomerTabType
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.enums.MyPageTabType
import kr.co.koscom.omp.enums.SettingTabType
import kr.co.koscom.omp.extension.hideKeyboard
import kr.co.koscom.omp.extension.toGone
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.ui.drawerlayout.contract.DrawerLayoutContract
import kr.co.koscom.omp.util.ActivityUtil
import kr.co.koscom.omp.util.LogUtil
import kr.co.koscom.omp.view.ViewUtils
import java.util.stream.Collectors

class DrawerLayoutActivity : AppCompatActivity(), DrawerLayoutContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val presenter: DrawerLayoutPresenter by lazy {
        DrawerLayoutPresenter(this)
    }

    val activity: DrawerLayoutActivity by lazy {
        this
    }


    private val adapterLeft: DrawerLeftMenuAdapter by lazy{
        DrawerLeftMenuAdapter(object: DrawerLeftMenuAdapter.OnMenuClickClickListener{
            override fun onMenuClick(position: Int, item: Any) {

                Handler().postDelayed({
                    (rvRightMenu.layoutManager as LinearLayoutManager)?.scrollToPositionWithOffset(position+1, 0)
                    adapterLeft.setSelectedPosition(position)
                    adapterRight.setSelectedPosition(position)
                }, 100)
            }
        })
    }

    private val adapterRight: DrawerRightMenuAdapter by lazy{
        DrawerRightMenuAdapter(object: DrawerRightMenuAdapter.OnMenuClickClickListener{
            override fun onMenuClick(position: Int, item: Any) {
            }

            override fun onNotifyChanged() {
//                adapterRight.notifyItemChanged(0)
                activity.finish()
            }
        })
    }

    private val adapterSearch: DrawerRelationSearchAdapter by lazy{
        DrawerRelationSearchAdapter(object: DrawerRelationSearchAdapter.OnMenuClickClickListener{
            override fun onNotifyChanged() {
                adapterRight.notifyItemChanged(0)
                activity.finish()
            }
        })
    }



    private var listSearchData: MutableList<DrawerMenuData> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_layout)

        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()


    }

    private fun init(){

        tvToolNickName.text = PreferenceUtils.getUserName()

        rvSearchList.apply {
            layoutManager = LinearLayoutManager(this@DrawerLayoutActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterSearch
        }


        rvLeftMenu.apply {
            layoutManager = LinearLayoutManager(this@DrawerLayoutActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterLeft
        }

        rvRightMenu.apply {
            layoutManager = LinearLayoutManager(this@DrawerLayoutActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterRight
        }

        vLeftTopMargin.isSelected = true

        val listLeft = resources.getStringArray(R.array.drawer_main_menu)

        listSearchData.clear()

        for((index, menu) in listLeft.withIndex()){
            //좌측 메뉴리스트
            adapterLeft.addItem(DrawerMenuData().apply {
                title = menu
                isSelectedMenu = index == 0
            })

            val searchSubList = getRightSubMenuList(index)
            //우측 메뉴리스트
            adapterRight.addItem(DrawerMenuData().apply {
                title = menu
                isSelectedMenu = index == 0
                menuType = DrawerMenuType.getType(index)
                listSubMenu = searchSubList
            })


            //검색어 리스트
            for(subItem in searchSubList){
                listSearchData.add(DrawerMenuData().apply {
                    title = subItem.title
                    searchName = String.format(R.string.drawer_search_contents.toResString(), menu, subItem.title)
                })
            }
        }

        adapterLeft.notifyDataSetChanged()
        adapterRight.notifyDataSetChanged()

    }

    private fun initListener(){

        tvToolLogout.setOnClickListener {
            etToolSearch.hideKeyboard()
        }

        rvRightMenu.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                when{
                    (!rvRightMenu.canScrollVertically(1)) -> {
                        adapterLeft.setSelectedPosition(adapterLeft.items.size-1)
                        adapterRight.setSelectedPosition(adapterLeft.items.size-1)
                    }
                    else->{

                        if(firstVisibleItem < 2){
                            if(!vLeftTopMargin.isSelected)
                                vLeftTopMargin.isSelected = true
                        }else{
                            if(vLeftTopMargin.isSelected)
                                vLeftTopMargin.isSelected = false
                        }
                        if(firstVisibleItem<2){
                            adapterLeft.setSelectedPosition(0)
                            adapterRight.setSelectedPosition(0)
                        }else{
                            adapterLeft.setSelectedPosition(firstVisibleItem-1)
                            adapterRight.setSelectedPosition(firstVisibleItem-1)
                        }

                    }
                }
            }
        })



        etToolSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(str.isNullOrEmpty()){
                    layoutSearchListFrame.toGone()
                }else{
                    layoutSearchListFrame.toVisible()

                    val listStream = listSearchData.stream().filter{
                        t-> t.title.contains(str)
                    }.collect(Collectors.toList())

                    adapterSearch.clearList()
                    adapterSearch.notifyDataSetChanged()
                    if(listStream.isNullOrEmpty()){
                        rvSearchList.toGone()
                        tvSearchEmpty.toVisible()
                    }else{
                        rvSearchList.toVisible()
                        tvSearchEmpty.toGone()

                        adapterSearch.searchContents = str.toString()
                        for(streamData in listStream){
                            adapterSearch.addItem(streamData)
//                            layoutSearchList.addView(CustomDrawerSearchView(this@DrawerLayoutActivity).apply {
//                                setDrawerSearchContents(streamData.searchName , str.toString())
//                            })
                        }
                        adapterSearch.notifyDataSetChanged()

                    }


                    /*
                    layoutSearchList.removeAllViews()

                    if(listStream.isNullOrEmpty()){
                        layoutSearchList.toGone()
                        tvSearchEmpty.toVisible()
                    }else{
                        layoutSearchList.toVisible()
                        tvSearchEmpty.toGone()
                        for(streamData in listStream){
                            layoutSearchList.addView(CustomDrawerSearchView(this@DrawerLayoutActivity).apply {
                                setDrawerSearchContents(streamData.searchName , str.toString())
                            })
                        }
                    }
                     */



                }
            }
        })

        etToolSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                LogUtil.e("search : ")

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }

        tvToolLogout.setOnClickListener {
            ViewUtils.alertLogoutDialog(this, R.string.drawer_logout_contents.toResString()){
                ActivityUtil.startCleanLoginActivity(this)
            }
        }

        ivToolAlarm.setOnClickListener {
            ActivityUtil.startMyPageActivity(this, MyPageTabType.ALARM.ordinal)
            finish()
        }

        ivToolSetting.setOnClickListener {
            ActivityUtil.startSettingNewActivity(this, SettingTabType.ALARM.ordinal)
            finish()
        }

        tvToolBottomChat.setOnClickListener {
            ActivityUtil.startChatListActivity(this)
        }

        tvToolBottomChat.setOnClickListener {
            ActivityUtil.startChatListActivity(this)
        }

        tvToolBottomService.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(this, CustomerTabType.SERVICE.ordinal)
        }

        tvToolBottomOneAndOne.setOnClickListener {
            ActivityUtil.startCustomerCenterActivity(this, CustomerTabType.ONE_AND_ONE.ordinal)
        }

        tvToolBottomQuick.setOnClickListener {
            ActivityUtil.startQuickMenuActivity(this)
        }

    }


    /**
     * 우측 서브 메뉴 리스트
     */
    private fun getRightSubMenuList(index: Int): MutableList<DrawerMenuData>{

        var returnList = arrayListOf<DrawerMenuData>()
        val array = getRightSubMenu(index)

        for(menu in array){
            returnList.add(DrawerMenuData().apply {
                menuType = DrawerMenuType.getType(index)
                title = menu
            })
        }
        return returnList
    }

    /**
     * 0: 주문
     * 1: 종목정보
     * 2: My Page
     * 3: 고객센터
     * 4: 설정
     */
    private fun getRightSubMenu(index: Int): Array<String>{
        return when(index){
            DrawerMenuType.ORDER.type->{
                resources.getStringArray(R.array.drawer_order)
            }
            DrawerMenuType.STOCK_INFO.type->{
                resources.getStringArray(R.array.drawer_stock_info)
            }
            DrawerMenuType.MYPAGE.type->{
                resources.getStringArray(R.array.drawer_mypage)
            }
            DrawerMenuType.CUSTOMER_CENTER.type->{
                resources.getStringArray(R.array.drawer_customer)
            }
            DrawerMenuType.SETTING.type->{
                resources.getStringArray(R.array.drawer_setting)
            }
            else ->{
                resources.getStringArray(R.array.drawer_order)
            }
        }
    }


    private fun loadingClose(){
    }


    /**
     * Loading Progress
     */
    override fun showProgress(isShow: Boolean){
        if(isShow){
//            progress_bar_login.toVisible()
        }else{
//            progress_bar_login.toInvisible()
        }
    }

    /**
     * Alert 다이얼로그
     */
    override fun alertDialog(msg: String){
        ViewUtils.alertDialog(this, msg){
        }
    }

    /**
     * Error Alert
     */
    override fun showErrorMsg(code: String?, msg: String?) {
        ViewUtils.showErrorMsg(this, code, msg)
    }



    override fun onDestroy() {
        presenter.detachView()

        super.onDestroy()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
    }




}
