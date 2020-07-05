package com.example.virusattack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context context;
    ArrayList<Player> arrayList;

    public MyAdapter(Context context, ArrayList<Player> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.my_records_list_view, null);
        TextView t1_rank = (TextView)convertView.findViewById(R.id.rank_lbl);
        TextView t2_score = (TextView)convertView.findViewById(R.id.score_lbl);
        TextView t3_name = (TextView)convertView.findViewById(R.id.name_lbl);
        Player player = arrayList.get(position);
        t1_rank.setText(String.valueOf(position+1));
        t2_score.setText(String.valueOf(player.getScore()));
        t3_name.setText(player.getName());
        return convertView;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }
}
