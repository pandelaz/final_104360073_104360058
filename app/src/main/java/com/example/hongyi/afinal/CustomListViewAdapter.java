package com.example.hongyi.afinal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by hongyi on 2017/12/31.
 */


public class CustomListViewAdapter extends SimpleAdapter {

    Context c;

    public CustomListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        c = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position,convertView,parent);

        TextView tview = (TextView) view.findViewById(R.id.tv3);

        String ClassStatus = tview.getText().toString();

        if(ClassStatus.equals("沒課")) {

            TextView topview = (TextView) view.findViewById(R.id.textView5);
            topview.setBackgroundColor(c.getResources().getColor(R.color.colorNoClass));

            LinearLayout l = (LinearLayout) view.findViewById(R.id.l2);
            TextView secview = (TextView) view.findViewById(R.id.tv2);
            l.setVisibility(View.GONE);
            secview.setVisibility(View.GONE);


        } else {

            TextView topview = (TextView) view.findViewById(R.id.textView5);
            topview.setBackgroundColor(c.getResources().getColor(R.color.colorAccent));

            LinearLayout l = (LinearLayout) view.findViewById(R.id.l2);
            TextView secview = (TextView) view.findViewById(R.id.tv2);
            l.setVisibility(View.VISIBLE);
            secview.setVisibility(View.VISIBLE);

        }

        return view;


    }



    @Override
    public int getCount() {
        return super.getCount();
    }

}