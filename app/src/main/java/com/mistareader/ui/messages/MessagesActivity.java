package com.mistareader.ui.messages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistareader.Forum;
import com.mistareader.Forum.iOnPOSTRequestExecuted;
import com.mistareader.R;
import com.mistareader.model.Message;
import com.mistareader.model.Topic;
import com.mistareader.ui.BaseNetworkActivity;
import com.mistareader.ui.messages.MessagesAdapter.MessageClicks;
import com.mistareader.ui.user.UserActivity;
import com.mistareader.util.ActivityCode;
import com.mistareader.util.Callback;
import com.mistareader.util.Empty;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.views.LoadingIcon;
import com.mistareader.util.views.PopupDialog;
import com.mistareader.util.views.Recycler;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

//TODO check xml to support api 16
public class MessagesActivity extends BaseNetworkActivity implements iOnPOSTRequestExecuted, MessageClicks {
    private static final int ARROW_HID_DELAY_MILLIS  = 1500;
    private static final int PREFETCH_MESSAGES_COUNT = 10;
    private static final int LOAD_MESSAGES_COUNT     = 20;

    public static final String EXTRA_TOPIC_ID     = "EXTRA_TOPIC_ID";
    public static final String EXTRA_SECTION_NAME = "EXTRA_SECTION_NAME";
    public static final String EXTRA_FORUM_NAME   = "EXTRA_FORUM";
    public static final String EXTRA_FOCUS_LAST   = "EXTRA_FOCUS_LAST";
    public static final String EXTRA_FOCUS_ON     = "EXTRA_FOCUS_ON";
    public static final String EXTRA_TOPIC_TITLE  = "EXTRA_TOPIC_TITLE";


    @BindView(R.id.imgFastScrollDown) ImageView    imgFastScrollDown;
    @BindView(R.id.imgFastScrollUp)   ImageView    imgFastScrollUp;
    @BindView(R.id.list)              RecyclerView recyclerView;
    @BindView(R.id.title)             TextView     title;

    private Recycler<Message, RecyclerViewHolder> mList;
    private MessagesAdapter                       mAdapter;
    private Forum                                 forum;
    public  Topic                                 currentTopic;
    private long                                  currentTopicId;
    private String                                mAccount;
    private LoadingIcon                           loadingIcon;

    private boolean messages_isLoading = false;
    private boolean focusLast;
    public  boolean modeMovePositionToLastMessage;
    public  boolean modeMovePositionToFirstMessage;
    public  boolean allowShowArrow     = true;
    public  int     movePositionToMessage;
    private int     lastLastVisiblePos;
    private int     lastFirstVisibleItem;

