package kr.co.koscom.omp.data;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustAnyTrustManager implements X509TrustManager {

    public java.security.cert.X509Certificate[] getAcceptedIssuers()
    {
        return new java.security.cert.X509Certificate[]{};
    }
    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
    {
        //No need to implement.
    }
    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
    {
        //No need to implement.
    }
}