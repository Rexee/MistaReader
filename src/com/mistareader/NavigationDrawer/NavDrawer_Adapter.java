package com.mistareader.NavigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistareader.R;

import java.util.ArrayList;

public class NavDrawer_Adapter extends ArrayAdapter<NavDrawer_MenuItem> {

    private LayoutInflater inflater;

    public NavDrawer_Adapter(Context context, int textViewResourceId, ArrayList<NavDrawer_MenuItem> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        NavDrawer_MenuItem menuItem = this.getItem(position);
        if (menuItem.isSection) {
            view = getSectionView(convertView, parent, menuItem);
        }
        else {
            view = getItemView(convertView, parent, menuItem);
        }
        return view;
    }

    public View getItemView(View convertView, ViewGroup parentView, NavDrawer_MenuItem navDrawerItem) {

        NavMenuItemHolder holder;
        View v;

        if (convertView == null) {
            v = inflater.inflate(R.layout.navdrawer_item, parentView, false);

            holder = new NavMenuItemHolder();

            holder.labelView = (TextView) v.findViewById(R.id.navmenuitem_label);
            holder.iconView = (ImageView) v.findViewById(R.id.navmenuitem_icon);
            holder.counterView = (TextView) v.findViewById(R.id.navmenuitem_counter);

            v.setTag(holder);
        }
        else {
            v = convertView;
            holder = (NavMenuItemHolder) v.getTag();
        }

        holder.labelView.setText(navDrawerItem.label);
        holder.iconView.setImageResource(navDrawerItem.icon);
        if (navDrawerItem.newSubs == 0) {
            holder.counterView.setVisibility(View.GONE);
        }

        else {
            holder.counterView.setVisibility(View.VISIBLE);
            holder.counterView.setText(""+navDrawerItem.newSubs);
        }

        // if (menuItem.isExpandable()) {
        // labelView.setTextColor(convertView.getResources().getColor(R.color.LightGray));
        // } else {
        // labelView.setTextColor(convertView.getResources().getColor(R.color.White));
        //
        // }

        return v;
    }

    public View getSectionView(View convertView, ViewGroup parentView, NavDrawer_MenuItem navDrawerItem) {

        NavMenuSectionHolder holder;
        View v;

        if (convertView == null) {
            v = inflater.inflate(R.layout.navdrawer_section, parentView, false);
            holder = new NavMenuSectionHolder();
            holder.labelView = (TextView) v.findViewById(R.id.navmenusection_label);
            v.setTag(holder);

        }
        else {
            v = convertView;
            holder = (NavMenuSectionHolder) v.getTag();
        }
        
        holder.labelView.setText(navDrawerItem.label);

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.getItem(position).isSection) 
            return 1;
        else
            return 0;
    }

    private static class NavMenuItemHolder {
        public TextView  counterView;
        public TextView  labelView;
        public ImageView iconView;
    }

    private class NavMenuSectionHolder {
        private TextView labelView;
    }

}