package com.mistareader.model;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.api.API;
import com.mistareader.api.WebIteraction;
import com.mistareader.api.WebIteraction.RequestResult;
import com.mistareader.ui.topics.NewTopicActivity;
import com.mistareader.ui.topics.TopicsActivity;
import com.mistareader.util.DB;
import com.mistareader.util.S;
import com.mistareader.util.S.ResultContainer;
import com.mistareader.util.Settings;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.textProcessors.JSONProcessor;
import com.mistareader.util.textProcessors.StringUtils;

import java.util.ArrayList;
import java.util.Random;

public class Forum {
    private static final int DEFAULT_LOGIN_DELAY = 1000;

    private static Forum mInstance;

    public static Forum getInstance() {
        if (mInstance == null) {
            mInstance = new Forum();
        }
        return mInstance;
    }

    public ArrayList<Topic> topics;

    public Random rand;

    public ArrayList<Section> sections;
    public ArrayList<String>  forums;

    public DB mainDB;

    private Forum() {

        topics = new ArrayList<>(20);
        reachedMaxTopics = false;

        rand = new Random();

    }

    public void initialize(Activity activity) {
        isInternetConnection = WebIteraction.isInternetAvailable(activity);

        if (sections == null || sections.isEmpty()) {
            new asyncGetSectionsList(sessionCookies).execute(API.getSectionsList());
        }

        mainDB = new DB(activity);
    }

    public void reloadSections(Activity activity) {
        new asyncGetSectionsList(activity, sessionCookies).execute(API.getSectionsList());
    }

    public String accountName, accountPass;
    public String sessionID;
    public String accountUserID;
    public String sessionCookies;

    public boolean reachedMaxTopics;
    public boolean isInternetConnection;

    final static String TOPIC_ATTRIBUTE_id    = "id";
    final static String TOPIC_ATTRIBUTE_forum = "forum";
    final static String TOPIC_ATTRIBUTE_text  = "text";
    final static String TOPIC_ATTRIBUTE_user0 = "user0";
    final static String TOPIC_ATTRIBUTE_utime = "utime";
    final static String TOPIC_ATTRIBUTE_user  = "user";
    final static String TOPIC_ATTRIBUTE_answ  = "answ";

    public final static int ACTIVITY_RESULT_NEWTOPIC = 1;
    public final static int ACTIVITY_RESULT_SETTINGS = 2;

    public final static String COMMAND_CREATE_NEW_TOPIC = "createnewtopic";


    private class asyncGetSectionsList extends AsyncTask<String, Integer, RequestResult> {

        private boolean        mSilentMode;
        private Activity       mActivity;
        private ProgressDialog progress;

        public asyncGetSectionsList(String sessionCookies) {
            mSilentMode = true;
        }

        public asyncGetSectionsList(Activity activity, String sessionCookies) {
            mActivity = activity;
            mSilentMode = false;
            progress = new ProgressDialog(mActivity);

        }

        @Override
        protected void onPreExecute() {
            if (!mSilentMode) {
                this.progress.setMessage(mActivity.getString(R.string.sLoading));
                this.progress.show();
            }
        }

        protected RequestResult doInBackground(String... urls) {
            return WebIteraction.doServerRequest(urls[0], sessionCookies);
        }

        protected void onPostExecute(RequestResult result) {
            if (!result.cookie.isEmpty()) {
                sessionCookies = result.cookie;
            }
            sections = JSONProcessor.parseSectionsList(result.resultStr);
            if (sections.isEmpty())
                sections = Section.fillDefauiltSectionsList();

            forums = Section.getUniqueForums(sections);

            if (!mSilentMode) {
                progress.dismiss();
            }

        }

    }

    public void updateTopic(Topic sourceTopic, Topic newTopic) {
        sourceTopic.answ = newTopic.answ;
        sourceTopic.closed = newTopic.closed;
        sourceTopic.down = newTopic.down;
        sourceTopic.is_voting = newTopic.is_voting;
        sourceTopic.forum = newTopic.forum;
        sourceTopic.sect1 = newTopic.sect1;
        sourceTopic.text = newTopic.text;
        sourceTopic.user = newTopic.user;
    }

