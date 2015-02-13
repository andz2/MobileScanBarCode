package ru.xxmmk.mobilescanbarcode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zemljanskij111614 on 06.02.2015.
 */
public class MobileBCRDB extends SQLiteOpenHelper {
    Context mContext;
    private MobileBCRApp mMobileSKDApp;

    public MobileBCRDB(Context context) {
        // конструктор суперкласса
        super(context, "SKDDB", null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(((MobileBCRApp) mContext).getLOG_TAG(), "MobileSKDDB.onCreate");
        // создаем таблицу с полями
    /*
        //люди и точечные доступы
        db.execSQL("create table skd_people_acc ("
                + "rfid integer ,"
                + "employee_number text,"
                + "acc_level_id integer,"
                + "acc_kpp text,"
                + "nm integer,"
                + "person_id integer,"
                + "cardholder_id integer"
                + ");");

        //кпп и уровни доступа
        db.execSQL("create table xxhr_skd_dev_acc ("
                + "acclev_id integer  primary key,"
                + "dev_str text,"
                + "acc_name text"
                + ");");

        //события
        db.execSQL("create table xxhr_skd_mobile_history ("
                + "person_id integer,"
                + "rf_id text,"
                + "operator text,"
                + "kpp text,"
                + "dt text,"
                + "rest text,"
                + "kpp_type text"
                + ");");
*/
        //карты охранников
        db.execSQL("create table xxhr_skd_operator ("
                + "operator text,"
                + "rf_id text"
                + ");");

        //мобильные проходные
        db.execSQL("create table xxhr_skd_objects ("
                + "kpp_name text,"
                + "skd_kpp text,"
                + "sort_kpp integer"
                + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*
        if (newVersion == 2) {
            Log.d(((MobileSKDApp)mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion="+newVersion);
            db.execSQL("create table new_code ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "code text" + ");");
        }

        if (newVersion == 3) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table orgs ("
                    + "id integer primary key autoincrement,"
                    + "org_id text,"
                    + "org_code text" + ");");
            db.execSQL("insert into settings (key) values ('orgs_date');");
        }
        }*/
    }
    public HashMap<String,String> getSKDOperator(String rfID)
    {
        HashMap<String,String> returnList =  new HashMap<String,String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();
        c = db.rawQuery("select operator from xxhr_skd_operator where rf_id=?",new String[] {rfID } );
        if (c.moveToFirst()) {
            do {
                //c.moveToFirst();
                returnList.put("operator", c.getString(0));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
        return returnList;
    }
    public ArrayList<HashMap<String,String>> getSKDMobDev()
    {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        //  HashMap<String, String> temp = new HashMap<String, String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();
        c = db.rawQuery("select kpp_name ,skd_kpp ,sort_kpp from xxhr_skd_objects order by sort_kpp",null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("kpp_name", c.getString(0));
             /*   temp.put("skd_kpp", c.getString(1));
                temp.put("sort_kpp", c.getString(2));*/
                returnList.add(temp);
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
        return returnList;
    }
}
