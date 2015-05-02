package ru.xxmmk.mobilescanbarcode;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class empty1T extends Activity {
    private MobileBCRApp mMobileSKDApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty1_t);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mMobileSKDApp = ((MobileBCRApp) this.getApplication());
        Button Exitbutton=(Button)findViewById(R.id.bkerr);

        Exitbutton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              finish();

                                          }
                                      }
        );
    }

}