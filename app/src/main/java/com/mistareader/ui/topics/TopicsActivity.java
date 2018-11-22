package com.mistareader.ui.topics;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mistareader.Forum;
import com.mistareader.Forum.iOnPOSTRequestExecuted;
import com.mistareader.R;
import com.mistareader.api.WebIteraction;
import com.mistareader.model.Section;
import com.mistareader.model.Topic;
import com.mistareader.ui.BaseNetworkActivity;
import com.mistareader.ui.messages.MessagesActivity;
import com.mistareader.ui.settings.SettingsActivity;
import com.mistareader.ui.topics.TopicsAdapter.TopicClicks;
import com.mistareader.ui.user.UserActivity;
import com.mistareader.util.ActivityCode;
import com.mistareader.util.Settings;
import com.mistareader.util.Subscriptions;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.views.LoadingIcon;
import com.mistareader.util.views.PopupDialog;
import com.mistareader.util.views.Recycler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class TopicsActivity extends BaseNetworkActivity implements iOnPOSTRequestExecuted, OnNavigationItemSelectedListener, TopicClicks, OnItemSelectedListener {
    @BindView(R.id.drawer_layout)   DrawerLayout       drawer;
    @BindView(R.id.nav_view)        NavigationView     navigationView;
    @BindView(R.id.list)            RecyclerView       recyclerView;
    @BindView(R.id.swipe)           SwipeRefreshLayout swipeView;
    @BindView(R.id.toolbar_spinner) Spinner            toolbarSpinner;

    private int                                       selectedNavBarItem        = R.id.nav_all;
    private int                                       selectedSectionPosition   = 0;
    private String                                    selectedForumName         = "";
    private String                                    selectedSectionName       = "";
    private boolean                                   isOpenedFromNotifications = false;
    private Forum                                     forum;
    private LoadingIcon                               loadingIcon;
    private NavDrawer                                 mNavDrawer;
    private BroadcastReceiver                         subscriptionsBroadcastReceiver;
    private Recycler<Topic, TopicsAdapter.ViewHolder> mList;
    private TopicsAdapter                             mAdapter;
    private ActionBar                                 actionBar;
    private ArrayAdapter<CharSequence>                toolbarSpinnerAdapter;
    private boolean                                   spinnerForceSelected;
    private LinkedHashMap<String, String>             dropdownItems             = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        mNavDrawer = new NavDrawer(this, this, drawer, toolbar, navigationView);
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);

        initCore(sPref);
        initRecyclerView();

        init_RegisterBroadcastReceiver();
        init_ProcessNotifications();

        if (!isOpenedFromNotifications) {
            init_LoadSavedInstances(savedInstanceState, sPref);
        }

        loadTopics(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavDrawer.onPostCreate();
    }

    private void initCore(SharedPreferences sPref) {
        actionBar = getSupportActionBar();
        loadingIcon = new LoadingIcon();
        toolbarSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        toolbarSpinnerAdapter.setDropDownViewResource(R.layout.list_item_tollbar_spinner_dropdown);
        toolbarSpinner.setAdapter(toolbarSpinnerAdapter);
        toolbarSpinner.setOnItemSelectedListener(this);

        if (forum == null) {
            forum = Forum.getInstance();
            forum.initialize(this, sPref);
            netProvider.getSectionsList(result -> forum.setSections(result));
        }
        mNavDrawer.setLoggedIn(forum.isLoggedIn);
    }

    private void initRecyclerView() {
        mAdapter = new TopicsAdapter(this, this, forum.accountName, selectedSectionName, false);
        mAdapter.setHasStableIds(true);
        mList = new Recycler<>(recyclerView, mAdapter);
        mList.disableChangesAnimations();
        mList.addOnScrollListener((page, totalItemsCount) -> loadTopics(totalItemsCount));
        swipeView.setColorSchemeColors(ThemesManager.getColorByAttr(this, R.attr.colorAccentSecondary));
        swipeView.setOnRefreshListener(this::reLoad);
    }

    private void loadTopics(int totalItemsCount) {
        if (!mList.loadData(totalItemsCount)) {
            return;
        }

        long beforeUTime = mAdapter.getLastTopicTime(totalItemsCount);
        showProgress();

        //TODO: test all these cases
        switch (selectedSectionName) {
            case NavDrawer.MENU_TOPICS_WITH_ME:
                netProvider.getTopicsWithMe(forum.accountUserID, beforeUTime, result -> {
                    drawTopicsList(beforeUTime, result);
                    hideProgress();
                });
                break;
            case NavDrawer.MENU_MY_TOPICS:
                netProvider.getMyTopics(beforeUTime, result -> {
                    drawTopicsList(beforeUTime, result);
                    hideProgress();
                });
                break;
            default:
                if (selectedNavBarItem == R.id.nav_subscriptions) {
                    mAdapter.setList(forum.mainDB.getSubscriptions());
                } else {
                    netProvider.getTopics(selectedForumName, dropdownItems.get(selectedSectionName), beforeUTime, result -> {
                        drawTopicsList(beforeUTime, result);
                        hideProgress();
                    });
                }
        }
    }

    private void drawTopicsList(long beforeUTime, final List<Topic> result) {
        mList.onLoadedList(result, beforeUTime);
        forum.onLoadedList(result, beforeUTime);
    }

    private void init_ProcessNotifications() {
        Intent inpIntent = getIntent();
        if (inpIntent.hasExtra(Subscriptions.NOTIFICATIONS_EXTRA_ID)) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Subscriptions.NOTIFICATIONS_UNIQUE_ID);

            Bundle extras = inpIntent.getExtras();
            if (extras != null) {

                boolean isMultipleSubs = extras.getBoolean(Subscriptions.NOTIFICATIONS_EXTRA_IS_MULTIPLE, false);
                long curTopicId = extras.getLong(Subscriptions.NOTIFICATIONS_EXTRA_TOPIC_ID);
                int curTopicAnsw = extras.getInt(Subscriptions.NOTIFICATIONS_EXTRA_TOPIC_ANSW);
                inpIntent.removeExtra(Subscriptions.NOTIFICATIONS_EXTRA_ID);

                if (isMultipleSubs) {
                    selectedForumName = NavDrawer.MENU_SUBSCRIPTIONS;
                } else {
                    isOpenedFromNotifications = true;
                    Topic currentTopic = Forum.getInstance().getTopicById(curTopicId);
                    if (currentTopic == null) {
                        currentTopic = new Topic();
                        currentTopic.id = curTopicId;
                        currentTopic.answ = curTopicAnsw;
                        forum.topics.add(currentTopic);
                    } else {
                        currentTopic.answ = curTopicAnsw;
                    }

                    openTopic(currentTopic, false, false);
                }
            }
        }
    }

    private void init_LoadSavedInstances(Bundle savedInstanceState, SharedPreferences sPref) {
        if (savedInstanceState != null) {
            selectedForumName = savedInstanceState.getString("forum", "");
            selectedSectionName = savedInstanceState.getString("section", "");
            selectedNavBarItem = savedInstanceState.getInt("selectedForumPos", 1);
            selectedSectionPosition = savedInstanceState.getInt("selectedSectionPos", 0);
        } else {
            forum.showWhatsNew(sPref, this);
            forum.delayedLogin(this, result -> {
                invalidateOptionsMenu();
                mNavDrawer.setLoggedIn(result != null);
            });
        }
    }

    @Override
    protected void onPause() {
        isOpenedFromNotifications = false;
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("forum", selectedForumName);
        outState.putString("section", selectedSectionName);
        outState.putInt("selectedForumPos", selectedNavBarItem);
        outState.putInt("selectedSectionPos", selectedSectionPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ThemesManager.tintMenu(R.menu.topics, this, getMenuInflater(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        loadingIcon.init(menu.findItem(R.id.menu_reload), false);

//        MenuItem menuAddNew = menu.findItem(R.id.menu_add);
//        MenuItem menuMarkAll = menu.findItem(R.id.menu_markAll);
//
//        boolean isSubscriptions = selectedForumName.equals(NavDrawer.MENU_SUBSCRIPTIONS);
//        if (!forum.isLoggedIn || isSubscriptions) {
//            menuAddNew.setVisible(false);
//        } else {
//            menuAddNew.setVisible(true);
//        }
//        if (isSubscriptions) {
//            menuMarkAll.setVisible(true);
//        } else {
//            menuMarkAll.setVisible(false);
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            //            mND.mDrawerToggle.onOptionsItemSelected(item);
            return true;
        } else if (id == R.id.menu_reload) {
            forum.isInternetConnection = WebIteraction.isInternetAvailable(TopicsActivity.this);

            if (forum.isInternetConnection) {
                if (selectedForumName.equals(NavDrawer.MENU_SUBSCRIPTIONS)) {
                    Intent serviceIntent = new Intent(TopicsActivity.this, Subscriptions.class);
                    serviceIntent.putExtra(Subscriptions.NOTIFICATIONS_EXTRA_RELOAD_MODE, true);

                    startService(serviceIntent);
                } else {
                    reLoad();
                }
            }

            return true;
        } else if (id == R.id.menu_add) {
            forum.addNewTopic(TopicsActivity.this);
            return true;
        } else if (id == R.id.menu_markAll) {
            forum.mainDB.markAllSubscriptionsAsReaded();

            updateBadgeCounter();

            forum.isInternetConnection = WebIteraction.isInternetAvailable(TopicsActivity.this);

            reLoad();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void reLoad() {
        if (!forum.isInternetConnection) {
            return;
        }

        mAdapter.reachedMaxTopics = false;
        forum.deleteTopics();
        loadTopics(0);
    }

    private void showProgress() {
        loadingIcon.showProgress();
    }

    private void hideProgress() {
        swipeView.post(() -> swipeView.setRefreshing(false));
        loadingIcon.hideProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Forum.ACTIVITY_RESULT_NEWTOPIC:
                if (data == null || resultCode != RESULT_OK) {
                    return;
                }

                Bundle args = data.getExtras();
                if (args != null) {
                    String commandName = args.getString("commandName", null);
                    if (commandName == null) {
                        return;
                    }

                    if (commandName.equals(Forum.COMMAND_CREATE_NEW_TOPIC)) {
                        forum.createNewTopic(TopicsActivity.this, args);
                    }

                    break;
                }

            case Forum.ACTIVITY_RESULT_SETTINGS:
                recreate();
                //                if (data == null)
                //                    return;
                //
                //                boolean isLoginChanged = data.getBooleanExtra("isLoginChanged", false);
                //                boolean isThemeChanged = data.getBooleanExtra("isThemeChanged", false);
                //                boolean isSubscriptionChanged = data.getBooleanExtra("isSubscriptionChanged", false);
                //
                //                if (isSubscriptionChanged) {
                //                    Subscriptions.updateNotifications(TopicsActivity.this);
                //                }
                //
                //                if (isLoginChanged) {
                //                    onLoggedIn(forum.isLoggedIn());
                //                }
                //
                //                if (isThemeChanged) {
                //                    recreate();
                //                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavDrawer.onBackPressed()) {
            return;
        }

        //if (selectedForumName == NavDrawer_Main.MENU_SUBSCRIPTIONS) {
        //    selectedNavBarItem = 1;
        //    mND.mSelectedPosition = selectedNavBarItem;
        //    selectedForumName = mND.getSelectedItemID();
        //
        //    invalidateOptionsMenu();
        //    openSelectedForum();
        //    return;
        //}
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(TopicsActivity.this).unregisterReceiver(subscriptionsBroadcastReceiver);

        forum = Forum.getInstance();
        if (forum != null) {
            forum.onDestroy(this);
        }
    }

    private void openTopic(Topic topic, boolean focusLast, boolean forceFirst) {
        if (!forum.isInternetConnection) {
            return;
        }

        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(MessagesActivity.EXTRA_TOPIC_ID, topic.id);
        intent.putExtra(MessagesActivity.EXTRA_SECTION_NAME, topic.sect1);
        intent.putExtra(MessagesActivity.EXTRA_FORUM_NAME, topic.forum);
        intent.putExtra(MessagesActivity.EXTRA_FOCUS_LAST, focusLast);
        intent.putExtra(MessagesActivity.EXTRA_TOPIC_TITLE, topic.text);
        if (!forceFirst) {
            intent.putExtra(MessagesActivity.EXTRA_FOCUS_ON, forum.mainDB.getLastPositionForMessage(topic.id));
        }

        startActivity(intent);

        markAsRead(topic.id);
    }

    private void markAsRead(long topicId) {
        boolean mIsSubscriptionPage = selectedForumName.equals(NavDrawer.MENU_SUBSCRIPTIONS);

        if (mIsSubscriptionPage || forum.mainDB.isTopicInSubscriptions(topicId)) {
            forum.mainDB.markTopicAsReaded(topicId, 0);
            updateBadgeCounter();
            if (mIsSubscriptionPage) {
                reLoad();
            }
        }
    }

    private void updateBadgeCounter() {
        mNavDrawer.update();
    }

    private void init_RegisterBroadcastReceiver() {
        IntentFilter intFilt = new IntentFilter(Settings.SUBSCRIPTIONS_UPDATED_BROADCAST);
        subscriptionsBroadcastReceiver = new subscriptionsResponseReceiver();
        LocalBroadcastManager.getInstance(TopicsActivity.this).registerReceiver(subscriptionsBroadcastReceiver, intFilt);
    }

    private class subscriptionsResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBadgeCounter();

            if (selectedForumName.equals(NavDrawer.MENU_SUBSCRIPTIONS)) {
                reLoad();
            }
        }
    }

    @Override
    public void onPOSTRequestExecuted(String result) {
        reLoad();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mNavDrawer.onNavigationItemSelected();

        selectedNavBarItem = item.getItemId();
        selectedForumName = "";
        selectedSectionName = "";
        mAdapter.setShowSections(true);

        switch (selectedNavBarItem) {
            case R.id.nav_all:
                hideDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_1c:
                selectedForumName = NavDrawer.MENU_1C;
                showDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_it:
                selectedForumName = NavDrawer.MENU_IT;
                showDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_job:
                selectedForumName = NavDrawer.MENU_JOB;
                showDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_life:
                selectedForumName = NavDrawer.MENU_LIFE;
                showDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_my_topics:
                showDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_subscriptions:
                setTitle(R.string.sSubscriptions);
                hideDropDownSpinner();
                loadTopics(0);
                break;
            case R.id.nav_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), Forum.ACTIVITY_RESULT_SETTINGS);
                return false;
            case R.id.nav_about:
                forum.showAbout(this);
                return false;
        }

        return true;
    }

    private void showDropDownSpinner() {
        actionBar.setDisplayShowTitleEnabled(false);
        toolbarSpinner.setVisibility(View.VISIBLE);

        dropdownItems.clear();

        if (selectedNavBarItem == R.id.nav_my_topics) {
            dropdownItems.put(getString(R.string.sMyTopics2), "");
            dropdownItems.put(getString(R.string.sMyTopics), "");
            selectedSectionName = NavDrawer.MENU_TOPICS_WITH_ME;
        } else {
            dropdownItems.put(selectedForumName + " (" + getString(R.string.sNavDrawerAll) + ")", "");
            for (int i = 0; i < forum.sections.size(); i++) {
                Section sec = forum.sections.get(i);
                if (sec.forumName.equals(selectedForumName)) {
                    dropdownItems.put(sec.sectionFullName, sec.sectionShortName);
                }
            }
        }
        spinnerForceSelected = true;
        toolbarSpinnerAdapter.setNotifyOnChange(false);
        toolbarSpinnerAdapter.clear();
        toolbarSpinnerAdapter.setNotifyOnChange(true);
        toolbarSpinnerAdapter.addAll(dropdownItems.keySet());
        toolbarSpinner.setSelection(0);
    }

    private void hideDropDownSpinner() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
        toolbarSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spinnerForceSelected) {
            spinnerForceSelected = false;
            return;
        }
        selectedSectionPosition = position;

        if (selectedNavBarItem == R.id.nav_my_topics) {
            if (position == 0) {
                selectedSectionName = NavDrawer.MENU_TOPICS_WITH_ME;
            } else {
                selectedSectionName = NavDrawer.MENU_MY_TOPICS;
            }
            mAdapter.setShowSections(true);
        } else {
            if (position == 0) {
                mAdapter.setShowSections(true);
                selectedSectionName = "";
            } else {
                mAdapter.setShowSections(false);
                selectedSectionName = (String) toolbarSpinnerAdapter.getItem(position);
            }
        }

        loadTopics(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onTopicClick(Topic topic) {
        openTopic(topic, false, false);
    }

    @Override
    public void onUserClick(String username) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserActivity.EXTRA_USER_NAME, username);
        startActivityForResult(intent, ActivityCode.USER_ACTIVITY);
    }

    @Override
    public void onTopicLongClick(View v, Topic topic) {
        ArrayList<Integer> items = new ArrayList<>();
        if (forum.isLoggedIn) {
            if (forum.mainDB.isTopicInSubscriptions(topic.id)) {
                items.add(R.string.sRemoveFromSubscriptions);
            } else if (forum.mainDB.getTotalSubscriptionsCount() < Settings.SUBSCRIPTIONS_MAX_COUNT) {
                items.add(R.string.sAddToSubscription);
            }
        }
        items.add(R.string.sMenuGoToFirstMessage);
        items.add(R.string.sMenuGoToLastMessage);

        new PopupDialog(this, items, (dialog, which) -> {
            switch (which) {
                case R.string.sMenuGoToLastMessage:
                    openTopic(topic, true, false);
                    break;
                case R.string.sMenuGoToFirstMessage:
                    openTopic(topic, false, true);
                    break;
                case R.string.sAddToSubscription:
                    forum.mainDB.addTopicToSubscriptions(topic);
                    break;
                case R.string.sRemoveFromSubscriptions:
                    forum.mainDB.removeTopicFromSubscriptions(topic.id);
                    if (selectedNavBarItem == R.id.nav_subscriptions) {
                        reLoad();
                    }
                    updateBadgeCounter();
                    break;
            }
        });
    }
}