    public long getLastTopicTime() {

        if (topics == null || topics.isEmpty()) {
            return 0;
        }

        return topics.get(topics.size() - 1).utime;

    }

    public long getFirstTopicTime() {

        if (topics == null || topics.isEmpty()) {
            return 0;
        }

        return topics.get(0).utime;

    }

    public Topic getTopicById(long id) {
        for (Topic locTopic : topics) {
            if (locTopic.id == id) {
                return locTopic;
            }
        }
        return null;
    }

    public void deleteTopics() {
        topics.clear();
        reachedMaxTopics = false;
    }

    public void addNewTopics(String JSONresult) {

        ArrayList<Topic> newTopics = JSONProcessor.parseTopics(JSONresult);

        if (newTopics.size() == 0) {
            reachedMaxTopics = true;
            return;
        }

        for (int i = 0; i < newTopics.size(); i++) {
            Topic newTopic = newTopics.get(i);
            Topic existingTopic = getTopicById(newTopic.id);

            if (existingTopic == null) topics.add(newTopic);
            else updateTopic(existingTopic, newTopic);
        }
    }

    public void setTopicsList(ArrayList<Topic> newTopics, long beforeUTime) {
        if (newTopics.size() == 0 && beforeUTime > 0) {
            reachedMaxTopics = true;
            return;
        }

        topics = newTopics;
    }

    public void addTopicsList(ArrayList<Topic> newTopics) {
        if (newTopics.size() == 0) {
            reachedMaxTopics = true;
            return;
        }

        if (topics == null) {
            topics = newTopics;
        } else {
            topics.addAll(newTopics);
        }
    }

    public interface iOnThemeChanged {
        void onThemeChanged(int newTheme);
    }

    // ************************************SETTINGS*******************************
    public void loadSettings(SharedPreferences sPref) {

        accountName = sPref.getString(Settings.SETTINGS_ACCOUNT_NAME, "");
        accountPass = sPref.getString(Settings.SETTINGS_ACCOUNT_PASS, "");
        sessionID = sPref.getString(Settings.SETTINGS_SESSION_ID, "");
        accountUserID = sPref.getString(Settings.SETTINGS_ACCOUNT_USER_ID, "");
        sessionCookies = sPref.getString(Settings.SETTINGS_COOKIES, "");

        String sSections = sPref.getString(Settings.SETTINGS_SECTIONS, "");
        sections = Section.getSectionsFromString(sSections);

        forums = Section.getUniqueForums(sections);

    }

    public void saveSettings(SharedPreferences sPref) {
        Editor ed = sPref.edit();

//        ed.clear();
        ed.putString(Settings.SETTINGS_ACCOUNT_NAME, accountName);
        ed.putString(Settings.SETTINGS_ACCOUNT_PASS, accountPass);
        ed.putString(Settings.SETTINGS_SESSION_ID, sessionID);
        ed.putString(Settings.SETTINGS_ACCOUNT_USER_ID, accountUserID);
        ed.putString(Settings.SETTINGS_COOKIES, sessionCookies);

        String sSections = Section.getSectionsAsString(sections);
        ed.putString(Settings.SETTINGS_SECTIONS, sSections);

//        ed.putInt(Settings.SETTINGS_THEME, ThemesManager.currentTheme);
        ed.putString(Settings.SETTINGS_VERSION, Settings.SETTINGS_VERSION_N);

        ed.apply();
    }

    public void saveSettings(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        saveSettings(sPref);
    }

    // ************************************LOGIN*********************************
    public interface iOnLoggedIn {
        void onLoggedIn(boolean isLoggedIn);
    }

    public void setupAccount(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_login, null);
        builder.setView(view);
        builder.setCancelable(true);

        final TextInputEditText usernameBox = view.findViewById(R.id.login_username);
        usernameBox.setText(accountName);

        final TextInputEditText passwordBox = view.findViewById(R.id.login_password);
        passwordBox.setText(accountPass);

        usernameBox.requestFocus();
        usernameBox.requestFocusFromTouch();

