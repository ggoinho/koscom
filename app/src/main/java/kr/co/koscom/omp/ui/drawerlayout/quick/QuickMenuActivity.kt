package kr.co.koscom.omp.ui.drawerlayout.quick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.koscom.omp.util.KosSharedPreferences
import kotlinx.android.synthetic.main.activity_order_regist.*
import kotlinx.android.synthetic.main.activity_quick_menu.*
import kotlinx.android.synthetic.main.toolbar_back.tvToolbarTitle
import kotlinx.android.synthetic.main.toolbar_close.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.QuickLeftMenuAdapter
import kr.co.koscom.omp.adapter.QuickRightMenuAdapter
import kr.co.koscom.omp.adapter.holder.QuickRightViewHolder
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.custom.QuickItemTouchHelperCallback
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.enums.HomeContentsType
import kr.co.koscom.omp.enums.MenuLocationType
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.ui.drawerlayout.quick.contract.QuickMenuContract
import kr.co.koscom.omp.util.KosUtil
import kr.co.koscom.omp.view.ViewUtils
import java.util.ArrayList

class QuickMenuActivity : AppCompatActivity(), QuickMenuContract.View {
    internal val tag = this.javaClass.simpleName

    private lateinit var viewModelFactory: ViewModelFactory

    private val presenter: QuickMenuPresenter by lazy {
        QuickMenuPresenter(this)
    }

    var itemTouchHelper: ItemTouchHelper? = null

    private val adapterLeft: QuickLeftMenuAdapter by lazy{
        QuickLeftMenuAdapter(object: QuickLeftMenuAdapter.OnMenuClickClickListener{
            override fun onMenuClick(position: Int, item: Any) {

                if(item is DrawerMenuData){
                    val itemLeft = adapterLeft.getList()[item.menuType.type]
                    if(itemLeft.listSubMenu[item.childPosition].isSelectedMenu){
                        //제거
                        itemLeft.listSubMenu[item.childPosition].isSelectedMenu = false
                        adapterRight.removeItem(itemLeft.listSubMenu[item.childPosition])
                    }else{
                        //추가
                        if(adapterRight.getList().size>9){
                            alertDialog(R.string.qucikmenu_count_alert_contents.toResString())
                            return
                        }

                        itemLeft.listSubMenu[item.childPosition].isSelectedMenu = true
                        adapterRight.addItem(itemLeft.listSubMenu[item.childPosition])
                    }
                }

                adapterLeft.notifyDataSetChanged()
                adapterRight.notifyDataSetChanged()
                getCurrentCount()
            }
        })
    }

