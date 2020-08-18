package kr.co.koscom.omp.ui.drawerlayout.quick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_order_regist.*
import kotlinx.android.synthetic.main.activity_quick_menu.*
import kotlinx.android.synthetic.main.toolbar_back.tvToolbarTitle
import kotlinx.android.synthetic.main.toolbar_close.*
import kr.co.koscom.omp.R
import kr.co.koscom.omp.adapter.QuickLeftMenuAdapter
import kr.co.koscom.omp.adapter.QuickRightMenuAdapter
import kr.co.koscom.omp.adapter.holder.QuickRightViewHolder
import kr.co.koscom.omp.custom.QuickItemTouchHelperCallback
import kr.co.koscom.omp.data.Injection
import kr.co.koscom.omp.data.ViewModelFactory
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.extension.toInvisible
import kr.co.koscom.omp.extension.toResString
import kr.co.koscom.omp.extension.toVisible
import kr.co.koscom.omp.ui.drawerlayout.quick.contract.QuickMenuContract
import kr.co.koscom.omp.view.ViewUtils

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

//        presenter.initViewModel(viewModelFactory)


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

        val listLeft = resources.getStringArray(R.array.drawer_main_menu)
        for((index, menu) in listLeft.withIndex()){
            //좌측 메뉴리스트
            val searchSubList = getRightSubMenuList(index)
            adapterLeft.addItem(DrawerMenuData().apply {
                title = menu
                isSelectedMenu = index == 0
                menuType = DrawerMenuType.getType(index)
                listSubMenu = searchSubList
            })

            //우측 메뉴리스트
//            adapterRight.addItem(DrawerMenuData().apply {
//                title = menu
//                isSelectedMenu = index == 0
//                menuType = DrawerMenuType.getType(index)
//                listSubMenu = searchSubList
//            })

        }

        adapterLeft.notifyDataSetChanged()

        getCurrentCount()


    }

    private fun initListener(){

        layoutClose.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom)
        }

    }


    /**
     * 현재 선택된 리스트 카운트
     */
    private fun getCurrentCount(){
        tvCurrentCount.text = adapterRight.getList().size.toString()
    }

    /**
     * 우측 서브 메뉴 리스트
     */
    private fun getRightSubMenuList(parentIndex: Int): MutableList<DrawerMenuData>{

        var returnList = arrayListOf<DrawerMenuData>()
        val array = getRightSubMenu(parentIndex)

        for((index, menu) in array.withIndex()){
            returnList.add(DrawerMenuData().apply {
                menuType = DrawerMenuType.getType(parentIndex)
                title = menu
                childPosition = index
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
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right)
    }




}
