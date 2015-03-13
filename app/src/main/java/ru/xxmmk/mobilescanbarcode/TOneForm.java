package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;


public class TOneForm extends Activity {
    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    ArrayList<String> data = new ArrayList<String>();
    ListView lv;
    ArrayList<T1Item> dataLV = new ArrayList<T1Item>();
    private PrintScanData mPrintDataTask = null;
    ProgressDialog pd;
    String ReadCode; //локальная переменная штрих кода

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_form);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());
        mMobileBCRApp.Scant1=false;
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(true);
        myAB.setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //политика сетевого доступа
        StrictMode.setThreadPolicy(policy); //применяем политику
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.abc_ic_cab_done_holo_dark/*R.drawable.block*/))
                .withButtonColor(Color.RED)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withButtonSize(92)
                .withMargins(0, 0, 10,480)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                Intent intent = new Intent();
                intent.setClass(TOneForm.this, ResultT1.class);

                startActivity(intent);
            }
        });
        Button EntCode = (Button) findViewById(R.id.EntCode);
        EntCode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(TOneForm.this, EnterCode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        pd = new ProgressDialog(TOneForm.this);
        pd.setMessage("Дождитесь окончания загрузки...");
/*      dataLV.add(new T1Item("Заголовок1","Подзаголовок1","Подзаголовок1-1","0"));
        dataLV.add(new T1Item("Заголовок2","Подзаголовок2","Подзаголовок2-1","1"));
        mMobileBCRApp.dataLV.clear();*/
        GetT1Data();

        //GetT1(mMobileBCRApp.idBarCode);

        lv = (ListView) this.findViewById(R.id.listView);

    }


    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundMode();
        //   disableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        if (!mMobileBCRApp.isChancel){
        GetT1Data();}
        else
            mMobileBCRApp.isChancel=false;


    }


    @Override
    protected void onNewIntent(Intent intent) {
        // Log.d("Intent", "Считываем nfc");
    }

    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.menu_tone_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // обработка нажатий в actionbar
        finish();
/*        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    //запуск сканирования
    public void scanBarcodeCustomOptions(View view) {
        //    Toast.makeText(this, "Сканирование штрих кодов запрещено", Toast.LENGTH_LONG).show();
        if (1==1) { //в условии необходимо добавить проверку на шаг
          //  mMobileBCRApp.dataLV.clear();
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.autoWide();
            integrator.initiateScan();
        }
    }
    public void encodeBarcode(View view) {
        new IntentIntegrator(this).shareText("Test Barcode");
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show();
            } else {
                /*временно присваиваем штриход в строку с элементами*/
                mMobileBCRApp.BarCodeR=mMobileBCRApp.BarCodeR+";"+result.getContents()+";";
            //    ReadCode=result.getContents();
                Intent intent = new Intent();
                finish();
                /*рефрешим экран*/
                intent.setClass(TOneForm.this, TOneForm.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//запуск асинхронного таска
    public void GetT1Data() {
        if (mPrintDataTask != null) {
            return;
        }
        boolean cancel = false;
        View focusView = null;
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mPrintDataTask = new PrintScanData(mMobileBCRApp.SKDRfId);
            mPrintDataTask.execute((Void) null);
        }
    }

    public class PrintScanData extends AsyncTask<Void, Void, Boolean> {
        private String mToken = "null";

        PrintScanData(String rfId) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            GetT1(mMobileBCRApp.T1BarCode);
            return true;
        }
