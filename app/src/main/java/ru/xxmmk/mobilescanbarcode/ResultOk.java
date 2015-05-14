package ru.xxmmk.mobilescanbarcode;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class ResultOk extends Activity /*implements LoaderCallbacks<Cursor>*/{
    private MobileBCRApp mMobileBCRApp;
    WebView mWebView;

    @Override
    protected void onStart(){
        super.onStart();
        //   Log.d("here","us");
        Button CnButton = (Button) findViewById(R.id.bkerr);
        CnButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_ok);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());
        //     Log.d("here1","us1");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/*
        ImageView imgView = (ImageView) findViewById(R.id.photoim);
        String imageUrl ="http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid="+ mMobileBCRApp.SKDOperRfId +"&p_mode=Y";  //"http://i.imgur.com/CQzlM.jpg";// "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid=1&p_mode=Y";
        Log.d("Photo","http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid="+ mMobileBCRApp.SKDOperRfId +"&p_mode=Y");
//        imgView.setImageBitmap(getBitmapFromURL(imageUrl)); //на медленных соединениях тупит*/

    }

}



