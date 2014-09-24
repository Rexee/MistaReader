package com.mistareader;

import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mistareader.NavigationDrawer.DropDownNav;
import com.mistareader.NavigationDrawer.NavDrawerMenuItem;
import com.mistareader.NavigationDrawer.NavDrawer_Main;

public class Topics_Activity extends BaseActivity implements Topics_Fragment.OnTopicSelectedListener, OnNavigationListener, Forum.iOnPOSTRequestExecuted,
        Forum.iOnLoggedIn {

    NavDrawer_Main        mND;
    DropDownNav           ddN;
    Forum                 forum;
    Topics_Fragment       topics_Fragment;

    public static boolean isLoginChanged          = false;
    public static boolean isThemeChanged          = false;

    int                   selectedForumPosition   = 1;
    int                   selectedSectionPosition = 0;
    String                selectedForumName       = "";
    String                selectedSectionName     = "";
    boolean               isInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        ThemesManager.CurrentTheme = sPref.getInt(ThemesManager.SETTINGS_THEME, ThemesManager.THEME_DEFAULT);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        isInternetConnection = WebIteraction.isInternetAvailable(this);

        if (forum == null) {
            forum = Forum.getInstance();
            forum.loadSettings(sPref);
            forum.initialize(isInternetConnection, this);
        }

        if (savedInstanceState != null) {
            selectedForumName = savedInstanceState.getString("forum", "");
            selectedSectionName = savedInstanceState.getString("section", "");
            selectedForumPosition = savedInstanceState.getInt("selectedForumPos", 1);
            selectedSectionPosition = savedInstanceState.getInt("selectedSectionPos", 0);
        }
        else {
            forum.showWhatsNew(sPref, this);
            forum.delayedLogin(this);
            forum.delayedStartNotifications();
        }

        topics_Fragment = (Topics_Fragment) getFragmentManager().findFragmentByTag("TOPICS");
        if (topics_Fragment == null)
            createTopicsFragment(true);

        mND = new NavDrawer_Main(Topics_Activity.this, forum.accountName, selectedForumPosition, !forum.sessionID.isEmpty());
        mND.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (selectedForumName.isEmpty())
            setTitle(mND.getSelectedMenuTitle());
        else {
            ddN = new DropDownNav();
            ddN.reBuildSubmenu(this, selectedForumName, selectedSectionPosition);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        // После recreate() открывает Drawer. Не знаю почему.
        mND.mDrawerLayout.closeDrawer(mND.mDrawerList);
        mND.mDrawerToggle.syncState();

        if (isLoginChanged) {
            isLoginChanged = false;

            onLoggedIn(!forum.sessionID.isEmpty());
        }

        if (isThemeChanged) {
            isThemeChanged = false;

            recreate();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("forum", selectedForumName);
        outState.putString("section", selectedSectionName);
        outState.putInt("selectedForumPos", selectedForumPosition);
        outState.putInt("selectedSectionPos", selectedSectionPosition);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem mi = menu.findItem(R.id.menu_add);
        if (forum.sessionID == null || forum.sessionID.isEmpty()) {
            mi.setVisible(false);
        }
        else {
            mi.setVisible(true);
        }

        return true;
    }

    private void createTopicsFragment(boolean createNewFragment) {

        topics_Fragment = new Topics_Fragment();

        Bundle args = new Bundle();

        args.putString("sForum", selectedForumName);
        args.putString("sSection", selectedSectionName);

        topics_Fragment.setArguments(args);

        if (createNewFragment) {
            getFragmentManager().beginTransaction().add(R.id.content_frame, topics_Fragment, "TOPICS").commit();
        }
        else {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, topics_Fragment, "TOPICS").commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            mND.mDrawerToggle.onOptionsItemSelected(item);
            return true;
        }
        else if (id == R.id.menu_reload) {
            if (topics_Fragment != null) {
                isInternetConnection = WebIteraction.isInternetAvailable(Topics_Activity.this);
                forum.isInternetConnection = isInternetConnection;
                topics_Fragment.reLoad();
            }
            return true;
        }
        else if (id == R.id.menu_add) {
            forum.addNewTopic(Topics_Activity.this);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        Bundle args = data.getExtras();
        if (args != null) {

            switch (requestCode) {
                case Forum.ACTIVITY_RESULT_NEWTOPIC:

                    String commandName = args.getString("commandName", null);
                    if (commandName == null) {
                        return;
                    }

                    if (commandName.equals(Forum.COMMAND_CREATE_NEW_TOPIC)) {
                        forum.createNewTopic(Topics_Activity.this, args);
                    }

                    break;

                case Forum.ACTIVITY_RESULT_SETTINGS:
                    break;
                default:
                    break;
            }

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            mND.mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        forum = Forum.getInstance();
        if (forum != null) {
            SharedPreferences sPref = getPreferences(MODE_PRIVATE);
            forum.saveSettings(sPref);
            forum.mainDB.close();
        }

        super.onDestroy();

    }

    @Override
    public void onTopicSelected(Topic selectedTopic, boolean focusLast, boolean forceFirst) {

        if (!isInternetConnection) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, Messages_Activity.class);
        intent.putExtra("topicId", selectedTopic.id);
        intent.putExtra("account", forum.accountName);
        intent.putExtra("section", selectedTopic.sect1);
        intent.putExtra("forum", selectedTopic.forum);
        intent.putExtra("focusLast", focusLast);
        if (!forceFirst) {
            intent.putExtra("focusOn", forum.mainDB.getLastPositionForMessage(selectedTopic.id));
        }

        startActivity(intent);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mND.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mND.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mND.mDrawerLayout.closeDrawer(mND.mDrawerList);

            NavDrawerMenuItem selectedMenu = mND.getMenuItem(position);

            if (!selectedMenu.isExpandable()) {

                String menuId = selectedMenu.getId();

                switch (menuId) {
                    case NavDrawer_Main.MENU_SETTINGS:
                        startActivity(new Intent(Topics_Activity.this, Settings_Activity.class));
                        break;

                    case NavDrawer_Main.MENU_ACCOUNT:

                        forum.setupAccount(Topics_Activity.this);
                        break;

                    case NavDrawer_Main.MENU_THEMES:

                        forum.selectTheme(Topics_Activity.this);
                        break;

                    case NavDrawer_Main.MENU_LOGOFF:

                        forum.sessionID = "";
                        invalidateOptionsMenu();
                        mND.buildMenu(Topics_Activity.this);
                        mND.mListAdapter.notifyDataSetInvalidated();
                        mND.mListAdapter.notifyDataSetChanged();
                        break;

                    case NavDrawer_Main.MENU_ABOUT:

                        forum.showAbout(Topics_Activity.this);
                        break;

                    case NavDrawer_Main.MENU_SUBSCRIPTIONS:

//                        forum.showAbout(Topics_Activity.this);
                        break;

                    default:

                        selectedForumPosition = position;
                        mND.mSelectedPosition = selectedForumPosition;
                        selectedForumName = mND.getSelectedItemID();

                        openSelectedForum();
                        break;
                }

            }
            else {

                // mND.setItemChecked(mND.mSelectedPosition);

                if (selectedMenu.isExpanded()) {

                    selectedMenu.setIsExpanded(false);
                    selectedMenu.setIcon(R.drawable.ic_action_expand);
                    selectedMenu.setLabel(getString(R.string.sNavDrawerLess));

                    mND.collapseMenu(position, selectedMenu.getForum());
                    mND.mListAdapter.notifyDataSetChanged();
                }
                else {

                    selectedMenu.setIsExpanded(true);
                    selectedMenu.setIcon(R.drawable.ic_action_collapse);
                    selectedMenu.setLabel(getString(R.string.sNavDrawerLess));

                    mND.expandMenu(position, selectedMenu.getForum());
                    mND.mListAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    public void openSelectedForum() {

        selectedSectionPosition = 0;
        selectedSectionName = "";

        ddN = new DropDownNav();
        ddN.reBuildSubmenu(this, selectedForumName, selectedSectionPosition);

        createTopicsFragment(false);

    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {

        if (selectedSectionPosition == position) {
            return false;
        }

        selectedSectionPosition = position;

        if (selectedForumName.equals(API.TOPICS_WITH_ME)) {
            if (selectedSectionPosition == 1)
                selectedSectionName = API.MYTOPICS;
            else
                selectedSectionName = API.TOPICS_WITH_ME;
        }
        else {
            if (selectedSectionPosition != 0)
                selectedSectionName = ddN.shortSectionsNamesList.get(selectedSectionPosition);
            else
                selectedSectionName = "";
        }
        createTopicsFragment(false);

        return true;

    }

    @Override
    public void onPOSTRequestExecuted(String result) {

        topics_Fragment.reLoad();

    }

    @Override
    public void onLoggedIn(boolean isLoggedIn) {

        if (mND.mIsLoggedIn == isLoggedIn) {
            return;
        }

        invalidateOptionsMenu();
        mND.mCurrentAccout = forum.accountName;
        mND.mIsLoggedIn = isLoggedIn;
        mND.buildMenu(this);
        mND.mListAdapter.notifyDataSetInvalidated();
        mND.mListAdapter.notifyDataSetChanged();

    }

}
