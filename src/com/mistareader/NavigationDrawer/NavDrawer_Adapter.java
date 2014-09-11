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

public class NavDrawer_Adapter extends ArrayAdapter<NavDrawerMenuItem> {

	private LayoutInflater inflater;

	public NavDrawer_Adapter(Context context, int textViewResourceId, ArrayList<NavDrawerMenuItem> objects) {
		super(context, textViewResourceId, objects);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		NavDrawerMenuItem menuItem = this.getItem(position);
		if (menuItem.getType() == NavMenuItem.ITEM_TYPE) {
			view = getItemView(convertView, parent, menuItem);
		} else {
			view = getSectionView(convertView, parent, menuItem);
		}
		return view;
	}

	public View getItemView(View convertView, ViewGroup parentView, NavDrawerMenuItem navDrawerItem) {

		NavMenuItem menuItem = (NavMenuItem) navDrawerItem;
		ImageView iconView;
		TextView labelView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.navdrawer_item, parentView, false);
			labelView = (TextView) convertView.findViewById(R.id.navmenuitem_label);
			iconView = (ImageView) convertView.findViewById(R.id.navmenuitem_icon);

			NavMenuItemHolder navMenuItemHolder = new NavMenuItemHolder();
			navMenuItemHolder.labelView = labelView;
			navMenuItemHolder.iconView = iconView;

			convertView.setTag(navMenuItemHolder);
		}
		else
		{
			NavMenuItemHolder navMenuItemHolder = (NavMenuItemHolder) convertView.getTag();
			labelView = navMenuItemHolder.labelView;
			iconView = navMenuItemHolder.iconView;
		}

		labelView.setText(menuItem.getLabel());
		iconView.setImageResource(menuItem.getIcon());
		
//		if (menuItem.isExpandable()) {
//			labelView.setTextColor(convertView.getResources().getColor(R.color.LightGray));
//		} else {
//			labelView.setTextColor(convertView.getResources().getColor(R.color.White));
//
//		}

		return convertView;
	}

	public View getSectionView(View convertView, ViewGroup parentView, NavDrawerMenuItem navDrawerItem) {

		NavMenuSection menuSection = (NavMenuSection) navDrawerItem;
		TextView labelView;
		  
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.navdrawer_section, parentView, false);
			labelView = (TextView) convertView.findViewById(R.id.navmenusection_label);

			NavMenuSectionHolder navMenuItemHolder = new NavMenuSectionHolder();
			navMenuItemHolder.labelView = labelView;
			
			convertView.setTag(navMenuItemHolder);
		}
		else
		{
			NavMenuSectionHolder navMenuItemHolder = (NavMenuSectionHolder) convertView.getTag();
			labelView = navMenuItemHolder.labelView;
		}

		labelView.setText(menuSection.getLabel());

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return this.getItem(position).getType();
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).isEnabled();
	}

	private static class NavMenuItemHolder {
		private TextView labelView;
		private ImageView iconView;
	}

	private class NavMenuSectionHolder {
		private TextView labelView;
	}

}