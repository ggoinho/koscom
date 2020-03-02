package kr.co.koscom.omp.data;

import android.util.Log;

import com.sendbird.syncmanager.utils.ComUtil;
import com.sendbird.syncmanager.utils.PreferenceUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if (ComUtil.cookie != null){
            Set<String> preferences = ComUtil.cookie;
            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);
            }
        }

        //Web, Android, iOS 구분을 위해 User - Agent세팅
        //builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");
        return chain.proceed(builder.build());

    }
}
