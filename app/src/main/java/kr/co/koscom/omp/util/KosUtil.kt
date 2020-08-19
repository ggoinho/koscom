package kr.co.koscom.omp.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.R
import kr.co.koscom.omp.constants.Keys
import kr.co.koscom.omp.data.local.DrawerMenuData
import kr.co.koscom.omp.enums.DrawerMenuType
import kr.co.koscom.omp.extension.toResString
import java.util.*

object KosUtil {

    /**
     * 전체 드로루 메뉴 리스트
     */
    fun getLeftDrawerMenuList(): MutableList<DrawerMenuData>{
        var returnList = arrayListOf<DrawerMenuData>()
        val listLeft = BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_main_menu)
        for((index, menu) in listLeft.withIndex()){
            //좌측 메뉴리스트
            val searchSubList = getRightSubMenuList(index)
            returnList.add(DrawerMenuData().apply {
                title = menu
                menuType = DrawerMenuType.getType(index)
                listSubMenu = searchSubList
            })
        }
        return returnList
    }

    /**
     * DrawerMenu 서브리스트
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
     * 내가 설정한 퀵메뉴 리스트
     */
    fun getQuickMenuList(): MutableList<DrawerMenuData>{

        val recentlyDrawerMenu = KosSharedPreferences(BaseApplication.getAppContext()).getString(Keys.KEY_QUICK_MENULIST)
        if(!recentlyDrawerMenu.isNullOrEmpty()){
            return Gson().fromJson(recentlyDrawerMenu, object : TypeToken<ArrayList<DrawerMenuData>>() {}.type) as ArrayList<DrawerMenuData>
        }else{
            var returnList = arrayListOf<DrawerMenuData>()
            //아무것도 없을 경우
            for(i in 0 until 5){
                val drawerMenuData = when(i){
                    0 ->{
                        //주문현황
                        DrawerMenuData().apply {
                            menuType = DrawerMenuType.ORDER
                            title = R.string.drawer_contents_order_status.toResString()
                            childPosition = 0
                        }
                    }
                    1 ->{
                        //주문등록
                        DrawerMenuData().apply {
                            menuType = DrawerMenuType.ORDER
                            title = R.string.drawer_contents_order_regist.toResString()
                            childPosition = 3
                        }

                    }
                    2 ->{
                        //기업정보
                        DrawerMenuData().apply {
                            menuType = DrawerMenuType.STOCK_INFO
                            title = R.string.drawer_contents_business_info.toResString()
                            childPosition = 0
                        }

                    }
                    3 ->{
                        //기업알림/홍보/평가자료
                        DrawerMenuData().apply {
                            menuType = DrawerMenuType.STOCK_INFO
                            title = R.string.drawer_contents_business_alarm.toResString()
                            childPosition = 2
                        }

                    }
                    4 ->{
                        //가입정보관리
                        DrawerMenuData().apply {
                            menuType = DrawerMenuType.MYPAGE
                            title = R.string.drawer_contents_sign_info_management.toResString()
                            childPosition = 0
                        }
                    }
                    else -> {
                        DrawerMenuData()
                    }
                }

                returnList.add(drawerMenuData)
            }
            return returnList
        }
    }


    /**
     * DrawerMenu 서브리스트 가져오기
     * 0: 주문
     * 1: 종목정보
     * 2: My Page
     * 3: 고객센터
     * 4: 설정
     */
    private fun getRightSubMenu(index: Int): Array<String>{
        return when(index){
            DrawerMenuType.ORDER.type->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_order)
            }
            DrawerMenuType.STOCK_INFO.type->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_stock_info)
            }
            DrawerMenuType.MYPAGE.type->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_mypage)
            }
            DrawerMenuType.CUSTOMER_CENTER.type->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_customer)
            }
            DrawerMenuType.SETTING.type->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_setting)
            }
            else ->{
                BaseApplication.getAppContext().resources.getStringArray(R.array.drawer_order)
            }
        }
    }


}