    private val adapterRight: QuickRightMenuAdapter by lazy{
        QuickRightMenuAdapter(object: QuickRightMenuAdapter.OnMenuClickClickListener{
            override fun onMenuClick(position: Int, item: Any) {

                if(item is DrawerMenuData){
                    val itemLeft = adapterLeft.getList()[item.menuType.type]
                    itemLeft.listSubMenu[item.childPosition].isSelectedMenu = false
                    adapterRight.removeItem(item)
                    adapterLeft.notifyDataSetChanged()
                    adapterRight.notifyDataSetChanged()
                    getCurrentCount()
                }
            }
        }, object: QuickRightMenuAdapter.OnStartDragListener{
            override fun onStartDrag(holder: QuickRightViewHolder) {
                itemTouchHelper?.startDrag(holder)
            }

            override fun onEndDrag(holder: QuickRightViewHolder) {
                adapterRight.notifyDataSetChanged()
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_menu)


        viewModelFactory = Injection.provideViewModelFactory(this)

        init()
        initListener()

    }

    private fun init(){

        tvToolbarTitle.text = R.string.qucikmenu_title.toResString()


        rvLeft.apply {
            layoutManager = LinearLayoutManager(this@QuickMenuActivity)
            itemAnimator = DefaultItemAnimator()

            adapter = adapterLeft
        }

        rvRight.apply {
            layoutManager = LinearLayoutManager(this@QuickMenuActivity)
            itemAnimator = DefaultItemAnimator()

            val callback = QuickItemTouchHelperCallback(adapterRight)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper?.attachToRecyclerView(this)

            adapter = adapterRight
        }

        initLeftList()
        resetData()
    }

    private fun initListener(){

        layoutClose.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
        }

        tvAllRemove.setOnClickListener {
            adapterRight.clearList()
            adapterRight.notifyDataSetChanged()

            initLeftList()
            getCurrentCount()
        }

        tvReset.setOnClickListener {
            resetData()
        }

        tvComplete.setOnClickListener {

            if(adapterRight.getList().size < 5 || 10 < adapterRight.getList().size){
                alertDialog(R.string.qucikmenu_count_alert_contents.toResString())
                return@setOnClickListener
            }

            if(tvMenuLocationLeft.isSelected){
                KosSharedPreferences(this)
                    .setInt(Keys.KEY_MENU_LOCAION, MenuLocationType.MENU_LEFT.type)
            }else{
                KosSharedPreferences(this)
                    .setInt(Keys.KEY_MENU_LOCAION, MenuLocationType.MENU_RIGHT.type)
            }

            if(ivHomeBtnExposure.isSelected){
                KosSharedPreferences(this)
                    .setBoolean(Keys.KEY_HOMEBTN_EXPOSURE, true)
            }else{
                KosSharedPreferences(this)
                    .setBoolean(Keys.KEY_HOMEBTN_EXPOSURE, false)
            }

            if(tvOrderStatusSelect.isSelected){
                KosSharedPreferences(this)
                    .setInt(Keys.KEY_HOME_CONTENTS, HomeContentsType.ORDER_SATUS.type)
            }else{
                KosSharedPreferences(this)
                    .setInt(Keys.KEY_HOME_CONTENTS, HomeContentsType.BUSINESS_INFO.type)
            }

            //퀵 메뉴리스트 수정.
            KosSharedPreferences(this).setString(Keys.KEY_QUICK_MENULIST, Gson().toJson(adapterRight.getList()))

            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)

        }


        tvMenuLocationLeft.setOnClickListener {
            onClickMenuLocation(MenuLocationType.MENU_LEFT)
        }
        tvMenuLocationRight.setOnClickListener {
            onClickMenuLocation(MenuLocationType.MENU_RIGHT)
        }

        tvOrderStatusSelect.setOnClickListener {
            onClickHomeContents(HomeContentsType.ORDER_SATUS)
        }

        tvBusinessInfoSelect.setOnClickListener {
            onClickHomeContents(HomeContentsType.BUSINESS_INFO)
        }

        ivHomeBtnExposure.setOnClickListener {
            ivHomeBtnExposure.isSelected = !ivHomeBtnExposure.isSelected
        }


    }


    /**
     * 데이터 초기화
     */
    private fun resetData(){
        onClickMenuLocation(getMenuLocation())
        ivHomeBtnExposure.isSelected = KosSharedPreferences(this).getBoolean(Keys.KEY_HOMEBTN_EXPOSURE)
        onClickHomeContents(getHomeContents())


        initRightList()
        syncLeftRight()

        getCurrentCount()
    }

    /**
     * 메뉴 위치 가져오기
     */
    private fun getMenuLocation(): MenuLocationType{
        return MenuLocationType.getType(KosSharedPreferences(this).getInt(Keys.KEY_MENU_LOCAION))
    }

    /**
     * 홈내용 가져오기
     */
    private fun getHomeContents(): HomeContentsType{
        return HomeContentsType.getType(KosSharedPreferences(this).getInt(Keys.KEY_HOME_CONTENTS))
    }

