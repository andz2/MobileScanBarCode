package ru.xxmmk.mobilescanbarcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
                Intent intent = new Intent();
                intent.setClass(SetT1.this, EnterCode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Button scan = (Button) findViewById(R.id.SetT1A);
        scan.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //         Log.d("Go KPP","Go KPP");
             /*   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
                startActivityForResult(intent,1);*/
                   scanBarcodeCustomOptions(v);
            }
        });
        mMobileBCRApp.dataLV.clear(); //очистим массив с элементами Т-1
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

}
