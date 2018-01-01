package com.example.hongyi.afinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hongyi on 2017/12/13.
 */

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

    Context conText;
    LoveDBHelper dbhelper;

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        conText = context;
        dbhelper = new LoveDBHelper(conText);


    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // TODO Auto-generated method stub
        super.bindView(view, context, cursor);
    }

    @Override
    public View getView(final int position, View  convertView, ViewGroup parent) {

        final View vw = super.getView(position,convertView,parent);

        final ImageButton btn = (ImageButton) vw.findViewById(R.id.imageButton2);

        final TextView tvw = (TextView) vw.findViewById(R.id.setd);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceType", "ShowToast"})
            public void onClick(View v) {

                if(tvw.getText().toString().equals("1")) {

                    tvw.setText("0");
                    btn.setBackgroundResource(R.drawable.hw10);
                    TextView n = (TextView) vw.findViewById(R.id.named);
                    dbhelper.deleteLovebyName(n.getText().toString());

                    Toast.makeText(v.getContext(),"已刪除你對 " + n.getText().toString() + " 的最愛",Toast.LENGTH_SHORT).show();



                } else {
                    tvw.setText("1");
                    btn.setBackgroundResource(R.drawable.hw9);

                    TextView n = (TextView) vw.findViewById(R.id.named);
                    TextView d = (TextView) vw.findViewById(R.id.timed);
                    TextView u = (TextView) vw.findViewById(R.id.urld);

                    dbhelper.insertLove(n.getText().toString(),d.getText().toString(),u.getText().toString());

                    Toast.makeText(v.getContext(),"已新增你對 " + n.getText().toString() + " 的最愛",Toast.LENGTH_SHORT).show();

                }
            }
        });

        return vw;

    }


}
