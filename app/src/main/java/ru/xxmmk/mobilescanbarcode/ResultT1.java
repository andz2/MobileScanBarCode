package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

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


public class ResultT1 extends Activity {
    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    public Boolean isOk=false;
    public Boolean incorrectR= false;
    public Boolean notAll = false;
    ProgressDialog pd;
    private SaveAudit mSaveAudit = null;
    private String pRes; //переменная разрешаем/запрещеаем выезд

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_t1);
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
        TextView comment =(TextView) findViewById(R.id.ResultComment);
        comment.setText("");
        String res;

        String tmp; //временная переменная для хранения состояния итема Т-1
        Spannable WordtoSpan=new SpannableString("");
        Spanned z=new SpannableString("");
         isOk=false;
         incorrectR= false;
         notAll = false;

        for (int i = 0; i < mMobileBCRApp.dataLV.size(); i++) {

                tmp=mMobileBCRApp.dataLV.get(i).getChecked();
                switch(Integer.parseInt(tmp)){
                case 1: res="Штрих код найден";
                    WordtoSpan= new SpannableString(mMobileBCRApp.dataLV.get(i).getSubHeader1()+": "+res+" \n");
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#16a085")), 0, WordtoSpan.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isOk=true;
                    break;
                case 2: res="Штрих код не найден в Т-1";
                    WordtoSpan= new SpannableString(mMobileBCRApp.dataLV.get(i).getSubHeader1()+": "+res+" \n");
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, WordtoSpan.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    incorrectR= true;
                        break;

                default: res = "Штрих код не считан";
                    WordtoSpan= new SpannableString(mMobileBCRApp.dataLV.get(i).getSubHeader1()+": "+res+" \n");
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, WordtoSpan.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     notAll = true;
                    break;
                }

            z=(Spanned) TextUtils.concat(z, WordtoSpan);
          //  comment.setText(comment.getText()+mMobileBCRApp.dataLV.get(i).getSubHeader1()+": "+res+" \n");
        }
        comment.setText(z);
        TextView r=(TextView) findViewById(R.id.ResultText);
        if (isOk)      { r.setText(getResources().getString(R.string.all_ok_item)); }
        if (incorrectR){ r.setText(getResources().getString(R.string.incorrect_item)); }
        if (notAll)    { r.setText(getResources().getString(R.string.no_all_item)); }
        Button chButton =(Button) findViewById(R.id.ResultCh);
        chButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                Intent intent = new Intent();
                intent.setClass(ResultT1.this, TOneForm.class);

                startActivity(intent);
            }
        });
        //кнопка разрешить
        Button resOk =(Button) findViewById(R.id.ResultOk);
        resOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 if (incorrectR||notAll)
                        {
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ResultT1.this);
                            dlgAlert.setMessage("Считанные данные не соответствуют 1-Т");
                            dlgAlert.setTitle("Ошибка");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.create().show();
                        }
                else {
                     showProgress(true);
                     pRes="Y";
                     mSaveAudit = new SaveAudit(mMobileBCRApp.SKDRfId);
                     mSaveAudit.execute((Void) null);
                 }
            }
        });
        Button resErr =(Button) findViewById(R.id.ResultErr);
        resErr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //запрещаем выезд
                showProgress(true);
                pRes="N";
                mSaveAudit = new SaveAudit(mMobileBCRApp.SKDRfId);
                mSaveAudit.execute((Void) null);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_result_t1, menu);
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
    public void SetAuditData (String T1Bc,String pRes) {


        mMobileBCRApp.dataLV.clear();
        Boolean vStatus = false;
        mMobileBCRApp.NetErr = false;
        //сохраняем аудит 1-Т
        try {
            mMobileBCRApp.AuditSeq = "-1";
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileBCRApp.getT1AuditHeaderURL(T1Bc,pRes));
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
                            mMobileBCRApp.AuditSeq = jsonObject.getString("TSQEQ");
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
        if (!mMobileBCRApp.AuditSeq.equals("-1")) {
            //здесь откроем цикл
            for (int t = 0; t < mMobileBCRApp.dataLV.size(); t++) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(mMobileBCRApp.getT1AuditLinesURL(mMobileBCRApp.AuditSeq, T1Bc, mMobileBCRApp.dataLV.get(t).getSubHeader1(), pRes));
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
                                    //возьмём результат
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
            }
//                     return false;
        }
        //если -1 то выведу ругачку
    }
    public class  SaveAudit extends AsyncTask<Void, Void, Boolean> {
        private String mToken = "null";

        SaveAudit(String rfId) {

        }

        @Override
        protected Boolean doInBackground(Void... params) { //сохранямся
                SetAuditData(mMobileBCRApp.T1BarCode,pRes);
            return true;
        }

        //обработка результата сохранения
        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            mSaveAudit = null;
            mMobileBCRApp.dataLV.clear();
            mMobileBCRApp.BarCodeR = "";
            finish();
            mMobileBCRApp.TwoActFlag = true;
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setClass(ResultT1.this, SetT1.class);
            startActivity(intent);
        }
        @Override
        protected void onCancelled() {
            mSaveAudit = null;
            showProgress(false);
        }
    }
    public void showProgress(Boolean flag) {
        if (flag) {
            pd = new ProgressDialog(ResultT1.this);
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
