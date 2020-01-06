package kr.co.koscom.omp.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class HttpTest2 {
    public final static void main(String[] args){
        try{
            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAnyTrustManager()};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

           String data = "{\"LOGIN_ID\":\"CL0000082\"}";
           //URLEncoder.encode("LOGIN_ID", "UTF-8") + "=" + URLEncoder.encode("CL0000082", "UTF-8");

            URL url = new URL("http://1.255.57.88:8080/api/invst/chatModal/properties");
            URLConnection conn = (URLConnection)url.openConnection();

            //conn.setSSLSocketFactory(sc.getSocketFactory());

            // If you invoke the method setDoOutput(true) on the URLConnection, it will always use the POST method.
            conn.setRequestProperty("Accept", "application/json");
            //conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36");
            //conn.setRequestProperty("Content-Length", String.valueOf(data.length()));

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            wr.write(data);
            wr.flush();

            BufferedReader rd = null;
            /*if(conn.getResponseCode() >= 400){
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            else{*/
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            /*}*/
            String line = null;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line;
            }

            System.out.println("response : " + response);

            wr.close();
            rd.close();

        }catch(Exception e){e.printStackTrace();}
    }
}
