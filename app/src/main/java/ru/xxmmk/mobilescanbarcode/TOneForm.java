package ru.xxmmk.mobilescanbarcode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.HashMap;


public class TOneForm extends Activity  {
    private MobileBCRApp mMobileBCRApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    ArrayList<String> data = new ArrayList<String>();
    ListView lv;
    ArrayList<T1Item> dataLV = new ArrayList<T1Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_form);
        mMobileBCRApp = ((MobileBCRApp) this.getApplication());

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        dataLV.add(new T1Item("Заголовок1","Подзаголовок1","Подзаголовок1-1"));
        dataLV.add(new T1Item("Заголовок2","Подзаголовок2","Подзаголовок2-1"));
        dataLV.add(new T1Item("Заголовок3","Подзаголовок3","Подзаголовок3-1"));
        dataLV.add(new T1Item("Заголовок4","Подзаголовок4","Подзаголовок4-1"));
        dataLV.add(new T1Item("Заголовок5","Подзаголовок5","Подзаголовок5-1"));
        lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new MyAdapter(this, dataLV));

        lv.getAdapter().getView(2,lv.getChildAt(2),lv).setBackgroundColor(getResources().getColor(R.color.abc_search_url_text_holo));
        View v1 =getViewByPosition(2,lv);
        v1.setBackgroundColor(getResources().getColor(R.color.abc_search_url_text_holo));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position,
                                    long id) {
                Log.d("1","click lv pos="+position+lv.getItemAtPosition(position)+dataLV.get(position).getSubHeader1()+";");
            //    view.setBackgroundColor(getResources().getColor(R.color.abc_search_url_text_holo));
            }
        });

    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    protected void  onResume() {
        super.onResume();
        enableForegroundMode();
     //   disableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileBCRApp.SKDOperator);
        myAB.setSubtitle(mMobileBCRApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
    }



    @Override
    protected void onNewIntent(Intent intent) {
       // Log.d("Intent", "Считываем nfc");
    }
    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
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
    public void GetKpp ()
    {
        Boolean vStatus = false;
        mMobileBCRApp.NetErr=false;

        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileBCRApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileBCRApp.ListCargoItems);
            //Log.d(mMobileBCRApp.getLOG_TAG(), mMobileBCRApp.ListCargoItems);
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
                            data.add(jsonObject.getString("KPP_NAME"));
                            vStatus = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {
                    Log.d("not ok","not ok");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                mMobileBCRApp.NetErr=true;
            }

            Thread.sleep(10);
            if (vStatus) {
//                        return true;
                Log.d("ok","ok");
            } else {
                Log.d("not ok","not ok");
//                        return false;
            }

        } catch (InterruptedException e) {
            Log.d("not ok","not ok");
//                    return false;
        }
        if (mMobileBCRApp.NetErr)
        {
            ArrayList<HashMap<String, String>> mKpp = new ArrayList<HashMap<String, String>>();

            mKpp=mMobileBCRApp.getmDbHelper().getSKDMobDev();
            HashMap<String, String> m = mKpp.get(0);//берём первый элемент в массиве hashMap

            String strArr[] = new String[m.size()];


            int i = 0;
            for (HashMap<String, String> hash : mKpp) {
                for (String current : hash.values()) { //для каждого значения в строке хэшмапа
//                        strArr[i] = current;
                    data.add(current);
                    //   Log.d("!!"+current,"!!!!");
                    i++;
                }


            }
        }

//            return false;
    }

}
