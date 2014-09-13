package com.mistareader;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.S;

public class Messages_Fragment extends Fragment implements Forum.iOnPOSTRequestExecuted {

    OnContextMenuListener mOnContextMenuCallback;

    public interface OnContextMenuListener {

        public void onContextMenySelected(Message selectedMessage, long currentTopicId);

    }

    ListView                lvMain;
    View                    rootView;
    public Messages_Adapter messages_sAdapter;
    private View            message_Header;
    public Topic            currentTopic;
    private long            currentTopicId;
    private String          mAccount;
    private final int       prefetchMessagesFactor = 10;

    public long getCurrentTopicId() {
        return currentTopicId;
    }

    public boolean  modeMovePositionToLastMessage;
    public boolean  modeMovePositionToFirstMessage;
    public int      movePositionToMessage;

    private String  URL;

    private boolean messages_isLoading = false;

    ImageView       imgFastScrollDown;
    ImageView       imgFastScrollUp;

    boolean         up                 = false;
    boolean         allowArrowChange   = true;
    boolean         allowArrowShow;
    boolean         focusLast;

    int             answ;

    @Override
    public void onAttach(Activity activity) {

        try {
            mOnContextMenuCallback = (OnContextMenuListener) activity;
        }
        catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement OnContextMenuListener");
        }

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        imgFastScrollDown = (ImageView) rootView.findViewById(R.id.imgFastScrollDown);
        imgFastScrollDown.setVisibility(View.INVISIBLE);
        imgFastScrollUp = (ImageView) rootView.findViewById(R.id.imgFastScrollUp);
        imgFastScrollUp.setVisibility(View.INVISIBLE);

        imgFastScrollDown.setOnClickListener(onArrowClick);
        imgFastScrollUp.setOnClickListener(onArrowClick);

//        if (currentTopic.is_voting == 1) {
            getTopicInfo();
//        }

        if (focusLast) {
            modeMovePositionToLastMessage = true;
            focusLastMessage(true);
        }
        else {
            if (currentTopic.messages == null) {
                loadMessagesFrom(0);
            }
            else {
                drawMessages();
            }
        }

