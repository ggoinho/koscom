package kr.co.koscom.omp.data.local

import kr.co.koscom.omp.enums.DrawerMenuType
import java.io.Serializable

class DrawerMenuData: Serializable {

    var title: String = ""
    var searchName: String = ""
    var isSelectedMenu: Boolean = false
    var menuType: DrawerMenuType = DrawerMenuType.ORDER

    var listSubMenu: MutableList<DrawerMenuData> = arrayListOf()

    var childPosition = 0

    override fun equals(other: Any?): Boolean {
        var isSame = false
        if(other != null && other is DrawerMenuData){
            isSame = other.title == this.title
        }

        return isSame
    }

}