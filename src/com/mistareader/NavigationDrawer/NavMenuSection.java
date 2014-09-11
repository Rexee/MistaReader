package com.mistareader.NavigationDrawer;

public class NavMenuSection implements NavDrawerMenuItem {

    public static final int SECTION_TYPE = 0;
    private String id;
    private String label;

    public static NavMenuSection create(String label ) {
        NavMenuSection section = new NavMenuSection();
        section.setLabel(label);
        return section;
    }
    
    @Override
    public int getType() {
        return SECTION_TYPE;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	@Override
	public boolean isExpandable() {
		return false;
	}

	@Override
	public int getIcon() {
		return 0;
	}

	@Override
	public boolean isExpanded() {
		return false;
	}

	@Override
	public void setIsExpanded(boolean inisExpanded) {
		
	}

	@Override
	public void setIcon(int icon) {
	}

	@Override
	public boolean isSubmenu() {
		return false;
	}

	@Override
	public boolean isForum() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getForum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setForum(String forum) {
		// TODO Auto-generated method stub
		
	}


}