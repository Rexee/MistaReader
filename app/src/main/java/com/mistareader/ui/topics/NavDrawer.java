package com.mistareader.ui.topics;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mistareader.R;
import com.mistareader.util.ThemesManager;

public class NavDrawer {
    public final static String   MENU_1C             = "1C";
    public final static String   MENU_IT             = "IT";
    public final static String   MENU_JOB            = "JOB";
    public final static String   MENU_LIFE           = "LIFE";
    public static final String   MENU_MY_TOPICS      = "MY_TOPICS";
    public static final String   MENU_TOPICS_WITH_ME = "TOPICS_WITH_ME";
    public final static String   MENU_SUBSCRIPTIONS  = "SUBSCRIPTIONS";
    private final       MenuItem menuMyTopics;
    private final       MenuItem menuSubscriptions;

    private DrawerLayout          mDrawer;
    private ActionBarDrawerToggle mToggle;
    public  boolean               mIsLoggedIn;

    public NavDrawer(Activity activity, OnNavigationItemSelectedListener listener, DrawerLayout drawer, Toolbar toolbar, NavigationView navigationView) {
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_checked},
                new ColorDrawable(ThemesManager.getColorByAttr(activity, R.attr.navDrawerSelectedColor)));
        navigationView.setItemBackground(res);

        mDrawer = drawer;
        mToggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView.setNavigationItemSelectedListener(listener);

        Menu menu = navigationView.getMenu();
        menuMyTopics = menu.findItem(R.id.nav_my_topics);
        menuSubscriptions = menu.findItem(R.id.nav_subscriptions);

        navigationView.setCheckedItem(R.id.nav_all);
    }

    private void showMenuItems() {
        menuMyTopics.setVisible(mIsLoggedIn);
//        menuSubscriptions.setVisible(mIsLoggedIn);
    }

    public boolean onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    public void onPostCreate() {
        mToggle.syncState();
    }

    public void update() {
        //        mND.mListAdapter.notifyDataSetInvalidated();
        //        mND.buildMenu(Topics_Activity.this);
        //        mND.mListAdapter.notifyDataSetChanged();
    }

    public void onNavigationItemSelected() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        mIsLoggedIn = isLoggedIn;
        showMenuItems();
    }
}
