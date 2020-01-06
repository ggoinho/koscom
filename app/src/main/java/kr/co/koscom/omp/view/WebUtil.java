package kr.co.koscom.omp.view;

import android.os.Build;
import android.text.Html;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chw on 2017. 8. 22..
 */

public class WebUtil {


    public static String jsonToQueryString(String json)
    {
        StringBuffer buffer = new StringBuffer();

        Gson gson = new Gson();

        Map<String, String> jsonInit = gson.fromJson(json, HashMap.class);
        Iterator<String> keys = jsonInit.keySet().iterator();

        for(int i = 0; keys.hasNext(); i++)
        {
            if(i > 0)
                buffer.append("&");

            String key = keys.next();
            buffer.append(key + "=" + jsonInit.get(key));
        }

        return buffer.toString();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }

    public static String htmlToText(String msg){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY).toString();
        }
        else{
            return Html.fromHtml(msg).toString();
        }
    }

    public static String blockSpecialChars(String msg){

        //msg = msg.replace("<script", "");
        //msg = msg.replace("<Vscript", "");
        msg = msg.replace("&", "＆");
        msg = msg.replace("<", "＜");
        msg = msg.replace(">", "＞");
        msg = msg.replace("(", "（");
        msg = msg.replace(")", "）");
        msg = msg.replace("'", "′");
        msg = msg.replace("\"", "˝");
        msg = msg.replace("/", "／");
        msg = msg.replace("=", "＝");

        return msg;
    }
}
