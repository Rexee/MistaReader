package com.mistareader.NavigationDrawer;

public interface NavDrawerMenuItem {
    public String getId();
    public String getLabel();
    
    public void setLabel(String label);
    public void setId(String id);
    public void setIsExpanded(boolean inisExpanded);
    public void setIcon(int icon);
    public boolean isSubmenu();
    public boolean isForum();
    
	public String getForum();
	public void setForum(String forum);
   
    public int getType();
    public boolean isEnabled();
    public boolean isExpandable();
    public boolean isExpanded();    
	public int getIcon();
}