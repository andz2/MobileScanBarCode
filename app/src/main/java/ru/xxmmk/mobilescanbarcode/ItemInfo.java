package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ItemInfo extends Activity {
    private MobileBCRApp mMobileBCRApp;
    private String stat ="";
    private int tmpPos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView bc = (TextView) findViewById(R.id.ItemBc);
        bc.setText("Штрикод: "+mMobileBCRApp.CurrBC);
        TextView nm = (TextView) findViewById(R.id.ItemNm);
        TextView pl = (TextView) findViewById(R.id.ItemPlavkN);
        TextView pc = (TextView) findViewById(R.id.ItemPackNum);
        TextView part = (TextView) findViewById(R.id.ItemPartNum);
        TextView w = (TextView) findViewById(R.id.ItemWeight);

        for (int i = 0; i < mMobileBCRApp.dataLV.size(); i++) {
            if ((";"+mMobileBCRApp.CurrBC+";").contains(";"+mMobileBCRApp.dataLV.get(i).getSubHeader1()+";")) {
                nm.setText(getString(R.string.Nm)+": "+mMobileBCRApp.dataLV.get(i).getLongName());
                pl.setText(getString(R.string.PlavkN)+": "+mMobileBCRApp.dataLV.get(i).getPlavkN());
                pc.setText(getString(R.string.PackNum)+": "+mMobileBCRApp.dataLV.get(i).getPackNum());
                part.setText(getString(R.string.PartNum) + ": " + mMobileBCRApp.dataLV.get(i).getPartNum());
                w.setText(getString(R.string.Weight) + ": " + mMobileBCRApp.dataLV.get(i).getWeight());
                stat=mMobileBCRApp.dataLV.get(i).getChecked();
                tmpPos=i;
            }
        }

        Button Bk = (Button) findViewById(R.id.ItemBk);
        Bk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (scale + 0.5f);

        Button del =(Button) findViewById(R.id.BtnDel);
        if (stat.equals("2")) {
         // del.setVisibility(View.INVISIBLE);
            del.getLayoutParams().height = pixels*60;
        }
        else
        {
            del.getLayoutParams().height = 0;
        }

        del.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            mMobileBCRApp.isChancel=false;
                            mMobileBCRApp.dataLV.remove(tmpPos);
                            mMobileBCRApp.BarCodeR=mMobileBCRApp.BarCodeR.replace(";"+mMobileBCRApp.CurrBC+";",";");
                            mMobileBCRApp.CurrBC="";
                            tmpPos=-1;
                            finish();
                        }
                    }

        );
        LinearLayout lb =(LinearLayout)findViewById(R.id.linearLayoutBottom);
        lb.setMinimumHeight(60);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_info, menu);
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
