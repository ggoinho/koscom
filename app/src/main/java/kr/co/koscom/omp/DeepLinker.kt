package kr.co.koscom.omp

import android.net.Uri

object DeepLinker {
    private var screen: String? = null
    private var url: String? = null
    private var tab: Int? = null
    private var message: String? = null

    fun parse(uri: Uri){
        screen = uri.getQueryParameter("screen")
        url = uri.getQueryParameter("url")
        tab = (uri.getQueryParameter("tab") ?: "0").toInt()
        message = uri.getQueryParameter("message")
    }

    fun subscribeScreen(): String?{
        return screen.also {
            screen = null
        }
    }
    fun subscribeUrl(): String?{
        return url.also {
            url = null
        }
    }
    fun subscribeTab(): Int?{
        return tab.also {
            tab = null
        }
    }
    fun subscribeMessage(): String?{
        return message.also {
            message = null
        }
    }
}