    private final Handler  mHandler         = new Handler();
    private final Runnable fadeOutAnimation = new Runnable() {
        @Override
        public void run() {
            allowShowArrow = true;
            if (imgFastScrollDown.getVisibility() == View.VISIBLE) {
                imgFastScrollDown.animate().alpha(0f).setDuration(700)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imgFastScrollDown.setVisibility(View.INVISIBLE);
                            }
                        });
            } else {
                imgFastScrollUp.animate().alpha(0f).setDuration(700)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                imgFastScrollUp.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        showArrowBack();

        Bundle args = getIntent().getExtras();
        if (args == null) {
            finish();
            return;
        }

        initCore();
        loadParams(args);
        initRecyclerView();

        // в ответе сервера "темы с моим участием" нет этого флага.
        // if (currentTopic.is_voting == 1) {
        loadTopicInfo(null);
        // }

        if (focusLast) {
            modeMovePositionToLastMessage = true;
            focusLastMessage(true);
        } else {
            if (Empty.is(currentTopic.getMessages())) {
                loadMessagesFrom(movePositionToMessage - LOAD_MESSAGES_COUNT / 2);
            } else {
                drawMessages();
            }
        }
    }

    @OnClick(R.id.imgFastScrollUp)
    void onArrowUpClick() {
        imgFastScrollUp.setVisibility(View.INVISIBLE);
        focusFirstMessage();
    }

    @OnClick(R.id.imgFastScrollDown)
    void onArrowDnClick() {
        imgFastScrollDown.setVisibility(View.INVISIBLE);
        focusLastMessage(false);
    }

    private void initCore() {
        forum = Forum.getInstance();
        loadingIcon = new LoadingIcon();

        modeMovePositionToLastMessage = false;
        modeMovePositionToFirstMessage = false;
    }

    private void loadParams(Bundle args) {
        String titleText = args.getString(EXTRA_TOPIC_TITLE, "");
        title.setText(Html.fromHtml(titleText));

        focusLast = args.getBoolean(EXTRA_FOCUS_LAST, false);
        movePositionToMessage = args.getInt(EXTRA_FOCUS_ON, 0);
        currentTopicId = args.getLong(EXTRA_TOPIC_ID);

        currentTopic = forum.getTopicById(currentTopicId);
        mAccount = forum.accountName;
    }

    private void initRecyclerView() {
        mAdapter = new MessagesAdapter(this, this, currentTopic, mAccount);
        mAdapter.setHasStableIds(true);
        mList = new Recycler<>(recyclerView, mAdapter);
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
            }
        });
    }

    private void loadMessages(final int messagesFrom, final int messagesTo) {
        messages_isLoading = true;
        loadingIcon.showProgress();

        netProvider.getMessages(currentTopicId, messagesFrom, messagesTo, result -> {
            currentTopic.addNewMessages(result, messagesFrom, messagesTo);
            drawMessages();
            messages_isLoading = false;
            loadingIcon.hideProgress();
        });
    }

    private void loadTopicInfo(Callback callback) {
        netProvider.getTopicInfo(currentTopicId, result -> {
            updateTopicInfo(result);
            if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    private void updateTopicInfo(Topic refreshedTopic) {
        if (refreshedTopic.id == currentTopicId) {
            currentTopic.answ = refreshedTopic.answ;
            currentTopic.closed = refreshedTopic.closed;
            currentTopic.deleted = refreshedTopic.deleted;
            currentTopic.down = refreshedTopic.down;
            currentTopic.text = refreshedTopic.text;
            currentTopic.is_voting = refreshedTopic.is_voting;
            if (refreshedTopic.getVotes() != null) {
                currentTopic.setVotes(new ArrayList<>(refreshedTopic.getVotes()));
            } else {
                currentTopic.setVotes(null);
            }

            mAdapter.notifyItemChanged(0);//TODO: check how we can handle it better
            currentTopic.updateAnsw(refreshedTopic.answ);
        }
    }

    private void loadMessagesFrom(int messageFrom) {
        messageFrom = Math.max(messageFrom, 0);
        int messageTo = messageFrom + LOAD_MESSAGES_COUNT;

        loadMessages(messageFrom, messageTo);
    }

    private void loadMessagesBefore(int messageTo) {
        if (messageTo == 0) {
            return;
        }

        int messageFrom = messageTo - LOAD_MESSAGES_COUNT;
        messageFrom = Math.max(messageFrom, 0);

        loadMessages(messageFrom, messageTo);
    }

    private void onListScrolled(int dy) {
        if (dy < 0) {
            showArrow(true);

            int firstVisibleItem = mList.findFirstVisibleItemPosition();
            if (firstVisibleItem != lastFirstVisibleItem && firstVisibleItem != NO_POSITION) {
                onScrollUpLoadMessages(firstVisibleItem);
                lastFirstVisibleItem = firstVisibleItem;
            }
        } else if (dy > 0) {
            showArrow(false);

            int lastVisiblePos = mList.findLastVisibleItemPosition();
            if (lastVisiblePos != lastLastVisiblePos && lastVisiblePos != NO_POSITION) {
                onScrollDownLoadMessages(lastVisiblePos);
                lastLastVisiblePos = lastVisiblePos;
            }
        }
    }

    private void showArrow(boolean isUp) {
        if (isUp && imgFastScrollDown.getVisibility() == View.VISIBLE ||
                !isUp && imgFastScrollUp.getVisibility() == View.VISIBLE) {
            allowShowArrow = true;
        }

        if (!allowShowArrow) {
            return;
        }

        allowShowArrow = false;
        mHandler.removeCallbacks(fadeOutAnimation);

        if (isUp) {
            imgFastScrollDown.setVisibility(View.INVISIBLE);
            imgFastScrollUp.animate().setListener(null).cancel();
            imgFastScrollUp.setAlpha(1f);
            imgFastScrollUp.setVisibility(View.VISIBLE);
        } else {

            imgFastScrollUp.setVisibility(View.INVISIBLE);
            imgFastScrollDown.animate().setListener(null).cancel();
            imgFastScrollDown.setAlpha(1f);
            imgFastScrollDown.setVisibility(View.VISIBLE);
        }
        Log.d("DBG", "showArrow: " + isUp);
        mHandler.postDelayed(fadeOutAnimation, ARROW_HID_DELAY_MILLIS);
    }

    private void onScrollUpLoadMessages(int firstVisiblePos) {
        if (messages_isLoading) {
            return;
        }
        messages_isLoading = true;

        int prevMessageN = firstVisiblePos - PREFETCH_MESSAGES_COUNT;

        if (prevMessageN < 0) {
            messages_isLoading = false;
            return;
        }

        if (!currentTopic.getMessages().get(prevMessageN).isLoaded()) {
            while (!currentTopic.getMessages().get(prevMessageN + 1).isLoaded()) {
                prevMessageN++;
                if (prevMessageN >= firstVisiblePos) {
                    messages_isLoading = false;
                    break;
                }
            }
            loadMessagesBefore(prevMessageN);
            return;
        }

        messages_isLoading = false;
    }

    private void onScrollDownLoadMessages(int lastVisiblePos) {
        if (messages_isLoading || currentTopic.getMessages() == null) {
            return;
        }
        messages_isLoading = true;

        int nextMessageN = lastVisiblePos + PREFETCH_MESSAGES_COUNT;
        if (nextMessageN > currentTopic.answ) {
            nextMessageN = currentTopic.answ;
        }
        if (currentTopic.getMessages().size() > nextMessageN) {
            if (!currentTopic.getMessages().get(nextMessageN).isLoaded()) {
                while (!currentTopic.getMessages().get(nextMessageN - 1).isLoaded()) {
                    nextMessageN--;
                    if (nextMessageN <= lastVisiblePos) {
                        messages_isLoading = false;
                        return;
                    }
                }
                loadMessagesFrom(nextMessageN);
                return;
            }
        }

        messages_isLoading = false;
    }

    @Override
    public void onUserClick(Message message) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserActivity.EXTRA_USER_NAME, message.user);
        startActivityForResult(intent, ActivityCode.USER_ACTIVITY);
    }

    @Override
    public void onMessageLongClick(View v, Message message) {
        if (!forum.isLoggedIn) {
            return;
        }

        ArrayList<Integer> items = new ArrayList<>();
        items.add(R.string.sMenuRelyToThis);

        new PopupDialog(this, items, (dialog, which) -> {
            switch (which) {
                case R.string.sMenuRelyToThis:
                    Forum forum = Forum.getInstance();
                    forum.addNewMessage(currentTopicId, message.n, MessagesActivity.this);
                    break;
            }
        });
    }

    private void drawMessages() {
        mAdapter.updateMessagesList();

        if (modeMovePositionToFirstMessage) {
            recyclerView.scrollToPosition(0);
            imgFastScrollUp.setVisibility(View.INVISIBLE);
            modeMovePositionToFirstMessage = false;
        } else if (modeMovePositionToLastMessage) {
            //TODO: recheck if it is really works as intended
            recyclerView.scrollToPosition(currentTopic.answ);
            imgFastScrollDown.setVisibility(View.INVISIBLE);
            modeMovePositionToLastMessage = false;
        } else if (movePositionToMessage > 0) {
            recyclerView.scrollToPosition(movePositionToMessage);
            movePositionToMessage = 0;
        }
    }

    public void focusFirstMessage() {
        if (currentTopic.getMessages().get(0).isLoaded()) {
            recyclerView.scrollToPosition(0);
            return;
        }

        modeMovePositionToFirstMessage = true;
        loadMessagesFrom(0);
    }

    public void focusLastMessage(boolean force) {
        if (!force && currentTopic.getMessages().get(currentTopic.getMessages().size() - 1).isLoaded()) {
            recyclerView.scrollToPosition(currentTopic.getMessages().size() - 1);
            return;
        }

        modeMovePositionToLastMessage = true;
        loadMessagesFrom(currentTopic.answ - LOAD_MESSAGES_COUNT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (forum.mainDB != null) {
            forum.mainDB.addLastPositionToMessage(currentTopicId, mList.findLastVisibleItemPosition());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ThemesManager.tintMenu(R.menu.messages, this, getMenuInflater(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        loadingIcon.init(menu.findItem(R.id.menu_reload), messages_isLoading);

        //        MenuItem mi;
        //        if (!forum.isLoggedIn) {
        //            mi = menu.findItem(R.id.menu_add);
        //            mi.setVisible(false);
        //        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_reload) {
            final int prevPos = mList.findLastVisibleItemPosition();
            final int prevAnsw = currentTopic.answ;

            loadTopicInfo(() -> {
                if (prevPos == prevAnsw) {
                    loadMessagesFrom(prevAnsw + 1);
                } else {
                    modeMovePositionToFirstMessage = true;
                    loadMessagesFrom(0);
                }
            });
            return true;
        } else if (id == R.id.menu_add) {
            Forum forum = Forum.getInstance();
            forum.addNewMessage(currentTopicId, MessagesActivity.this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPOSTRequestExecuted(String result) {
        final int prevAnsw = currentTopic.answ;
        movePositionToMessage = prevAnsw + 1;

        loadTopicInfo(() -> {
            if (forum.mainDB.isTopicInSubscriptions(currentTopicId)) {
                forum.mainDB.markTopicAsReaded(currentTopicId, currentTopic.answ);
            }

            if (prevAnsw + 1 < currentTopic.answ) {
                loadMessagesFrom(prevAnsw + 1);
            }
        });
    }

}
