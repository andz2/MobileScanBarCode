package ru.xxmmk.mobilescanbarcode;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        final TextView subHeader1 = (TextView) someView.findViewById(R.id.idSubheader1); //штрих код
        ImageView arrow =   (ImageView) someView.findViewById(R.id.descrItem);
        //Устанавливаем в каждую текствьюшку соответствующий текст
        // сначала заголовок
        header.setText(data.get(i).header);
        // потом подзаголовок
        subHeader.setText(data.get(i).subHeader);
        // потом подзаголовок 1
        subHeader1.setText(data.get(i).subHeader1);
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context,ItemInfo.class);
                context.startActivity(intent);
                Log.d("1","click!!!!!!!! "+subHeader1.getText().toString());
            }});

        if (data.get(i).isChecked.equals("1")) //(bc.contains(";"+this.data.get(i).subHeader1+";"))
        {
            Log.i("change color",data.get(i).subHeader1);
            someView.setBackgroundColor(context.getResources().getColor(R.color.bg_green));
            //Color.parseColor("#2D3E50")
            subHeader.setTextColor(Color.parseColor("white"));
            subHeader1.setTextColor(Color.parseColor("white"));
        }
        else
        {
            someView.setBackgroundColor(Color.WHITE);
            subHeader.setTextColor(Color.parseColor("black"));
            subHeader1.setTextColor(Color.parseColor("black"));
        }
        return someView;
    }
}