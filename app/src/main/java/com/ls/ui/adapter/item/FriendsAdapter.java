package com.ls.ui.adapter.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ls.drupalcon.R;
import com.ls.ui.view.FontTextView;

import java.util.ArrayList;


public class FriendsAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<String> data;

    public FriendsAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_header, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (FontTextView) convertView.findViewById(R.id.txtTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(data.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView text;

    }
}
