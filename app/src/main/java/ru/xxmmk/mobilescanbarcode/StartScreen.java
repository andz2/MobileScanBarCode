package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Camera;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class StartScreen extends Activity {

    //flag to detect flash is on or off
    private boolean isLighOn = false;
    private Camera camera;

   /* @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }*/

    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private UserLoginTask mAuthTask = null;
    Context context;
    private CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());
        context = StartScreen.this;
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextSize(18);
                //title.setTextColor(Color.BLACK);
            }
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //политика сетевого доступа
        StrictMode.setThreadPolicy(policy); //применяем политику

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        StartScreen ();

//*****************
 /*       camera = Camera.open();
        final Parameters p = camera.getParameters();
                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                    camera.startPreview();*/
//*****************
    }


 /*   void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!safeMode && !prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, true)) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }
    private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
        boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
        doSetTorch(parameters, currentSetting, safeMode);
    }
*/

    @Override
    protected void  onResume()
    {
        super.onResume();
        // Log.d("Resume","Resume");
        enableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        cameraManager = new CameraManager(getApplication());
        StartScreen ();

    }
    @Override
    public void onBackPressed() {
        //здесь можно переопределить кнопку назад
    }
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d( mMobileBCRApp.SKDStep, mMobileBCRApp.SKDStep);

        if (mMobileBCRApp.SKDStep=="1" ) {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                mMobileBCRApp.SKDOperRfId=bytesToHex(myTag.getId());
                mMobileBCRApp.SKDRfId=mMobileBCRApp.SKDOperRfId;
                Log.d( mMobileBCRApp.SKDOperRfId, "=mCode");
                attemptLogin();
                Log.d(mMobileBCRApp.SKDOperator,"operator !!!!!!");
                mMobileBCRApp.vibrate();
                Log.d("Поехали","action !!!!!!");
            }}
    }

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true); *************************************************************************подменить на показ окна
            mAuthTask = new UserLoginTask(mMobileBCRApp.SKDRfId);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }

   /* @Override
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
    }*/

   final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mToken = "null";
        UserLoginTask(String rfId) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
            mMobileBCRApp.NetErr=false;
            if ( mMobileBCRApp.SKDStep=="1" ) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(mMobileBCRApp.getLoginDataURL(mMobileBCRApp.SKDRfId));
                    Log.d(mMobileBCRApp.getLOG_TAG(), "OperLogin.UserLoginTask " + mMobileBCRApp.getLoginDataURL(mMobileBCRApp.SKDRfId));
                    try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            try {
                                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                                JSONArray jsonArray = new JSONArray(builder.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    mToken = jsonObject.getString("token");
                                    mMobileBCRApp.SKDOperator = jsonObject.getString("oper");
                                    vStatus = true;
                                    //   Log.d(jsonObject.getString("oper"),"Tst");

                                }
                                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        } else {
                            //Log.e("Login fail", "Login fail");

                        }
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();


                    } catch (IOException e) {
                        e.printStackTrace();
                        mMobileBCRApp.NetErr=true;

                    }

                    Thread.sleep(10);
                    vStatus = vStatus && !mToken.equals("null");
                    if (vStatus) {
//                        mMobileBCRApp.setmHASH(mToken);**********************************временно убрано
                        return true;
                    } else {
                        return false;
                    }

                } catch (InterruptedException e) {
                    return false;
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            // showProgress(false); ******************************************************заменим потом

            if (success) {
                Log.d("Is OK","Is OK");
                Intent intent = new Intent();
                intent.setClass(StartScreen.this, AccLogin.class);

                startActivity(intent);

                setContentView(R.layout.activity_start_screen);
                Button KPPbutton=(Button)findViewById(R.id.SetKPP);
                KPPbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_kpp_state)); //setBackgroundResource
                KPPbutton.setTextColor(Color.rgb(255,255,255));
                Button Logbutton=(Button)findViewById(R.id.Loginbutton);
                Logbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_ok)); //setBackgroundResource
                Logbutton.setTextColor(Color.rgb(255,255,255));
                //      Logbutton.setText(Html.fromHtml(/*getResources().getString(R.string.seccabstr)*/"<b>Личный кабинет</b><br><br><sup><small>Нажмите для информации</small></sup>"));
                mMobileBCRApp.SKDStep = "2";
            } else
            {
                Log.d("Мы не в сети","сети нет");
                Intent intent = new Intent();
                if ( mMobileBCRApp.SKDStep=="1" )
                {
                    Long i= Long.parseLong( mMobileBCRApp.SKDOperRfId, 16);

                    HashMap h= mMobileBCRApp.getmDbHelper().getSKDOperator(String.valueOf(i));
                    String operator=(String)h.get("operator");
                    if (operator==null) {
                        if (mMobileBCRApp.NetErr == true)
                            intent.setClass(StartScreen.this, NetError.class);
                        else
                            intent.setClass(StartScreen.this, ErrorLogin.class);
                        startActivity(intent);
                    }else
                    {
                        mMobileBCRApp.SKDOperator=operator;
                        Log.d("Is OK","Is OK");
                        intent.setClass(StartScreen.this, AccLogin.class);

                        startActivity(intent);

                        setContentView(R.layout.activity_start_screen);
                        Button KPPbutton=(Button)findViewById(R.id.SetKPP);
                        KPPbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_aut_state)); //setBackgroundResource
                        KPPbutton.setTextColor(Color.rgb(255,255,255));
                        Button Logbutton=(Button)findViewById(R.id.Loginbutton);
                        Logbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact)); //setBackgroundResource
                        Logbutton.setTextColor(Color.rgb(0, 0, 0));/*(Color.rgb(65, 169, 4));*/
                        //      Logbutton.setText(Html.fromHtml(/*getResources().getString(R.string.seccabstr)*/"<b>Личный кабинет</b><br><br><sup><small>Нажмите для информации</small></sup>"));
                        mMobileBCRApp.SKDStep = "2";
                    }
                    //  setContentView(R.layout.error_l);
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //   showProgress(false);****************************************************тоже
        }


    }

    public void StartScreen ()
    {
        Button KPPbutton=(Button)findViewById(R.id.SetKPP);
        Button Logbutton=(Button)findViewById(R.id.Loginbutton);
        Button Scanbutton=(Button)findViewById(R.id.ScanBtn);
        Button Exitbutton=(Button)findViewById(R.id.ExitBtn);

        if (mMobileBCRApp.SKDStep!="1") {
            //  Logbutton.setText(Html.fromHtml(/*getResources().getString(R.string.seccabstr)*/"<b>Личный кабинет</b><br><br><sup><small>Нажмите для информации</small></sup>"));
            Logbutton.setText(Html.fromHtml(/*getResources().getString(R.string.seccabstr)*/"<b>Вход выполнен</b><br><br><sup><small>Выберите автопроезд </small></sup>"));
        }

               if (mMobileBCRApp.SKDStep=="1")
        {
            Logbutton.setText(Html.fromHtml(/*getResources().getString(R.string.seccabstr)*/"<b>Авторизоваться</b><br><br><sup><small>Поднесите личную карту к устройству</small></sup>"));

        }
        if (mMobileBCRApp.SKDStep=="3")
        {

            Scanbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_scan_state)); //setBackgroundResource
            Scanbutton.setTextColor(Color.rgb(255,255,255));

            //Exitbutton.setTextColor(Color.rgb(0,0,0));
        }
        Scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //         Log.d("Go KPP","Go KPP");
                /*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
                startActivityForResult(intent,1);*/

                scanBarcodeCustomOptions(view);
            }
        });

        KPPbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //         Log.d("Go KPP","Go KPP");
                if ((mMobileBCRApp.SKDStep == "2") || (mMobileBCRApp.SKDStep == "3")) {
                    Intent intent = new Intent(view.getContext(), SetKPPAct.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                }
            }
        });
        Exitbutton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              mMobileBCRApp.SKDStep = "1";
                                              mMobileBCRApp.SKDKPP = "Укажите КПП";
                                              mMobileBCRApp.SKDOperator = "Кто ВЫ?";
                                              finish();
                                              System.exit(0);
                                              // StartScreen();

                                          }
                                      }
        );
    }
    public void enableForegroundMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void scanBarcode(View view) {
        new IntentIntegrator((Activity)this).initiateScan();
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

    public void encodeBarcode(View view) {
        new IntentIntegrator(this).shareText("Test Barcode");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        mMobileBCRApp.idBarCode=result.getContents();
        /*временно присваиваем штриход в самовывоз*/
        mMobileBCRApp.SKDRfId=result.getContents();
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show();
            } else {
                mMobileBCRApp.BarCodeR="";
                Intent intent = new Intent();
                /*Уходим на другой экран*/
                intent.setClass(StartScreen.this, TOneForm.class);
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