    private fun onClickMenuLocation(type: MenuLocationType){
        if(type == MenuLocationType.MENU_LEFT){
            tvMenuLocationLeft.isSelected = true
            tvMenuLocationRight.isSelected = false
        }else{
            tvMenuLocationLeft.isSelected = false
            tvMenuLocationRight.isSelected = true
        }
    }

    private fun onClickHomeContents(type: HomeContentsType){
        if(type == HomeContentsType.ORDER_SATUS){
            tvOrderStatusSelect.isSelected = true
            tvBusinessInfoSelect.isSelected = false
        }else{
            tvOrderStatusSelect.isSelected = false
            tvBusinessInfoSelect.isSelected = true
        }
    }

    /**
     * 현재 선택된 리스트 카운트
     */
    private fun getCurrentCount(){
        tvCurrentCount.text = adapterRight.getList().size.toString()
    }

    private fun initLeftList(){
        adapterLeft.clearList()
//        val listLeft = resources.getStringArray(R.array.drawer_main_menu)
        adapterLeft.addAllItem(KosUtil.getLeftDrawerMenuList())
//        for((index, menu) in listLeft.withIndex()){
//            //좌측 메뉴리스트
//            val searchSubList = getRightSubMenuList(index)
//            adapterLeft.addItem(DrawerMenuData().apply {
//                title = menu
//                menuType = DrawerMenuType.getType(index)
//                listSubMenu = searchSubList
//            })
//        }

        adapterLeft.notifyDataSetChanged()
    }

    private fun initRightList(){

        adapterRight.clearList()
        adapterRight.addAllItem(KosUtil.getQuickMenuList())
        adapterRight.notifyDataSetChanged()
    }


    /**
     * 우측 메뉴 리스트와 Sync 맞추기
     */
    private fun syncLeftRight(){
        var sameData: Boolean
        for(item in adapterRight.getList()){
            sameData = false

            for(leftData in adapterLeft.getList()){
                if(sameData) break

                for ((j, subData) in leftData.listSubMenu.withIndex()){
                    if(item.title == subData.title){
                        leftData.listSubMenu[j].isSelectedMenu = true
                        sameData = true
                        break
                    }
                }
            }
        }
        adapterLeft.notifyDataSetChanged()
    }

//    /**
//     * 우측 서브 메뉴 리스트
//     */
//    private fun getRightSubMenuList(parentIndex: Int): MutableList<DrawerMenuData>{
//
//        var returnList = arrayListOf<DrawerMenuData>()
//        val array = getRightSubMenu(parentIndex)
//
//        for((index, menu) in array.withIndex()){
//            returnList.add(DrawerMenuData().apply {
//                menuType = DrawerMenuType.getType(parentIndex)
//                title = menu
//                childPosition = index
//            })
//        }
//        return returnList
//    }
//
//    /**
//     * 0: 주문
//     * 1: 종목정보
//     * 2: My Page
//     * 3: 고객센터
//     * 4: 설정
//     */
//    private fun getRightSubMenu(index: Int): Array<String>{
//        return when(index){
//            DrawerMenuType.ORDER.type->{
//                resources.getStringArray(R.array.drawer_order)
//            }
//            DrawerMenuType.STOCK_INFO.type->{
//                resources.getStringArray(R.array.drawer_stock_info)
//            }
//            DrawerMenuType.MYPAGE.type->{
//                resources.getStringArray(R.array.drawer_mypage)
//            }
//            DrawerMenuType.CUSTOMER_CENTER.type->{
//                resources.getStringArray(R.array.drawer_customer)
//            }
//            DrawerMenuType.SETTING.type->{
//                resources.getStringArray(R.array.drawer_setting)
//            }
//            else ->{
//                resources.getStringArray(R.array.drawer_order)
//            }
//        }
//    }



    private fun loadingClose(){
    }


    /**
     * Loading Progress
     */
    override fun showProgress(isShow: Boolean){
        if(isShow){
            progress_bar_login.toVisible()
        }else{
            progress_bar_login.toInvisible()
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
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
    }




}
