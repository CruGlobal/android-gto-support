# keep the delegate member name so we can inject a TrustManager
-keepclassmembernames class org.ccci.gto.android.common.util.DynamicSSLSocketFactory {
    javax.net.ssl.SSLSocket delegate;
}
