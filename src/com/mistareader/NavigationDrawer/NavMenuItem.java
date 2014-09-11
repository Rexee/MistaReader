package com.mistareader.NavigationDrawer;

import android.content.Context;
import com.mistareader.R;

public class NavMenuItem implements NavDrawerMenuItem {

	public static final int ITEM_TYPE = 1;

	private String forum;
	private String id;
	private String label;
	private int icon;
	private boolean isExpandable;
	private boolean isExpanded;
	private boolean isSubmenu;
	private boolean isForum;
	
	public static NavMenuItem create(Context context, String id, String label) {

		NavMenuItem item = new NavMenuItem();
		item.id = id;
		item.label = label;
		item.isExpanded = false;
		item.isExpandable = false;
		item.setSubmenu(false);
		item.setIsForum(false);
		item.icon = R.drawable.ic_point;

		return item;
	}

    public static NavMenuItem createMenuItem(Context context, String id, String label) {

        NavMenuItem item = new NavMenuItem();
        item.id = id;
        item.label = label;
        item.isExpanded = false;
        item.isExpandable = false;
        item.setSubmenu(false);
        item.setIsForum(true);
        item.icon = R.drawable.ic_action_view_as_grid;

        return item;
    }

	public static NavMenuItem createMenuItem(Context context, String id, String label, int icon) {

		NavMenuItem item = new NavMenuItem();
		item.id = id;
		item.label = label;
		item.isExpanded = false;
		item.isExpandable = false;
		item.setSubmenu(false);
		item.setIsForum(true);
		item.icon = icon;

		return item;
	}
	
	public static NavMenuItem createExpandable(Context context, String id, String label, String inForum) {

		NavMenuItem item = new NavMenuItem();
		item.id = id;
		item.label = label;
		item.isExpanded = false;
		item.isExpandable = true;
		item.setSubmenu(false);
		item.setIsForum(false);
		item.setForum(inForum);
		item.icon = R.drawable.ic_action_expand;

		return item;
	}
	
	public static NavMenuItem createSubmenu(Context context, String id, String label, String inForum) {

		NavMenuItem item = new NavMenuItem();
		item.id = id;
		item.label = label;
		item.icon = R.drawable.ic_point;
		item.setSubmenu(true);
		item.setForum(inForum);

		return item;
	}

	public void setIsExpanded(boolean inisExpanded) {
		this.isExpanded = inisExpanded;
	}

	@Override
	public int getType() {
		return ITEM_TYPE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean isExpandable() {
		return isExpandable;

	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public boolean isSubmenu() {
		return isSubmenu;
	}

	public void setSubmenu(boolean isSubmenu) {
		this.isSubmenu = isSubmenu;
	}

	public boolean isForum() {
		return isForum;
	}

	public void setIsForum(boolean isForum) {
		this.isForum = isForum;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}

}