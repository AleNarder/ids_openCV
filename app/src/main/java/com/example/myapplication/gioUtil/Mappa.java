package com.example.myapplication.gioUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Mappa {

    Context context;
    LinearLayout ll_mappa, ll_score;
    TextView tv_score;

    public Mappa(Context context, LinearLayout ll1){
        this.context = context;
        this.ll_mappa = ll1;
    }

    public Mappa(Context context, LinearLayout ll1, LinearLayout ll2, TextView tv){
        this.context = context;
        this.ll_mappa = ll1;
        this.ll_score = ll2;
        this.tv_score = tv;
    }

    public void creaMap(ArrayList<Mina> l, int n, int m){
        for(int i=0;i<n;i++){
            addButton(l, i, m);
        }
    }

    public void creaMap(ArrayList<Mina> l, int n, int m, int score){

        tv_score = new TextView(context);
        tv_score.setText(score);

        for(int i=0;i<n;i++){
            addButton(l, i, m);
        }
    }

    public void addButton(ArrayList<Mina> l, int n, int m){
        LinearLayout ll2 = new LinearLayout(context);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams ll_params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll2.setLayoutParams(ll_params);
        ll_mappa.addView(ll2);

        for (int i = 0; i < m; i++) {
            Button btn = new Button(context);

            LinearLayout.LayoutParams b_params = new LinearLayout.LayoutParams(0, 100, 1);
            btn.setLayoutParams(b_params);
            ll2.addView(btn);

            for(int j=0;j<l.size();j++) {

                int x = l.get(j).getPosition().getRow();
                int y = l.get(j).getPosition().getCol();
                btn.setText(""+n+","+i);
                String color = l.get(j).getColor();

                if(color==null)
                    color="green";

                if (n == x && i == y) {
                    switch (color) {
                        case "red":
                            btn.setBackgroundColor(Color.RED);
                            break;
                        case "yellow":
                            btn.setBackgroundColor(Color.YELLOW);
                            break;
                        case "blue":
                            btn.setBackgroundColor(Color.BLUE);
                            break;
                        case "green":
                            btn.setBackgroundColor(Color.GREEN);
                            break;
                    }
                }
            }

            final int x2 = i;
            btn.setOnClickListener(v -> {
                Toast.makeText(context, "["+n+","+x2+"]", Toast.LENGTH_LONG).show();
            });
        }
    }

}