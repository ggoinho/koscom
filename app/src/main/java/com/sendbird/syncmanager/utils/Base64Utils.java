package com.sendbird.syncmanager.utils;

import android.util.Base64;

public class Base64Utils {

    /**
     * Base64 인코딩
     */
    public static String getBase64encode(String content){
        return Base64.encodeToString(content.getBytes(), 0);
    }

    /**
     * Base64 디코딩
     */
    public static String getBase64decode(String content){
        return new String(Base64.decode(content, 0));
    }


}
