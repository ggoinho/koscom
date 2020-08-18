package kr.co.koscom.omp

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import android.util.Log
import org.apache.commons.lang3.StringUtils


class Preference {

    companion object{

        val CHANNEL_ID = "CHANNEL_ID"

        val PREFERENCE = Preference::class.java.`package`!!.name

        fun setPushYn(context: Context, value: String) {
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("PUSH_YN", value)
            editor.commit()
        }
        fun getPushYn(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("PUSH_YN", "N")
        }

        fun setAdYn(context: Context, value: String) {
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("AD_YN", value)
            editor.commit()
        }
        fun getAdYn(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("AD_YN", "N")
        }

        fun setPushToken(context: Context, value: String) {
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("PUSH_TOKEN", value)
            editor.commit()
        }
        fun getPushToken(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("PUSH_TOKEN", null)
        }

        fun setServerToken(context: Context, value: String) {
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("SERVER_TOKEN", value)
            editor.commit()
        }
        fun getServerToken(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("SERVER_TOKEN", null)
        }


        fun setRecentlyDrawerMenu(context: Context, value: String) {
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("RECENTLY_DRAWER_MENU", value)
            editor.commit()
        }

        fun getRecentlyDrawerMenu(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("RECENTLY_DRAWER_MENU", "")
        }

        fun addRecentStock(context: Context, value: String): Boolean {

            var recentStock = StringUtils.defaultIfEmpty(getRecentStock(context),"")

            if(!",$recentStock,".contains(",$value,")){
                if(!recentStock.isNullOrEmpty()){
                    recentStock = ",$recentStock"
                }
                recentStock = value.plus(recentStock)

                val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
                val editor = pref.edit()
                editor.putString("RECENT_STOCK", recentStock)
                editor.commit()
                Log.d("RecentList", "addRecentStock RECENT_STOCK : $recentStock")

                return true
            }

            return false

        }
        fun removeRecentStock(context: Context, index: Int){
            var recentStock = StringUtils.defaultIfEmpty(getRecentStock(context),"")

            if(!recentStock.isNullOrEmpty()){
                var items = StringUtils.split(recentStock, ",").toMutableList()
                items.removeAt(index)

                val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
                val editor = pref.edit()
                editor.putString("RECENT_STOCK", StringUtils.join(items, ","))
                editor.commit()
                Log.d("RecentList", "removeRecentStock RECENT_STOCK : " + StringUtils.join(items, ","))
            }
        }
        fun getRecentStock(context: Context): String?{
            val pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
            return pref.getString("RECENT_STOCK", null)
        }
    }
}
