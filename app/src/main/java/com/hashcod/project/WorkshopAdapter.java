package com.hashcod.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alen on 08-May-16.
 */
public class WorkshopAdapter extends BaseAdapter {
    ArrayList<DetailsClass>arrayList=new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;

    public WorkshopAdapter(Context context, ArrayList<DetailsClass>arrayList) {
        this.context=context;
        this.arrayList=arrayList;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView=layoutInflater.inflate(R.layout.single_item,null);

        TextView title=(TextView)rootView.findViewById(R.id.titlesingle);
        TextView desc=(TextView)rootView.findViewById(R.id.descsinge);
        TextView distance=(TextView)rootView.findViewById(R.id.distance);

        title.setText(arrayList.get(position).getName());
        desc.setText(arrayList.get(position).getAddress());
        distance.setText(arrayList.get(position).getDistance()+" Km's Away");

        return rootView;
    }
}