//обработка результата сканирования/ручного ввода
        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            mPrintDataTask = null;
            if (mMobileBCRApp.NetErr == true) {
                finish();
                Intent intent = new Intent();
                intent.setClass(TOneForm.this, NetError.class);
                startActivity(intent);
            }
            else {
                // showProgress(false); ******************************************************
                TextView DriverFio = (TextView) findViewById(R.id.drN);
                TextView AutoNum = (TextView) findViewById(R.id.autoN);
                TextView NumT1 = (TextView) findViewById(R.id.NumT1);

                if (mMobileBCRApp.T1Num.equals("Не найдено, код неверен")) {
                    NumT1.setText(mMobileBCRApp.T1Num);
                    AutoNum.setText(" ");
                    DriverFio.setText(" ");
                } else {
                    NumT1.setText("Номер T1:  " + mMobileBCRApp.T1Num);
                    AutoNum.setText("Номер автомобиля:  " + mMobileBCRApp.T1Auto);
                    DriverFio.setText("ФИО водителя:  " + mMobileBCRApp.T1Driver);
                }
                String allCode = "";
//пробегаемся по массиву итемов и записываем найденный код
                for (int i = 0; i < mMobileBCRApp.dataLV.size(); i++) {
                    allCode=allCode+ ";" +  mMobileBCRApp.dataLV.get(i).getSubHeader1()+ ";";
                    if (mMobileBCRApp.BarCodeR.contains(";" + mMobileBCRApp.dataLV.get(i).getSubHeader1() + ";")) {
                        mMobileBCRApp.dataLV.get(i).setChecked("1");
                    }
                }
//добавление отсутсвующих в списке
               /* if (!allCode.contains(";" + ReadCode + ";") && mMobileBCRApp.BarCodeR.length()>=1)
                {
                    mMobileBCRApp.dataLV.add(new T1Item("Отсутствует в Т-1","Неверный ШК",ReadCode,"2","","","","",""));
                }*/

                lv.setAdapter(new MyAdapter(TOneForm.this, mMobileBCRApp.dataLV));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position,
                                            long id) {
                        Log.d("1", "click lv pos=" + mMobileBCRApp.dataLV.get(position).getSubHeader1() + ";");
                    }
                });
            }
        }
        @Override
        protected void onCancelled() {
            mPrintDataTask = null;
               showProgress(false);
        }
    }

    public void GetT1(String T1BC) {
        mMobileBCRApp.dataLV.clear();
        Boolean vStatus = false;
        mMobileBCRApp.NetErr = false;
        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileBCRApp.getT1HeaderDataURL(T1BC));
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
                        mMobileBCRApp.T1Driver = "Не найдено, код неверен";
                        mMobileBCRApp.T1Auto = "Не найдено, код неверен";
                        mMobileBCRApp.T1Header = "Не найдено, код неверен";
                        mMobileBCRApp.T1Num = "Не найдено, код неверен";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mMobileBCRApp.T1Driver = jsonObject.getString("FIODRIVER");
                            mMobileBCRApp.T1Auto = jsonObject.getString("NUMAUTO");
                            mMobileBCRApp.T1Header = jsonObject.getString("DELIVNUM");
                            mMobileBCRApp.T1Num = jsonObject.getString("T1NUM");
                            //mMobileBCRApp.dataLV.add(new T1Item (jsonObject.getString("PRODUCTNAME"),jsonObject.getString("CERNNUM"),jsonObject.getString("BARCODE"),"0"));
                            vStatus = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("not ok", "not ok");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                mMobileBCRApp.NetErr = true;
            }
            Thread.sleep(10);
            if (vStatus) {
//                        return true;
                Log.d("ok", "ok");
            } else {
                Log.d("not ok", "not ok");
//                        return false;
            }

        } catch (InterruptedException e) {
            Log.d("not ok", "not ok");
//                    return false;
        }

        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileBCRApp.getT1ItemDataURL(mMobileBCRApp.T1Num));
            //  Log.d(mMobileBCRApp.getLOG_TAG(), mMobileBCRApp.ListCargoItems);
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
                            mMobileBCRApp.dataLV.add(new T1Item(jsonObject.getString("PRODUCTNAME")
                                                                , jsonObject.getString("CERNNUM")
                                                                , jsonObject.getString("BARCODE")
                                                                , "0"
                                                                , jsonObject.getString("PLAVKNUM")
                                                                , jsonObject.getString("PARTNUM")
                                                                , jsonObject.getString("PACKNUM")
                                                                , jsonObject.getString("WEIGHTC")
                                                                , jsonObject.getString("LONGNAME")));
                            //   data.add(jsonObject.getString("KPP_NAME"));
                            vStatus = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("not ok", "not ok");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                mMobileBCRApp.NetErr = true;
            }
            Thread.sleep(10);
            if (vStatus) {
//                        return true;
                Log.d("ok", "ok");
            } else {
                Log.d("not ok", "not ok");
//                        return false;
            }

        } catch (InterruptedException e) {
            Log.d("not ok", "not ok");
//                    return false;
        }
//            return false;
    }
    public void showProgress(Boolean flag) {
        if (flag) {
            pd = new ProgressDialog(TOneForm.this);
            pd.setMessage("Дождитесь окончания загрузки...");
            pd.show();
        } else
        {
            try
            {
                if ((this.pd != null) && this.pd.isShowing()) {
                    this.pd.dismiss();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                this.pd = null;
            }
        }
    }

}
