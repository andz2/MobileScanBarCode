package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class EnterCode extends Activity {
    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    private String EntText;
    public  String HeaderNum="Введите номер шк"; //заголовок в цифровом поле

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(true);
        myAB.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView Header =(TextView) findViewById(R.id.textNumId);
        Header.setText(HeaderNum);

        Button Num1 = (Button) findViewById(R.id.button1);
        Num1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("1");
            }
        });
        Button Num2 = (Button) findViewById(R.id.button2);
        Num2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("2");
            }
        });
        Button Num3 = (Button) findViewById(R.id.button3);
        Num3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("3");
            }
        });
        Button Num4 = (Button) findViewById(R.id.button4);
        Num4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("4");
            }
        });
        Button Num5 = (Button) findViewById(R.id.button5);
        Num5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("5");
            }
        });
        Button Num6 = (Button) findViewById(R.id.button6);
        Num6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("6");
            }
        });
        Button Num7 = (Button) findViewById(R.id.button7);
        Num7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("7");
            }
        });
        Button Num8 = (Button) findViewById(R.id.button8);
        Num8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("8");
            }
        });
        Button Num9 = (Button) findViewById(R.id.button9);
        Num9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("9");
            }
        });
        Button Num0 = (Button) findViewById(R.id.button0);
        Num0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NumBtnEnt ("0");
            }
        });
        Button Entr = (Button) findViewById(R.id.ent);

        Entr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Enter ();
            }
        });
        Button Bk = (Button) findViewById(R.id.backspace);
        Bk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BackSpace();
            }
        });

        Button Ch = (Button) findViewById(R.id.EnterChancel );
        Ch.setOnClickListener( new View.OnClickListener()
        { public  void onClick (View v)
                                   {
                                       finish();
                                       mMobileBCRApp.isChancel=true;
                                   }

                               });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //enableForegroundMode();
        //   disableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_enter_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/      finish();
        return super.onOptionsItemSelected(item);
    }
    public void NumBtnEnt (String num)
    {
        TextView NumT = (TextView) findViewById(R.id.textNumId);
        if (NumT.getText().equals(HeaderNum))
        {
            NumT.setText("");
        }
        EntText=NumT.getText()+num;
        NumT.setText(EntText);

    }
    public void BackSpace ()
    {
        TextView NumT = (TextView) findViewById(R.id.textNumId);
        if (!NumT.getText().equals(HeaderNum)) {
            if (NumT.getText().length()!=0) {
                EntText = (String) NumT.getText();
                EntText = EntText.substring(0, EntText.length() - 1);
                NumT.setText(EntText);
            }
            else
            { NumT.setText(HeaderNum);  }
        }
    }
    public void Enter ()
    {
        mMobileBCRApp.CurrBC=EntText;
        mMobileBCRApp.BarCodeR=mMobileBCRApp.BarCodeR+";"+EntText+";";
        finish();
    }
}
