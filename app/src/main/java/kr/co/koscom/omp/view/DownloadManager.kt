package kr.co.koscom.omp.view

import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast

import com.google.android.material.snackbar.Snackbar
import com.sendbird.syncmanager.utils.PreferenceUtils

import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.URL

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

import kr.co.koscom.omp.BaseApplication
import kr.co.koscom.omp.BuildConfig
import kr.co.koscom.omp.Preference
import kr.co.koscom.omp.data.TrustAnyTrustManager

class DownloadManager {

    fun download(file_path: String, org_file_name: String, sys_file_name: String, CLNT_NO: String, MOBILE_TOCKEN: String, listener: (file: File) -> Unit) {

        Thread(Runnable {
            var rd: DataInputStream? = null
            var wr: OutputStreamWriter? = null
            var fos: FileOutputStream? = null
            try {
                val trustAllCerts = arrayOf<TrustManager>(TrustAnyTrustManager())

                val sc = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, java.security.SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

                var data =
                    "file_path=$file_path&org_file_name=$org_file_name&sys_file_name=$sys_file_name"

                var url2 = "CLNT_NO=$CLNT_NO"
                url2 = url2 + "&MOBILE_TOCKEN=$MOBILE_TOCKEN"

                data = "$data&$url2"

                Log.d(DownloadManager::class.java.simpleName, "download : $data")
                //"file_path":"/board/201910","org_file_name":"그림1.png","sys_file_name":"33ed1_201910290216095810.png"

                val url = URL(BuildConfig.SERVER_URL + "/api/fileDownLoad")
                val conn = url.openConnection() as HttpsURLConnection

                //conn.setSSLSocketFactory(sc.getSocketFactory());

                // If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
                //conn.setRequestProperty("Accept", "application/json");
                conn.requestMethod = "POST"
                conn.doOutput = true
                //conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
                //conn.setRequestProperty("Content-Length", String.valueOf(data.length()));

                wr = OutputStreamWriter(conn.outputStream, "utf-8")
                wr.write(data)
                wr.flush()

                /*if(conn.getResponseCode() >= 400){
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            else{*/
                rd = DataInputStream(conn.inputStream)
                /*}*/
                val buffer = ByteArray(1024)
                var length: Int

                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, org_file_name)
                Log.d(
                    DownloadManager::class.java.simpleName,
                    "file.getAbsolutePath() : " + file.absolutePath
                )
                fos = FileOutputStream(file)
                while (true) {
                    length = rd.read(buffer)
                    if(length > 0){
                        fos.write(buffer, 0, length)
                    }
                    else{
                        break
                    }
                }

                println("write ok")

                Handler(BaseApplication.getAppContext().mainLooper).post {
                    if(listener != null){
                        listener.invoke(file)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    wr?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    rd?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }).start()

    }

    fun downloadExcel(CLNT_NO: String, MOBILE_TOCKEN: String, listener: (file: File) -> Unit) {

        Thread(Runnable {
            var rd: DataInputStream? = null
            var wr: OutputStreamWriter? = null
            var fos: FileOutputStream? = null
            try {
                val trustAllCerts = arrayOf<TrustManager>(TrustAnyTrustManager())

                val sc = SSLContext.getInstance("SSL")
                sc.init(null, trustAllCerts, java.security.SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

                var data =
                    "CLNT_NO=$CLNT_NO&MOBILE_TOCKEN=$MOBILE_TOCKEN"

                Log.d(DownloadManager::class.java.simpleName, "downloadExcel : $data")
                //"file_path":"/board/201910","org_file_name":"그림1.png","sys_file_name":"33ed1_201910290216095810.png"

                val url = URL(BuildConfig.SERVER_URL + "/mobile/invst/myPage/invstMyPageMyOrdLst4/excel")
                val conn = url.openConnection() as HttpsURLConnection

                //conn.setSSLSocketFactory(sc.getSocketFactory());

                // If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
                //conn.setRequestProperty("Accept", "application/json");
                conn.requestMethod = "POST"
                conn.doOutput = true
                //conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
                //conn.setRequestProperty("Content-Length", String.valueOf(data.length()));

                wr = OutputStreamWriter(conn.outputStream, "utf-8")
                wr.write(data)
                wr.flush()

                /*if(conn.getResponseCode() >= 400){
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            else{*/
                rd = DataInputStream(conn.inputStream)
                /*}*/
                val buffer = ByteArray(1024)
                var length: Int

                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, "MyOrderListExcel.xls")
                Log.d(
                    DownloadManager::class.java.simpleName,
                    "file.getAbsolutePath() : " + file.absolutePath
                )
                fos = FileOutputStream(file)
                while (true) {
                    length = rd.read(buffer)
                    if(length > 0){
                        fos.write(buffer, 0, length)
                    }
                    else{
                        break
                    }
                }

                println("write ok")

                Handler(BaseApplication.getAppContext().mainLooper).post {
                    if(listener != null){
                        listener.invoke(file)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    wr?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    rd?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }).start()

    }
}
