package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ResultT1 extends Activity {
    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

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
        Boolean isOk=false;
        Boolean incorrectR= false;
        Boolean notAll = false;

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
        Button resOk =(Button) findViewById(R.id.ResultOk);
        resOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMobileBCRApp.dataLV.clear();
                mMobileBCRApp.BarCodeR="";
                finish();
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setClass(ResultT1.this, SetT1.class);
                startActivity(intent);
            }
        });
        Button resErr =(Button) findViewById(R.id.ResultErr);
        resErr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMobileBCRApp.dataLV.clear();
                mMobileBCRApp.BarCodeR="";
                finish();
                Intent intent = new Intent();
                intent.setClass(ResultT1.this, SetT1.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_t1, menu);
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
}
