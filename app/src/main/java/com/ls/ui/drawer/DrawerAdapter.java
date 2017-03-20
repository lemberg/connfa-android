package com.ls.ui.drawer;

import com.ls.drupalcon.R;
import com.ls.drupalcon.app.App;
import com.ls.utils.L;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {
    private List<DrawerMenuItem> menu;
    private LayoutInflater inflater;
    private int selectedPos = 0;

    public DrawerAdapter(Context theContext, List<DrawerMenuItem> theMenu) {
        inflater = (LayoutInflater) theContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menu = theMenu;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    @Override
    public int getCount() {
        return menu.size();
    }

    @Override
    public DrawerMenuItem getItem(int position) {
        return menu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DrawerMenuItem item = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_drawer, parent, false);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.divider =  convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtName.setText(item.getName());
        if (position == selectedPos) {
            holder.image.setImageResource(item.getSelIconRes());
            holder.txtName.setTextColor(App.getContext().getResources().getColor(R.color.item_selection));
        } else {
            holder.image.setImageResource(item.getIconRes());
            holder.txtName.setTextColor(App.getContext().getResources().getColor(R.color.grey_200));
        }


        if (item.isGroup()) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        private TextView txtName;
        private ImageView image;
        private  View divider;
    }
}
