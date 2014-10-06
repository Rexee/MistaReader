package com.mistareader;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mistareader.NavigationDrawer.NavDrawer_Main;
import com.mistareader.TextProcessors.S;

public class Topics_Fragment extends Fragment {

    OnTopicSelectedListener mOntopicSelectedCallback;
    OnUnsubscribeListener   mOnUnsubscribe;

    public interface OnTopicSelectedListener {
        public void onTopicSelected(Topic selectedTopic, boolean focusLast, boolean forceFirst);
    }

    public interface OnUnsubscribeListener {
        public void onUnsubscribe();
    }

    public String         sSection;
    public String         sForum;

    private Forum         forum;

    public Topics_Adapter topics_sAdapter;
    ListView              lvMain;
    View                  rootView;
    boolean               isInternetConnection;

    private String        URL;

    private boolean       topics_isLoading          = false;

    private boolean       mode_Subscription         = false;
    boolean               isOpenedFromNotifications = false;

    class TopicsOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, final long id) {

            try {
                mOntopicSelectedCallback.onTopicSelected((Topic) adapter.getItemAtPosition(position), false, false);

            }
            catch (Exception e) {

                S.L("TopicsOnItemClickListener: ", e);

            }

        }

    }

    private class topicScrollListener implements OnScrollListener {

        private int visibleThreshold = 10;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemsInList) {

            if (mode_Subscription || topics_isLoading || visibleItemCount == 0 || !isInternetConnection)
                return;

            if ((totalItemsInList - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                loadTopicsInfinite();

            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.lvMain) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.topics_context, menu);
            menu.setHeaderTitle(R.string.sPopupMenuHeader);

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            int position = info.position;

            Topic selTopic = topics_sAdapter.getItem(position);
            if (!forum.mainDB.isTopicInSubscriptions(selTopic.id)) {
                menu.removeItem(R.id.menu_removeFromSubscriptions);

                if (forum.mainDB.getTotalSubscriptionsCount() >= Settings.SUBSCRIPTIONS_MAX_COUNT) {
                    menu.removeItem(R.id.menu_addToSubscription);

                }

            }
            else {
                menu.removeItem(R.id.menu_addToSubscription);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == R.id.menu_GoToLastMessage) {
            mOntopicSelectedCallback.onTopicSelected(topics_sAdapter.getItem(info.position), true, false);
            return true;
        }
        else if (itemId == R.id.menu_GoToFirstMessage) {
            mOntopicSelectedCallback.onTopicSelected(topics_sAdapter.getItem(info.position), false, true);
            return true;
        }
        else if (itemId == R.id.menu_addToSubscription) {
            Topic selTopic = topics_sAdapter.getItem(info.position);
            forum.mainDB.addTopicToSubscriptions(selTopic);
            return true;
        }
        else if (itemId == R.id.menu_removeFromSubscriptions) {
            Topic selTopic = topics_sAdapter.getItem(info.position);
            forum.mainDB.removeTopicFromSubscriptions(selTopic.id);
            if (mode_Subscription) {
                reLoad();
            }
            try {
                mOnUnsubscribe.onUnsubscribe();
            }
            catch (Exception e) {
                S.L("TopicsOnItemClickListener: ", e);
            }

            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }

    private void drawTopicsList() {

        try {

            if (topics_sAdapter == null) {

                final Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                topics_sAdapter = new Topics_Adapter(activity, forum, sSection, mode_Subscription);

                lvMain = (ListView) rootView.findViewById(R.id.lvMain);
                lvMain.setAdapter(topics_sAdapter);
                lvMain.setOnScrollListener(new topicScrollListener());
                lvMain.setOnItemClickListener(new TopicsOnItemClickListener());

                registerForContextMenu(lvMain);

            }
            else {
                topics_sAdapter.notifyDataSetChanged();
            }

        }
        catch (Exception e) {

            S.L("DrawTopicsList: " + Log.getStackTraceString(e));

        }
    }

    public class RequestAsyncTopics extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            topics_isLoading = true;
        }

        protected String doInBackground(String... urls) {
            if (urls.length == 1) {
                return WebIteraction.getServerResponse(urls[0]);
            }
            else
                return WebIteraction.getServerResponseWithAuth(urls[0], urls[1], urls[2]);
        }

        protected void onPostExecute(String result) {

            forum.addNewTopics(result);
            drawTopicsList();

            topics_isLoading = false;
        }

    }

    private void loadTopicsInfinite() {

        if (topics_isLoading || forum.reachedMaxTopics) {
            return;
        }

        if (forum.topics == null || forum.topics.isEmpty()) {
            loadTopics(0);
        }

        loadTopics(forum.getLastTopicTime());

    }

    private void loadTopics(long beforeUTime) {

        if (topics_isLoading) {
            return;
        }

        switch (sForum) {
            case API.TOPICS_WITH_ME:

                URL = API.getTopicsWithMe(forum.accountUserID, beforeUTime);
                new RequestAsyncTopics().execute(URL);
                break;

            case API.MYTOPICS:

                URL = API.getMyTopics(beforeUTime);
                new RequestAsyncTopics().execute(URL, forum.sessionID, forum.accountUserID);
                break;

            case NavDrawer_Main.MENU_SUBSCRIPTIONS:

                fillTopicsWithSubscriptions();
                drawTopicsList();
                break;

            default:

                URL = API.getTopics(sForum, sSection, beforeUTime);
                new RequestAsyncTopics().execute(URL);
                break;
        }

    }

    private void fillTopicsWithSubscriptions() {

        ArrayList<Topic> subs = forum.mainDB.getSubscriptions();

        for (int i = 0; i < subs.size(); i++) {
            forum.topics.add(subs.get(i));
        }

    }

    public void loadNewTopics() {

        if (topics_isLoading) {
            return;
        }

        switch (sForum) {
            case API.TOPICS_WITH_ME:
                break;

            case API.MYTOPICS:
                break;
            default:

                URL = API.getLastTopics(sForum, sSection, forum.getFirstTopicTime());
                new RequestAsyncTopics().execute(URL);
                break;
        }

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {
            mOntopicSelectedCallback = (OnTopicSelectedListener) activity;
            mOnUnsubscribe = (OnUnsubscribeListener) activity;
        }
        catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement OnTopicSelectedListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // setRetainInstance(true);
        // setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            
            sForum = args.getString("sForum", "");

            if (sForum.equals(NavDrawer_Main.MENU_SUBSCRIPTIONS)) {
                mode_Subscription = true;
            }
            else {

                sSection = args.getString("sSection", "");

                if (sForum.equals(API.TOPICS_WITH_ME) && !sSection.isEmpty()) {
                    sForum = sSection;
                    sSection = "";
                }
            }

        }

        if (sSection == null) {
            sSection = "";
        }
        if (sForum == null) {
            sForum = "";
        }

    }

    // ----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        forum = Forum.getInstance();
        isInternetConnection = forum.isInternetConnection;

        if (isInternetConnection) {
            forum.deleteTopics();
            loadTopics(0);
        }

        return rootView;
    }

    public void reLoad() {

        isInternetConnection = forum.isInternetConnection;

        if (!isInternetConnection) {
            return;
        }

        if (topics_sAdapter != null) {
            topics_sAdapter.notifyDataSetInvalidated();
        }

        forum.deleteTopics();

        loadTopics(0);

    }

}
