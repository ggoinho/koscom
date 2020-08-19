package kr.co.koscom.omp.util

import android.content.Context
import android.content.SharedPreferences

/**
 * NeoSharedPreference
 * @author parkjh
 * @since 2020. 3. 6
 **/
class KosSharedPreferences(context: Context) {

    private val preferenceFileName = "NeoSharedPref"
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().run {
            putInt(key, value)
            apply()
        }
    }

    fun setIntCommit(key: String, value: Int) {
        sharedPreferences.edit().run {
            putInt(key, value)
            commit()
        }
    }

//    fun getInt(key: String): Int = sharedPreferences.getInt(key, -1)

    fun getInt(key: String, defValue: Int = 0): Int = sharedPreferences.getInt(key, defValue)

    fun setString(key: String, value: String) {
        sharedPreferences.edit().run {
            putString(key, value)
            apply()
        }
    }

    fun setStringCommit(key: String, value: String) {
        sharedPreferences.edit().run {
            putString(key, value)
            commit()
        }
    }

    fun getString(key: String, defValue: String=""): String =
        sharedPreferences.getString(key, defValue).toString()

    fun getString(key: String): String = sharedPreferences.getString(key, "").toString()

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().run {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().run {
            putLong(key, value)
            apply()
        }
    }

    fun getLong(key: String) = sharedPreferences.getLong(key, -1)

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).commit()
    }

    fun removeAll() {
        sharedPreferences.edit().clear().commit()
    }
}



