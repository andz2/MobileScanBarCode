package ru.xxmmk.mobilescanbarcode;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MobileBCRApp extends Application {
        public String SKDOperator="Кто ВЫ?";
        public String SKDKPP="Автопроезд 12";//"Укажите автопроезд"; //временно
        public String SKDStep="1";
        public String SKDOperRfId;
        public String T1BarCode = "12345"; //Т-1
        public String SKDRfId; //карта охранника
        public Boolean    NetErr = false;
        protected String mResult= "null";
        private String mLoginURL = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.xxmob_auto_pkg.login";//"https://navigator.mmk.ru/login_kis.aspx";
        public String mDatURL = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.MOBILE_SKD_VIEW";//"https://navigator.mmk.ru/login_kis.aspx";
        public String ListKPP = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxmob_auto_pkg.list_kpp";

        public String URLListCargoItems    = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxmob_auto_pkg.get_t1_list_json";
        public String URLT1Headerdata      = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxmob_auto_pkg.get_t1_hdr_json";

        public String URLAuditHeader       = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxmob_auto_pkg.set_header_audit";
        public String URLAuditLines        = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxmob_auto_pkg.set_lines_audit";

        public String T1Header;
        public String AuditSeq;
        public String BarCodeR =""; //";Штрихкод: 324;Штрихкод: 785;"; массив штрих кодов
        public String CurrBC ="";   //текущий шк
        public ArrayList<T1Item> dataLV = new ArrayList<T1Item>(); //массив итемов для Т1

        public String T1Driver;  //водитель
        public String T1Num="";  //номер Т-1
        public String T1Auto;    //номер авто
        public Boolean isChancel = false;
        public Boolean TwoActFlag =false; //флаг закрытия повторной активности
        public boolean Scant1;
        public boolean empty1T; //флаг на пустую 1-Т
        private static MobileBCRApp instance;
        public String mToken;


        public MobileBCRDB getmDbHelper() {
            Log.d(this.getLOG_TAG(), "MobileBCRApp.getmDbHelper");
            return mDbHelper;
        }
        private MobileBCRDB mDbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new MobileBCRDB(getApplicationContext());
        mDbHelper.getWritableDatabase();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mDbHelper.close();
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        mDbHelper.close();
    }

    public String getT1HeaderDataURL(String bc) {
        return this.URLT1Headerdata+"?p_bc="+bc+"&p_token="+mToken+"";
    }
    public String getT1ItemDataURL(String T1N) {
        return this.URLListCargoItems+"?p_t1n="+T1N+"";
    }

    public String getT1AuditHeaderURL(String pBc,String pRes) {
        return this.URLAuditHeader+"?p_bc="+pBc+"&p_res="+pRes+"&p_token="+mToken+"";
    }
    public String getT1AuditLinesURL(String pSeq,String pBc,String pBcLine,String pRes) {
        return this.URLAuditLines+"?p_seq="+pSeq+"&p_bc="+pBc+"&p_bc_line="+pBcLine+"&p_res="+pRes+"&p_token="+mToken+"";
    }

    public void ClearTmpData ()
    {
        dataLV.clear(); //очистим массив с элементами Т-1
        CurrBC = "-1";
    }
    public static MobileBCRApp getInstance() {
        return instance;
    }

    public MobileBCRApp() {
        instance = this;
    }

        public void vibrate() {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(500);
        }

        public String getLoginDataURL(String rfId) {
        return this.mLoginURL+"?rfid="+rfId;
        }

        public String getLOG_TAG() {
            return LOG_TAG;
        }
    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }


        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
        final String LOG_TAG = "myLogs";

    }