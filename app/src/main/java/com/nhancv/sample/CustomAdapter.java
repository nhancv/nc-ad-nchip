package com.nhancv.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nhancao on 11/15/16.
 */

public class CustomAdapter extends ArrayAdapter<Obj> {

    public CustomAdapter(Context context, List<Obj> objs) {
        super(context, 0, objs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Obj obj = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_drop_down_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTx1 = (TextView) convertView.findViewById(R.id.tvTx1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTx1.setText(obj.name);
        return convertView;
    }

    private static class ViewHolder {
        TextView tvTx1;
    }
}