        return rootView;

    }

    private void getTopicInfo() {
        URL = API.getTopicInfo(currentTopicId);
        new RequestAsyncTopicInfo(null).execute(URL);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == R.id.menu_ReplyToThis) {
            mOnContextMenuCallback.onContextMenySelected(messages_sAdapter.getItem(info.position - 1), currentTopicId);
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }

    View.OnClickListener   onArrowClick            = new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           if (up) {
                                                               imgFastScrollUp.setVisibility(View.INVISIBLE);
                                                               focusFirstMessage();
                                                           }
                                                           else {
                                                               imgFastScrollDown.setVisibility(View.INVISIBLE);
                                                               focusLastMessage(false);
                                                           }
                                                       }
                                                   };

    private final Handler  fadeOutHandler          = new Handler();
    private final Handler  allowArrowChangeHandler = new Handler();

    private final Runnable allowArrowChangeTimer   = new Runnable() {
                                                       public void run() {
                                                           allowArrowChange = true;
                                                       }
                                                   };

    private final Runnable fadeOutAnimation        = new Runnable() {
                                                       @Override
                                                       public void run() {

                                                           if (!up) {

                                                               imgFastScrollDown.animate().alpha(0f).setDuration(700)
                                                                       .setListener(new AnimatorListenerAdapter() {
                                                                           @Override
                                                                           public void onAnimationEnd(Animator animation) {
                                                                               imgFastScrollDown.setVisibility(View.INVISIBLE);
                                                                           }
                                                                       });
                                                           }
                                                           else

                                                           {
                                                               imgFastScrollUp.animate().alpha(0f).setDuration(700).setListener(new AnimatorListenerAdapter() {
                                                                   @Override
                                                                   public void onAnimationEnd(Animator animation) {
                                                                       imgFastScrollUp.setVisibility(View.INVISIBLE);
                                                                   }
                                                               });

                                                           }
                                                       }
                                                   };

    private void onUp(int firstVisibleItem, int visibleItemCount) {

        onScrollUpLoadMessages(firstVisibleItem, visibleItemCount);

        if (!allowArrowChange || !allowArrowShow)
            return;

        allowArrowChange = false;
        allowArrowChangeHandler.postDelayed(allowArrowChangeTimer, 500);

        imgFastScrollDown.setVisibility(View.INVISIBLE);

        imgFastScrollUp.animate().cancel();
        imgFastScrollUp.setAlpha(1f);
        imgFastScrollUp.setVisibility(View.VISIBLE);

        fadeOutHandler.postDelayed(fadeOutAnimation, 1000);

        up = true;
    }

    private void onScrollUpLoadMessages(int firstVisibleItem, int visibleItemCount) {
        if (messages_isLoading) {
            return;
        }
        messages_isLoading = true;

        int prevMessageN = firstVisibleItem - prefetchMessagesFactor;

        if (prevMessageN < 0) {
            messages_isLoading = false;
            return;
        }

        Message curMessage = currentTopic.messages.get(prevMessageN);

        if (curMessage.isLoaded) {
            messages_isLoading = false;
            return;
        }

        loadMessagesBefore(prevMessageN);

    }

    private void onScrollDownLoadMessages(int firstVisibleItem, int visibleItemCount) {
        if (messages_isLoading) {
            return;
        }
        messages_isLoading = true;

        int nextMessageN = firstVisibleItem + visibleItemCount + prefetchMessagesFactor;

        if (nextMessageN > answ) {
            messages_isLoading = false;
            return;
        }

        Message curMessage = currentTopic.messages.get(nextMessageN);

        if (curMessage.isLoaded) {
            messages_isLoading = false;
            return;
        }

        loadMessagesFrom(nextMessageN);

    }

    private void loadMessagesFrom(int nextMessageN) {
        int messages_from;
        int messages_to;

        messages_from = nextMessageN;
        messages_to = nextMessageN + 20;
        URL = API.getMessages(currentTopicId, messages_from, messages_to);

        new RequestAsyncMessages(messages_from, messages_to).execute(URL);
    }

    private void loadMessagesBefore(int firstMessageN) {
        int messages_from;
        int messages_to;

        messages_from = firstMessageN - 20;
        messages_to = firstMessageN;
        URL = API.getMessages(currentTopicId, messages_from, messages_to);

        new RequestAsyncMessages(messages_from, messages_to).execute(URL);
    }

    private void onDown(int firstVisibleItem, int visibleItemCount) {

        onScrollDownLoadMessages(firstVisibleItem, visibleItemCount);

        if (!allowArrowChange || !allowArrowShow)
            return;

        allowArrowChange = false;
        allowArrowChangeHandler.postDelayed(allowArrowChangeTimer, 500);

        imgFastScrollUp.setVisibility(View.INVISIBLE);

        imgFastScrollDown.animate().cancel();
        imgFastScrollDown.setAlpha(1f);
        imgFastScrollDown.setVisibility(View.VISIBLE);

        fadeOutHandler.postDelayed(fadeOutAnimation, 1000);

        up = false;
    }

    private class messagesScrollListener implements OnScrollListener {

        int lastFirstVisibleItem;
        int lastTop;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemsInList) {

            View v = view.getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();

            if (firstVisibleItem == lastFirstVisibleItem) {
                if (top > lastTop) {
                    onUp(firstVisibleItem, visibleItemCount);
                }
                else if (top < lastTop) {
                    onDown(firstVisibleItem, visibleItemCount);
                }
            }
            else {
                if (firstVisibleItem > lastFirstVisibleItem) {
                    onDown(firstVisibleItem, visibleItemCount);
                }
                else {
                    onUp(firstVisibleItem, visibleItemCount);
                }
            }
            lastFirstVisibleItem = firstVisibleItem;
            lastTop = top;

        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case 1:
                case 2:
                    allowArrowShow = true;
                    break;
                default:
                    allowArrowShow = false;
                    break;
            }
        }

    }

    private void drawHeader() {
        if (message_Header == null) {
            return;
        }

        ((TextView) message_Header.findViewById(R.id.mess_headerText)).setText(currentTopic.text);

        if (currentTopic.votes != null) {

            RelativeLayout RL = (RelativeLayout) message_Header.findViewById(R.id.RL1);
            RL.setVisibility(View.VISIBLE);

            TextView twVote1 = (TextView) message_Header.findViewById(R.id.mess_headerVote1);
            TextView twVote2 = (TextView) message_Header.findViewById(R.id.mess_headerVote2);
            TextView twVote3 = (TextView) message_Header.findViewById(R.id.mess_headerVote3);
            TextView twVote4 = (TextView) message_Header.findViewById(R.id.mess_headerVote4);
            TextView twVote5 = (TextView) message_Header.findViewById(R.id.mess_headerVote5);

            Topic.Votes vote = currentTopic.votes.get(0);

            if (!vote.voteName.isEmpty()) {
                twVote1.setText("1. " + vote.voteName + ": " + vote.voteCount);
                twVote1.setVisibility(View.VISIBLE);
            }

            vote = currentTopic.votes.get(1);
            if (!vote.voteName.isEmpty()) {
                twVote2.setText("2. " + vote.voteName + ": " + vote.voteCount);
                twVote2.setVisibility(View.VISIBLE);
            }

            vote = currentTopic.votes.get(2);
            if (!vote.voteName.isEmpty()) {
                twVote3.setText("3. " + vote.voteName + ": " + vote.voteCount);
                twVote3.setVisibility(View.VISIBLE);
            }

            vote = currentTopic.votes.get(3);
            if (!vote.voteName.isEmpty()) {
                twVote4.setText("4. " + vote.voteName + ": " + vote.voteCount);
                twVote4.setVisibility(View.VISIBLE);
            }

            vote = currentTopic.votes.get(4);
            if (!vote.voteName.isEmpty()) {
                twVote5.setText("5. " + vote.voteName + ": " + vote.voteCount);
                twVote5.setVisibility(View.VISIBLE);
            }

        }

    }

    private void drawMessages() {

        if (messages_sAdapter == null) {

            try {
                lvMain = (ListView) rootView.findViewById(R.id.lvMain);
                lvMain.setDivider(null);

                Activity activity = getActivity();

                messages_sAdapter = new Messages_Adapter(activity, currentTopic, mAccount, R.layout.message_row);

                message_Header = activity.getLayoutInflater().inflate(R.layout.message_header, null);
                drawHeader();

                lvMain.addHeaderView(message_Header);

                lvMain.setAdapter(messages_sAdapter);
                lvMain.setOnScrollListener(new messagesScrollListener());

                registerForContextMenu(lvMain);

            }
            catch (Exception e) {
                S.L("drawMessages: " + Log.getStackTraceString(e));
            }

        }
        else {
            messages_sAdapter.notifyDataSetChanged();
        }

        if (modeMovePositionToFirstMessage) {
            lvMain.setSelection(0);
            imgFastScrollUp.setVisibility(View.INVISIBLE);
            modeMovePositionToFirstMessage = false;

        }
        if (modeMovePositionToLastMessage) {
            lvMain.setSelection(currentTopic.messages.size());
            imgFastScrollDown.setVisibility(View.INVISIBLE);
            modeMovePositionToLastMessage = false;
        }

        if (movePositionToMessage > 0) {
            lvMain.setSelection(movePositionToMessage);
            movePositionToMessage = 0;

        }

    }

    public class RequestAsyncMessages extends AsyncTask<String, Integer, String> {
        private int mMessages_from;
        private int mMessages_to;

        public RequestAsyncMessages(int messages_from, int messages_to) {
            mMessages_from = Math.max(messages_from, 0);
            mMessages_to = Math.min(messages_to, answ);

            S.L(mMessages_from + "-" + messages_to);
        }

        @Override
        protected void onPreExecute() {
            messages_isLoading = true;
        }

        protected String doInBackground(String... urls) {
            return WebIteraction.getServerResponse(urls[0]);
        }

        protected void onPostExecute(String result) {
            
            currentTopic.addNewMessages(result, mMessages_from, mMessages_to);
            drawMessages();
            messages_isLoading = false;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        modeMovePositionToLastMessage = false;
        modeMovePositionToFirstMessage = false;

        Bundle args = getArguments();
        if (args != null) {

            currentTopicId = args.getLong("topicId");

            currentTopic = Forum.getInstance().getTopicByid(currentTopicId);
            answ = currentTopic.answ;
            mAccount = args.getString("account");
            focusLast = args.getBoolean("focusLast");

        }

        super.onCreate(savedInstanceState);
    }

    public class RequestAsyncTopicInfo extends AsyncTask<String, Integer, String> {

        iOnLoadTopicInfoFinished mCallBack;

        public RequestAsyncTopicInfo(iOnLoadTopicInfoFinished callBackOnLoadTopicInfoFinished) {
            mCallBack = callBackOnLoadTopicInfoFinished;
        }

        protected String doInBackground(String... urls) {
            return WebIteraction.getServerResponse(urls[0]);
        }

        protected void onPostExecute(String result) {
            updateTopicInfo(JSONProcessor.ParseTopicInfo(result));
            if (mCallBack != null) {
                mCallBack.onLoadTopicInfoFinished();
            }
        }

    }

    private void updateTopicInfo(Topic refreshedTopic) {

        if (refreshedTopic.id == currentTopicId) {
            currentTopic.answ = refreshedTopic.answ;
            currentTopic.closed = refreshedTopic.closed;
            currentTopic.deleted = refreshedTopic.deleted;
            currentTopic.down = refreshedTopic.down;
            currentTopic.text = refreshedTopic.text;
            currentTopic.is_voting = refreshedTopic.is_voting;

            if (refreshedTopic.votes != null) {
                if (currentTopic.votes == null)
                    currentTopic.votes = new ArrayList<Topic.Votes>(5);
                else
                    currentTopic.votes.clear();

                currentTopic.votes.addAll(refreshedTopic.votes);
            }

            if (answ != refreshedTopic.answ) {
                if (refreshedTopic.answ > answ && currentTopic.messages !=null) {
                    for (int i = 0; i < (refreshedTopic.answ - answ); i++) {
                        Message newMessage = new Message();
                        currentTopic.messages.add(newMessage);
                    }
                }

                currentTopic.answ = refreshedTopic.answ;
                answ = refreshedTopic.answ;
            }
        }

    }

    public interface iOnLoadTopicInfoFinished {
        void onLoadTopicInfoFinished();
    }

    public void reLoad() {

        URL = API.getTopicInfo(currentTopicId);

        iOnLoadTopicInfoFinished callBackOnLoadTopicInfoFinished = new iOnLoadTopicInfoFinished() {

            @Override
            public void onLoadTopicInfoFinished() {

                if (currentTopic.is_voting == 1) {
                    drawHeader();
                }

                int pos = lvMain.getLastVisiblePosition();
                if (pos >= lvMain.getCount() - 1) {
                    movePositionToMessage = pos + 1;
                    loadMessagesFrom(pos);
                }
                else {
                    focusFirstMessage();
                }
            }
        };

        new RequestAsyncTopicInfo(callBackOnLoadTopicInfoFinished).execute(URL);

    }

    public void focusFirstMessage() {
        if (currentTopic.messages.get(0).isLoaded) {
            lvMain.setSelection(0);
            return;
        }

        modeMovePositionToFirstMessage = true;
        loadMessagesFrom(0);
    }

    public void focusLastMessage(boolean force) {

        if (!force && currentTopic.messages.get(answ).isLoaded) {
            lvMain.setSelection(answ);
            return;
        }

        modeMovePositionToLastMessage = true;
        loadMessagesFrom(currentTopic.answ - 20);
    }

    @Override
    public void onPOSTRequestExecuted(String result) {

    }

}
