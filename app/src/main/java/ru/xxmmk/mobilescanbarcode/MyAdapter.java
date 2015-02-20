package ru.xxmmk.mobilescanbarcode;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

    ArrayList<T1Item> data = new ArrayList<T1Item>();
    Context context;

    public MyAdapter(Context context, ArrayList<T1Item> arr) {
        if (arr != null) {
            data = arr;
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int num) {
        // TODO Auto-generated method stub
        return data.get(num);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }
    public void onClick(View v) {

    }

    @Override
    public View getView(int i, View someView, ViewGroup arg2) {
        //Получение объекта inflater из контекста
        LayoutInflater inflater = LayoutInflater.from(context);
        //Если someView (View из ListView) вдруг оказался равен
        //null тогда мы загружаем его с помошью inflater
        if (someView == null) {
            someView = inflater.inflate(R.layout.item, arg2, false);
        }
        //Обявляем наши текствьюшки и связываем их с разметкой
        TextView header = (TextView) someView.findViewById(R.id.idHeader);
        TextView subHeader = (TextView) someView.findViewById(R.id.idSubheader);
        TextView subHeader1 = (TextView) someView.findViewById(R.id.idSubheader1);

        //Устанавливаем в каждую текствьюшку соответствующий текст
        // сначала заголовок
        header.setText(data.get(i).header);
        // потом подзаголовок
        subHeader.setText(data.get(i).subHeader);
        // потом подзаголовок 1
        subHeader1.setText(data.get(i).subHeader1);


        return someView;
    }




}