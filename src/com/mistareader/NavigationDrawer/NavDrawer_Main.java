package com.mistareader.NavigationDrawer;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ListView;

import com.mistareader.API;
import com.mistareader.R;
import com.mistareader.ThemesManager;

public class NavDrawer_Main {

    final String                        MENU_1C            = "1C";
    final String                        MENU_IT            = "IT";
    final String                        MENU_LIFE          = "LIFE";

    public final static String          MENU_SETTINGS      = "SETTINGS";
    public final static String          MENU_ACCOUNT       = "ACCOUNT";
    public final static String          MENU_THEMES        = "THEMES";
    public final static String          MENU_SUBSCRIPTIONS = "SUBSCRIPTIONS";

    public final static String          MENU_ABOUT         = "ABOUT";
    public final static String          MENU_LOGOFF        = "LOGOFF";

    public DrawerLayout                 mDrawerLayout;

    public ListView                     mDrawerList;

    public NavDrawer_Adapter            mListAdapter;
    public ActionBarDrawerToggle        mDrawerToggle;
    public int                          mSelectedPosition;
    private String                      sAllSections;
    public ArrayList<NavDrawerMenuItem> mMenu;
    public ArrayList<NavDrawerMenuItem> mSubMenu;

    public String                       mCurrentAccout;
    public boolean                      mIsLoggedIn;

    public NavDrawer_Main(Activity mainActivity, String accountName, int selectedMenuPosition, boolean isLoggedIn) {

        mCurrentAccout = accountName;
        mIsLoggedIn = isLoggedIn;
        mSelectedPosition = selectedMenuPosition;
        sAllSections = mainActivity.getString(R.string.sAllSections);

        buildMenu(mainActivity);

        mDrawerLayout = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) mainActivity.findViewById(R.id.left_drawer);
        mListAdapter = new NavDrawer_Adapter(mainActivity, R.layout.navdrawer_item, mMenu);
        mDrawerList.setAdapter(mListAdapter);

        mDrawerList.setItemChecked(mSelectedPosition, true);

        mDrawerToggle = new ActionBarDrawerToggle(mainActivity, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                mDrawerList.setItemChecked(mSelectedPosition, true);
                return super.onOptionsItemSelected(item);
            }
        };

        // mDrawerList.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // mDrawerLayout.setScrimColor(Color.alpha(20));
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);

        mainActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getActionBar().setHomeButtonEnabled(true);

    }

    public void buildSubmenu(Activity mainActivity, String forum, int mResNames, int mResIDs) {
        String[] mSectionsMenuNames = mainActivity.getResources().getStringArray(mResNames);
        String[] mSectionsMenuIDs = mainActivity.getResources().getStringArray(mResIDs);

        for (int currSectNum = 0; currSectNum < mSectionsMenuNames.length; currSectNum++) {
            if (currSectNum < 3)
                mMenu.add(NavMenuItem.create(mainActivity, mSectionsMenuIDs[currSectNum], mSectionsMenuNames[currSectNum]));
            else
                mSubMenu.add(NavMenuItem.createSubmenu(mainActivity, mSectionsMenuIDs[currSectNum], mSectionsMenuNames[currSectNum], forum));
        }

        mMenu.add(NavMenuItem.createExpandable(mainActivity, "more", mainActivity.getString(R.string.sNavDrawerMore), forum));

    }

    public void buildMenu(Activity mainActivity) {

        if (mMenu == null) {
            mMenu = new ArrayList<NavDrawerMenuItem>();
            mSubMenu = new ArrayList<NavDrawerMenuItem>();
        }
        else {
            mMenu.clear();
            mSubMenu.clear();
        }

        mMenu.add(NavMenuSection.create(mainActivity.getString(R.string.sNavDrawerSect1)));

        mMenu.add(NavMenuItem.createMenuItem(mainActivity, "", mainActivity.getString(R.string.sNavDrawerAll), ThemesManager.iconForum));
        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_1C, MENU_1C, ThemesManager.iconForum));
        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_IT, MENU_IT, ThemesManager.iconForum));
        // buildSubmenu(mainActivity, "1c", R.array.sectionsName_IT, R.array.sections_IT);

        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_LIFE, MENU_LIFE, ThemesManager.iconForum));

        if (mIsLoggedIn)
            mMenu.add(NavMenuItem.createMenuItem(mainActivity, API.TOPICS_WITH_ME, mainActivity.getString(R.string.sMyTopics2), ThemesManager.iconForum));

//        if (mIsLoggedIn)
//            mMenu.add(NavMenuItem.createMenuItem(mainActivity, API.MYTOPICS, mainActivity.getString(R.string.sMyTopics), ThemesManager.iconForum));

        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_SUBSCRIPTIONS, mainActivity.getString(R.string.sSubscriptions), ThemesManager.iconForum));
        
        
        // buildSubmenu(mainActivity, "life", R.array.sectionsName_LIFE, R.array.sections_LIFE);

        mMenu.add(NavMenuSection.create(mainActivity.getString(R.string.sNavDrawerSect2)));

        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_SETTINGS, mainActivity.getString(R.string.sSettings), ThemesManager.iconSettings));

        // if (mIsLoggedIn)
        // mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_ACCOUNT, mCurrentAccout, ThemesManager.iconAccount));
        // else
        // mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_ACCOUNT, mainActivity.getString(R.string.sAccount), ThemesManager.iconAccount));
        //
        // mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_THEMES, mainActivity.getString(R.string.sTheme), ThemesManager.iconThemes));
        mMenu.add(NavMenuItem.createMenuItem(mainActivity, MENU_ABOUT, mainActivity.getString(R.string.sAbout), ThemesManager.iconAbout));

    }

    public void setPosition(int i) {
        mDrawerList.setSelection(i);
        // mDrawerList.smoothScrollToPosition(i);
    }

    public void setItemChecked(int i) {
        mDrawerList.setItemChecked(i, true);
    }

    public void expandMenu(int inPos, String inForum) {
        int k = 0;
        for (int i = 0; i < mSubMenu.size(); i++) {

            NavDrawerMenuItem sm = mSubMenu.get(i);
            if (sm.getForum().equals(inForum)) {
                mMenu.add(inPos + k, sm);
                k++;
            }

        }
    }

    public void collapseMenu(int inPos, String inForum) {
        int menuSize = mMenu.size();
        for (int i = 0; i < menuSize; i++) {
            if (mMenu.get(i).isSubmenu()) {

                if (mMenu.get(i).getForum().equals(inForum)) {

                    mMenu.remove(i);
                    i--;
                    menuSize--;

                }
            }

        }
    }

    public String getSelectedItemID() {
        if (mSelectedPosition <= 1)
            return "";

        return mMenu.get(mSelectedPosition).getId();

    }

    public String getSelectedMenuTitle() {
        if (mSelectedPosition <= 1)
            return sAllSections;

        return mMenu.get(mSelectedPosition).getLabel();

    }

    public NavDrawerMenuItem getMenuItem(int pos) {
        return mMenu.get(pos);
    }

}
