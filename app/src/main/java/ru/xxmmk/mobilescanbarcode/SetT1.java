package ru.xxmmk.mobilescanbarcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class SetT1 extends Activity {
    private MobileBCRApp mMobileBCRApp;
    private CameraManager cameraManager;

    // Tag used for logging errors
    private static final String TAG = SetT1.class.getSimpleName();
    // Let's define some intent strings
    // This intent string contains the source of the data as a string
    private static final String SOURCE_TAG = "com.motorolasolutions.emdk.datawedge.source";
    // This intent string contains the barcode symbology as a string
    private static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";
    // This intent string contains the barcode data as a byte array list
    private static final String DECODE_DATA_TAG = "com.motorolasolutions.emdk.datawedge.decode_data";
    // This intent string contains the captured data as a string
    // (in the case of MSR this data string contains a concatenation of the track data)
    private static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";
    // Let's define the MSR intent strings (in case we want to use these in the future)
    private static final String MSR_DATA_TAG = "com.motorolasolutions.emdk.datawedge.msr_data";
    private static final String MSR_TRACK1_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1";
    private static final String MSR_TRACK2_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2";
    private static final String MSR_TRACK3_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3";
    private static final String MSR_TRACK1_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1_status";
    private static final String MSR_TRACK2_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2_status";
    private static final String MSR_TRACK3_STATUS_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3_status";
    private static final String MSR_TRACK1_ENCRYPTED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1_encrypted";
    private static final String MSR_TRACK2_ENCRYPTED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2_encrypted";
    private static final String MSR_TRACK3_ENCRYPTED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3_encrypted";
    private static final String MSR_TRACK1_HASHED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track1_hashed";
    private static final String MSR_TRACK2_HASHED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track2_hashed";
    private static final String MSR_TRACK3_HASHED_TAG = "com.motorolasolutions.emdk.datawedge.msr_track3_hashed";

    // Let's define the API intent strings for the soft scan trigger
    private static final String ACTION_SOFTSCANTRIGGER = "com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER";
    private static final String EXTRA_PARAM = "com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER";
    private static final String DWAPI_START_SCANNING = "START_SCANNING";
    private static final String DWAPI_STOP_SCANNING = "STOP_SCANNING";
    private static final String DWAPI_TOGGLE_SCANNING = "TOGGLE_SCANNING";

    private static String ourIntentAction = "ru.xxmmk.mobilescanbarcode.T1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_t1);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());

        if (mMobileBCRApp.TwoActFlag)
        {finish();
        mMobileBCRApp.TwoActFlag=false;}
        Button Bk = (Button) findViewById(R.id.SetT1Ch);
        Bk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        Button setkey = (Button) findViewById(R.id.SetT1M);
        setkey.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mMobileBCRApp.Scant1=true;
                mMobileBCRApp.dataLV.clear(); //очистим массив с элементами Т-1
                Intent intent = new Intent();
                intent.setClass(SetT1.this, EnterCode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

               /*Запуск сканирования с кнопки
                    Intent i = new Intent();
                    i.setAction(ACTION_SOFTSCANTRIGGER);
                    i.putExtra(EXTRA_PARAM, DWAPI_TOGGLE_SCANNING);
                    SetT1.this.sendBroadcast(i);
                    Toast.makeText(v.getContext(), "Soft scan trigger toggled.", Toast.LENGTH_SHORT).show();*/
            }
        });
        Button scan = (Button) findViewById(R.id.SetT1A);
        scan.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
             /*   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
                startActivityForResult(intent,1);*/
                   mMobileBCRApp.ClearTmpData();
                   scanBarcodeCustomOptions(v);

            }
        });
        mMobileBCRApp.dataLV.clear(); //очистим массив с элементами Т-1
        Intent i = getIntent();
    //    handleDecodeData(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobileBCRApp.ClearTmpData();
    }

    // We need to handle any incoming intents, so let override the onNewIntent method
    @Override
    public void onNewIntent(Intent i) {
    try {
        handleDecodeData(i);
    }
    catch (final Exception e)
        {
           Log.d("1","java.lang.NullPointerException");
        }
    }
    //запуск сканирования
    public void scanBarcodeCustomOptions(View view) {
        //    Toast.makeText(this, "Сканирование штрих кодов запрещено", Toast.LENGTH_LONG).show();
        mMobileBCRApp.dataLV.clear();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.autoWide();
        integrator.initiateScan();
        //       cameraManager.setTorch(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_t1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        mMobileBCRApp.T1BarCode=result.getContents();
        /*временно присваиваем штриход в самовывоз*/
        mMobileBCRApp.SKDRfId=result.getContents();
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show();
            } else {
                mMobileBCRApp.BarCodeR="";
                Intent intent = new Intent();
                /*Уходим на другой экран*/
                intent.setClass(SetT1.this, TOneForm.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    CameraManager getCameraManager() {
        return cameraManager;
    }

    private void handleDecodeData(Intent i) {
        // check the intent action is for us
        if (i.getAction().contentEquals(ourIntentAction)) {
            String out = "";
            String source = i.getStringExtra(SOURCE_TAG);
            if (source == null) source = "scanner";
            String data = i.getStringExtra(DATA_STRING_TAG);
            Integer data_len = 0;
            if (data != null) data_len = data.length();
            if (source.equalsIgnoreCase("scanner")) {
                if (data != null && data.length() > 0) {
                    String sLabelType = i.getStringExtra(LABEL_TYPE_TAG);
                    if (sLabelType != null && sLabelType.length() > 0) {
                        sLabelType = sLabelType.substring(11);
                    } else {
                        sLabelType = "Unknown";
                    }
                    out = "Source: Scanner, " + "Symbology: " + sLabelType + ", Length: " + data_len.toString() + ", Data: ...\r\n";
                }
            }
            if (source.equalsIgnoreCase("msr")) {
                out = "Source: MSR, Length: " + data_len.toString() + ", Data: ...\r\n";
            }
            Toast.makeText(this, "Штрих код " + data, Toast.LENGTH_SHORT).show();
            mMobileBCRApp.T1BarCode=data;
            mMobileBCRApp.BarCodeR="";
            Intent intent = new Intent();
                /*Уходим на другой экран*/
            intent.setClass(SetT1.this, TOneForm.class);
            startActivity(intent);
        }
    }
}
