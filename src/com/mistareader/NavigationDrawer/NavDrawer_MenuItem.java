package com.mistareader.NavigationDrawer;

import android.content.Context;

import com.mistareader.R;

public class NavDrawer_MenuItem {

    public static final int ITEM_TYPE    = 1;
    public static final int SECTION_TYPE = 0;

    public String          forum;
    public String          id;
    public String          label;
    public int             newSubs;
    public int             icon;
    public boolean         isExpandable;
    public boolean         isExpanded;
    public boolean         isSubmenu;
    public boolean         isSection;
    public boolean         isForum;

    public static NavDrawer_MenuItem create(Context context, String id, String label) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;
        item.icon = R.drawable.ic_point;

        return item;
    }

    public static NavDrawer_MenuItem createMenuItem(Context context, String id, String label) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;
        item.icon = R.drawable.ic_action_view_as_grid;

        return item;
    }

    
    public static NavDrawer_MenuItem createMenuItem(Context context, String id, String label, int icon) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;
        item.isForum = true;
        item.isSection = false;
        item.icon = icon;

        return item;
    }
    
    public static NavDrawer_MenuItem createMenuItem(Context context, String id, String label, int icon, int newSubs) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;
        item.isForum = true;
        item.isSection = false;
        item.icon = icon;
        item.newSubs = newSubs;

        return item;
    }

    public static NavDrawer_MenuItem createExpandable(Context context, String id, String label, String inForum) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;

        item.forum = inForum;
        item.isSection = false;
        item.icon = R.drawable.ic_action_expand;

        return item;
    }

    public static NavDrawer_MenuItem createSubmenu(Context context, String id, String label, String inForum) {

        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.id = id;
        item.label = label;
        item.icon = R.drawable.ic_point;
        item.isSubmenu = true;
        item.forum = inForum;
        item.isSection = false;

        return item;
    }

    public void setIsExpanded(boolean inisExpanded) {
        this.isExpanded = inisExpanded;
    }

    public static NavDrawer_MenuItem createSectionItem(String label) {
        
        NavDrawer_MenuItem item = new NavDrawer_MenuItem();
        item.label = label;
        item.isSection = true;

        return item;
    }

}