        builder.setPositiveButton(R.string.sDoLogin, (dialogInterface, i) -> {
            String username = usernameBox.getText().toString().trim();
            String password = passwordBox.getText().toString();

            doLogin(activity, username, password, false);
        });

        builder.setNegativeButton(R.string.sCancel, null);

        AlertDialog dialog = builder.create();
        passwordBox.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(Dialog.BUTTON_POSITIVE).performClick();
                handled = true;
            }
            return handled;
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }

    private void doLogin(Activity activity, String username, String password, boolean silentMode) {

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        String URL;
        URL = API.login(username, password);

        iOnLoggedIn mCallback = null;
        if (activity instanceof iOnLoggedIn) {
            mCallback = (iOnLoggedIn) activity;
        }

        new requestAsyncLogin(activity, mCallback, username, password, silentMode).execute(URL);
    }

    public class requestAsyncLogin extends AsyncTask<String, Integer, RequestResult> {
        private Activity       activity;
        private ProgressDialog progress;
        private Context        context;
        private iOnLoggedIn    listener;
        private String         mUsername;
        private String         mPpassword;
        private boolean        mSilentMode;

        public requestAsyncLogin(Activity activity, iOnLoggedIn mCallback, String username, String password, boolean silentMode) {
            this.activity = activity;
            context = activity;
            listener = mCallback;
            mUsername = username;
            mPpassword = password;
            mSilentMode = silentMode;

            if (!mSilentMode) {
                progress = new ProgressDialog(context);
            }
        }

        @Override
        protected void onPreExecute() {
            if (!mSilentMode) {
                this.progress.setMessage(activity.getString(R.string.sAuthorizationInProgress));
                this.progress.show();
            }
        }

        protected RequestResult doInBackground(String... urls) {
            return WebIteraction.doServerRequest(urls[0], sessionCookies);
        }

        protected void onPostExecute(RequestResult result) {
            if (!result.cookie.isEmpty()) {
                sessionCookies = result.cookie;
            }

            ResultContainer res = JSONProcessor.parseLogin(result.resultStr);

            if (!mSilentMode) {
                progress.dismiss();
            }

            if (res.result) {

                accountName = mUsername;
                accountPass = mPpassword;
                sessionID = res.resultSessionID;
                accountUserID = res.userID;

                saveSettings(activity);

                if (listener != null) {
                    listener.onLoggedIn(true);
                }

            } else {

                sessionID = "";
                if (!mSilentMode) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this.context);

                    dlgAlert.setMessage(res.resultStr);
                    dlgAlert.setTitle(R.string.sAuthorizationError);
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.create().show();
                }
                if (listener != null) {
                    listener.onLoggedIn(false);
                }
            }

        }

    }

    // ************************************TOPIC*********************************
    public interface iOnPOSTRequestExecuted {
        void onPOSTRequestExecuted(String result);
    }

    public void addNewTopic(final Activity activity) {

        Intent intent = new Intent();
        intent.setClass(activity, NewTopicActivity.class);

        activity.startActivityForResult(intent, ACTIVITY_RESULT_NEWTOPIC);

    }

    public void createNewTopic(TopicsActivity activity, Bundle args) {

        String select1 = "";
        String select2 = "";
        String select3 = "";
        String select4 = "";
        String select5 = "";

        String forumName = args.getString("forumName");
        String sectionIndex = args.getString("sectionIndex");
        String subject = args.getString("subject");
        String message = args.getString("message");

        boolean isVoting = args.getBoolean("isVoting");
        if (isVoting) {
            select1 = args.getString("select1");
            select2 = args.getString("select2");
            select3 = args.getString("select3");
            select4 = args.getString("select4");
            select5 = args.getString("select5");
        }

        String URL = API.addNewTopic();

        final WebIteraction.POST newMessagePOST = new WebIteraction.POST();

        newMessagePOST.url = URL;
        newMessagePOST.cookie = buildCookie();
        newMessagePOST.POSTString = API.POST_message_text + StringUtils.mista_URL_Encode(message) + "&" + API.POST_action + API.action_New + "&" + API.POST_topic_text + StringUtils.mista_URL_Encode(subject) + "&" + API.POST_target_forum + forumName.toLowerCase() + "&" + API.POST_target_section + sectionIndex + "&" + API.POST_rnd + "" + Math.abs(rand.nextLong());

        if (isVoting) {
            newMessagePOST.POSTString = newMessagePOST.POSTString + "&" + API.POST_voting + "&" + API.POST_select1 + select1 + "&" + API.POST_select2 + select2 + "&" + API.POST_select3 + select3 + "&" + API.POST_select4 + select4 + "&" + API.POST_select5 + select5;
        }

        iOnPOSTRequestExecuted mCallback = (iOnPOSTRequestExecuted) activity;

        new requestAsyncPOST(activity, mCallback).execute(newMessagePOST);

    }

    // ******************************MESSAGES************************************
    class extSpinnerAdapder extends ArrayAdapter<String> {
        private Context           mContext;
        private ArrayList<String> mItems;

        public extSpinnerAdapder(Context context, ArrayList<String> votes) {
            super(context, 0, votes);
            mContext = context;
            mItems = votes;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.votes_dropdowntext, parent, false);

            if (position == getCount()) {
                textView.setText("");
                textView.setHint(getItem(getCount())); // Hint
                return textView;
            }

            textView.setText(mItems.get(position));
            return textView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            final TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.votes_dropdowntext, parent, false);
            textView.setText(mItems.get(position));
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setSingleLine(false);
                }
            });
            return textView;
        }

        @Override
        public int getCount() {
            return super.getCount() - 1; // Hint
        }

    }

    public String buildCookie() {
        return sessionCookies + (!sessionCookies.isEmpty() ? ";" : "") + API.COOKIE_SESSION_ID + "=" + sessionID + "; " + API.COOKIE_USER_ID + "=" + accountUserID;
    }

    public void addNewMessage(final long curTopicId, int replyTo, final Activity activity) {

        final Topic curTopic = getTopicById(curTopicId);
        if (curTopic == null || (curTopic.is_voting == 1 && curTopic.votes == null)) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.sNewMessage);
        builder.setIcon(ThemesManager.iconNewItem);

        final View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_message, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        final Spinner spinVote;
        if (curTopic.is_voting == 1) {
            spinVote = ((Spinner) dialogView.findViewById(R.id.spinVote));

            ArrayList<String> votes = new ArrayList<String>();
            for (int i = 0; i < curTopic.votes.size(); i++) {
                Topic.Votes curVote = curTopic.votes.get(i);
                if (curVote.voteName == null || curVote.voteName.isEmpty()) {
                    break;
                }
                votes.add((i + 1) + ". " + curVote.voteName);
            }
            votes.add(activity.getString(R.string.sVoteHint));

            spinVote.setAdapter(new extSpinnerAdapder(activity, votes));
            spinVote.setSelection(votes.size() - 1);

            spinVote.setVisibility(View.VISIBLE);
        } else spinVote = null;

        final EditText editSubject = ((EditText) dialogView.findViewById(R.id.editNewMessage));
        if (replyTo >= 0) {
            editSubject.append("(" + replyTo + ") ");
        }

        editSubject.requestFocus();
        editSubject.requestFocusFromTouch();

        builder.setPositiveButton(R.string.sCreate, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int i) {
            }

        });

        builder.setNegativeButton(R.string.sCancel, null);

        final AlertDialog dialog;

        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String message = ((EditText) dialogView.findViewById(R.id.editNewMessage)).getText().toString().trim();

                        if (message.isEmpty()) {
                            ErrorMessage.Show(R.string.sMessageError, activity);
                            return;
                        }

                        int vote = 0;
                        if (curTopic.is_voting == 1) {
                            vote = spinVote.getSelectedItemPosition() + 1;
                            if (vote >= spinVote.getCount()) {
                                vote = 0;
                            }
                        }

                        createNewMessage(activity, curTopicId, message, vote);
                        dialog.dismiss();

                    }
                });
            }
        });

        dialog.show();

    }

    public void addNewMessage(final long curTopicId, final Activity activity) {

        addNewMessage(curTopicId, -1, activity);

    }

    protected void createNewMessage(Activity activity, long curTopicId, String message, int vote) {

        String URL = API.postNewMessages();

        final WebIteraction.POST newMessagePOST = new WebIteraction.POST();

        newMessagePOST.url = URL;
        newMessagePOST.cookie = buildCookie();
        newMessagePOST.POSTString = API.POST_message_text + StringUtils.mista_URL_EncodePlus(message) + "&" + API.POST_action + API.action_New + "&" + API.POST_topic_id + curTopicId + "&" + API.POST_user_name + StringUtils.mista_URL_Encode(accountName) + "&" + API.POST_rnd + "" + Math.abs(rand.nextLong());

        if (vote > 0) {
            newMessagePOST.POSTString = newMessagePOST.POSTString + "&" + API.POST_vote + vote;
        }

        iOnPOSTRequestExecuted mCallback = (iOnPOSTRequestExecuted) activity;

        new requestAsyncPOST(activity, mCallback).execute(newMessagePOST);

    }

    public class requestAsyncPOST extends AsyncTask<WebIteraction.POST, Integer, RequestResult> {
        private Activity               activity;
        private ProgressDialog         progress;
        private Context                context;
        private iOnPOSTRequestExecuted listener;

        public requestAsyncPOST(Activity activity, iOnPOSTRequestExecuted listener) {
            this.activity = activity;
            context = activity;
            progress = new ProgressDialog(context);
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            this.progress.setMessage(activity.getString(R.string.sAddingInProgress));
            this.progress.show();
        }

        protected RequestResult doInBackground(WebIteraction.POST... urls) {
            return WebIteraction.postWebRequest(urls[0]);
        }

        protected void onPostExecute(RequestResult result) {

            progress.dismiss();

            listener.onPOSTRequestExecuted(result.resultStr);

        }

    }

    // ****************************ABOUT*****************************************
    public void showAbout(final TopicsActivity main_Activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(main_Activity);

        final View dialogView = main_Activity.getLayoutInflater().inflate(R.layout.dialog_about, null);

        Button marketButton = (Button) dialogView.findViewById(R.id.buttonMarket);
        marketButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.mistareader"));
                main_Activity.startActivity(intent);
            }

        });

        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);

        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showWhatsNew(SharedPreferences sPref, TopicsActivity main_Activity) {

        //        if (!sPref.getString(Settings.SETTINGS_VERSION, "").equals(Settings.SETTINGS_VERSION_N)) {
        //
        //            Editor ed = sPref.edit();
        //            ed.putString(Settings.SETTINGS_VERSION, Settings.SETTINGS_VERSION_N);
        //            ed.commit();
        //
        //            final AlertDialog.Builder builder = new AlertDialog.Builder(main_Activity);
        //
        //            builder.setTitle(R.string.sWHatsNew);
        //
        //            final View dialogView = main_Activity.getLayoutInflater().inflate(R.layout.whatsnew, null);
        //
        //            TextView mTextView1 = (TextView) dialogView.findViewById(R.id.textWhatsNewDetail);
        //            mTextView1.setText(Html.fromHtml(main_Activity.getString(R.string.sWHatsNewDetal2)));
        //            mTextView1.setMovementMethod(new ScrollingMovementMethod());
        //
        //            builder.setView(dialogView);
        //            builder.setCancelable(true);
        //            builder.setPositiveButton("OK", null);
        //
        //            final AlertDialog dialog = builder.create();
        //            dialog.show();
        //
        //        }

    }

    public void delayedLogin(final Activity activity) {
        if (!isInternetConnection) {
            return;
        }

        final Handler loginHandler = new Handler();
        final Runnable loginProcedure = new Runnable() {
            @Override
            public void run() {

                doLogin(activity, accountName, accountPass, true);

            }
        };

        loginHandler.postDelayed(loginProcedure, DEFAULT_LOGIN_DELAY);

    }

    public boolean isLoggedIn() {
        return !S.isEmpty(sessionID);
    }
}
