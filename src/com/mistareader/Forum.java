package com.mistareader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mistareader.TextProcessors.JSONProcessor;
import com.mistareader.TextProcessors.S;
import com.mistareader.TextProcessors.S.ResultContainer;
import com.mistareader.TextProcessors.StringProcessor;

public class Forum {

    private static Forum mInstance;

    public static Forum getInstance() {
        if (mInstance == null) {
            mInstance = new Forum();
        }
        return mInstance;
    }

    public ArrayList<Topic>   topics;

    public Random             rand;

    public ArrayList<Section> sections;
    public ArrayList<String>  forums;

    static long               mm;
    static long               mm1;

    public static void Trace(String inStr) {

        mm1 = System.currentTimeMillis();
        // S.L(inStr + " - " + (mm1 - mm));
        mm = mm1;

    }

    public DB mainDB;

    private Forum() {

        topics = new ArrayList<Topic>(20);
        reachedMaxTopics = false;

        rand = new Random();

    }

    void initialize(boolean inIsInternetConnection, Activity activity) {

        isInternetConnection = inIsInternetConnection;

        if (sections == null || sections.isEmpty()) {

            new asyncGetSectionsList().execute(API.getSectionsList());
        }

        mainDB = new DB(activity);

    }

    public String                   accountName, accountPass;
    public String                   sessionID;
    public String                   accountUserID;
    public boolean                  reachedMaxTopics;
    boolean                         isInternetConnection;

    final static String             TOPIC_ATTRIBUTE_id       = "id";
    final static String             TOPIC_ATTRIBUTE_forum    = "forum";
    final static String             TOPIC_ATTRIBUTE_text     = "text";
    final static String             TOPIC_ATTRIBUTE_user0    = "user0";
    final static String             TOPIC_ATTRIBUTE_utime    = "utime";
    final static String             TOPIC_ATTRIBUTE_user     = "user";
    final static String             TOPIC_ATTRIBUTE_answ     = "answ";

    public final static int         ACTIVITY_RESULT_NEWTOPIC = 1;
    public final static int         ACTIVITY_RESULT_SETTINGS = 2;
    
    public final static String COMMAND_CREATE_NEW_TOPIC = "createnewtopic";
    
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sdf                      = new SimpleDateFormat("d MMM H:mm");

    private class asyncGetSectionsList extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... urls) {
            return WebIteraction.getServerResponse(urls[0]);
        }

        protected void onPostExecute(String result) {
            sections = JSONProcessor.parseSectionsList(result);
            forums = Section.getUniqueForums(sections);
            updateSectionsWithIndex();
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

    public Topic getTopicByid(long id) {

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

        ArrayList<Topic> newTopics = JSONProcessor.ParseTopics(JSONresult);

        if (newTopics.size() == 0) {
            reachedMaxTopics = true;
            return;
        }

        for (int i = 0; i < newTopics.size(); i++) {

            Topic newTopic = newTopics.get(i);
            Topic existingTopic = getTopicByid(newTopic.id);

            if (existingTopic == null)
                topics.add(newTopic);
            else
                updateTopic(existingTopic, newTopic);

        }

    }

    public interface iOnThemeChanged {
        void onThemeChanged(int newTheme);
    }

    public Section getSectionByName(String name) {

        for (Section locTopic : sections) {
            if (locTopic.sectionShortName.equals(name)) {
                return locTopic;
            }
        }
        return null;
    }

    private void updateSectionsWithIndex() {
        String[] section_codes = new String[45];

        section_codes[1] = "it-news";
        section_codes[10] = "math";
        section_codes[13] = "politic";
        section_codes[15] = "admin";
        section_codes[18] = "digit-photo";
        section_codes[19] = "nix";
        section_codes[2] = "philosophy";
        section_codes[20] = "sport";
        section_codes[23] = "fear";
        section_codes[24] = "mobile";
        section_codes[25] = "car";
        section_codes[26] = "love";
        section_codes[27] = "food";
        section_codes[28] = "culture";
        section_codes[29] = "science";
        section_codes[3] = "v7";
        section_codes[31] = "good";
        section_codes[32] = "games";
        section_codes[33] = "spam";
        section_codes[36] = "events";
        section_codes[38] = "realty";
        section_codes[39] = "chat";
        section_codes[4] = "web";
        section_codes[40] = "history";
        section_codes[41] = "travel";
        section_codes[42] = "english";
        section_codes[43] = "dominikana";
        section_codes[44] = "darom";
        section_codes[5] = "job";
        section_codes[6] = "forum";
        section_codes[7] = "lol";
        section_codes[8] = "v8";

        for (int i = 1; i < section_codes.length; i++) {
            Section section = getSectionByName(section_codes[i]);
            if (section != null) {
                section.sectionId = Integer.toString(i);

            }
            // else
            // S.L(section_codes[i]);
        }
    }

    public void selectTheme(final Activity activity) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                iOnThemeChanged mCallback = (iOnThemeChanged) activity;
                mCallback.onThemeChanged(++item);

                dialog.cancel();
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.sTheme);
        builder.setIcon(ThemesManager.iconThemes);
        builder.setSingleChoiceItems(R.array.arrThemes, ThemesManager.CurrentTheme - 1, listener);
        builder.setPositiveButton(R.string.sCancel, null);
        builder.create().show();

    }

    // ************************************SETTINGS*******************************
    public void loadSettings(SharedPreferences sPref) {

        accountName = sPref.getString(Settings_Activity.SETTINGS_ACCOUNT_NAME, "");
        accountPass = sPref.getString(Settings_Activity.SETTINGS_ACCOUNT_PASS, "");
        sessionID = sPref.getString(Settings_Activity.SETTINGS_SESSION_ID, "");
        accountUserID = sPref.getString(Settings_Activity.SETTINGS_ACCOUNT_USER_ID, "");

        String sSections = sPref.getString(Settings_Activity.SETTINGS_SECTIONS, "");
        sections = Section.getSectionsFromString(sSections);
        updateSectionsWithIndex();

        forums = Section.getUniqueForums(sections);

    }

    public void saveSettings(SharedPreferences sPref) {

        Editor ed = sPref.edit();

        ed.clear();
        ed.putString(Settings_Activity.SETTINGS_ACCOUNT_NAME, accountName);
        ed.putString(Settings_Activity.SETTINGS_ACCOUNT_PASS, accountPass);
        ed.putString(Settings_Activity.SETTINGS_SESSION_ID, sessionID);
        ed.putString(Settings_Activity.SETTINGS_ACCOUNT_USER_ID, accountUserID);

        String sSections = Section.getSectionsAsString(sections);
        ed.putString(Settings_Activity.SETTINGS_SECTIONS, sSections);

        ed.putInt(ThemesManager.SETTINGS_THEME, ThemesManager.CurrentTheme);
        ed.putString(Settings_Activity.SETTINGS_VESION, Settings_Activity.SETTINGS_VESION_N);

        ed.commit();

    }

    public void saveSettings(Activity activity) {

        SharedPreferences sPref = activity.getPreferences(Context.MODE_PRIVATE);
        saveSettings(sPref);

    }

    // ************************************LOGIN*********************************
    public interface iOnLoggedIn {
        void onLoggedIn(boolean isLoggedIn);
    }

    public void setupAccount(final Activity activity) {

        final AlertDialog dialog;

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.sAccount);
        builder.setIcon(ThemesManager.iconAccount);

        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_login, null);
        builder.setView(view);
        builder.setCancelable(true);

        final EditText usernameBox = ((EditText) view.findViewById(R.id.login_username));
        usernameBox.setText(accountName);

        final EditText passwordBox = ((EditText) view.findViewById(R.id.login_password));
        passwordBox.setText(accountPass);

        usernameBox.requestFocus();
        usernameBox.requestFocusFromTouch();

        builder.setPositiveButton(R.string.sDoLogin, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int i) {

                String username = usernameBox.getText().toString().trim();
                String password = passwordBox.getText().toString();

                doLogin(activity, username, password, false);

            }

        });

        builder.setNegativeButton(R.string.sCancel, null);

        dialog = builder.create();

        passwordBox.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).performClick();
                    handled = true;
                }
                return handled;
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }

    protected void doLogin(Activity activity, String username, String password, boolean silentMode) {

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        String URL;
        URL = API.Login(username, password);

        iOnLoggedIn mCallback = (iOnLoggedIn) activity;

        new requestAsyncLogin(activity, mCallback, username, password, silentMode).execute(URL);
    }

    public class requestAsyncLogin extends AsyncTask<String, Integer, WebIteraction.hashResult> {
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

        protected WebIteraction.hashResult doInBackground(String... urls) {
            return WebIteraction.getServerResponseWithCookie(urls[0]);
        }

        protected void onPostExecute(WebIteraction.hashResult result) {

            ResultContainer res = JSONProcessor.parseLogin(result.result);

            if (!mSilentMode) {
                progress.dismiss();
            }

            if (res.result) {

                accountName = mUsername;
                accountPass = mPpassword;
                sessionID = result.sessionID;
                accountUserID = res.userID;

                saveSettings(activity);

                listener.onLoggedIn(true);

            }
            else {

                sessionID = "";
                if (!mSilentMode) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this.context);

                    dlgAlert.setMessage(res.errorString);
                    dlgAlert.setTitle(R.string.sAuthorizationError);
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.create().show();
                }

                listener.onLoggedIn(false);
            }

        }

    }

    // ************************************TOPIC*********************************
    public interface iOnPOSTRequestExecuted {
        void onPOSTRequestExecuted(String result);
    }

    public void addNewTopic(final Activity activity) {

        Intent intent = new Intent();
        intent.setClass(activity, NewTopic_Activity.class);

        // Intent intent = new Intent(activity, NewTopic_Activity.class);
        activity.startActivityForResult(intent, ACTIVITY_RESULT_NEWTOPIC);

    };

    public void createNewTopic(Topics_Activity activity, Bundle args) {

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

        S.L(forumName + " posting message:" + sectionIndex + " s:" + subject + " m:" + message);

        String URL = API.addNewTopic();

        final WebIteraction.POST newMessagePOST = new WebIteraction.POST();

        newMessagePOST.url = URL;
        newMessagePOST.cookie = API.COOKIE_SESSION_ID + "=" + sessionID + "; " + API.COOKIE_USER_ID + "=" + accountUserID;
        newMessagePOST.POSTString = API.POST_message_text + StringProcessor.mista_URL_Encode(message) + "&" + API.POST_action + API.action_New + "&"
                + API.POST_topic_text + StringProcessor.mista_URL_Encode(subject) + "&" + API.POST_target_forum + forumName.toLowerCase() + "&"
                + API.POST_target_section + sectionIndex + "&" + API.POST_rnd + "" + Math.abs(rand.nextLong());

        if (isVoting) {
            newMessagePOST.POSTString = newMessagePOST.POSTString + "&" + API.POST_voting + "&" + API.POST_select1 + select1 + "&" + API.POST_select2 + select2
                    + "&" + API.POST_select3 + select3 + "&" + API.POST_select4 + select4 + "&" + API.POST_select5 + select5;
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

    };

    public void addNewMessage(final long curTopicId, int replyTo, final Activity activity) {

        final Topic curTopic = getTopicByid(curTopicId);
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
        }
        else
            spinVote = null;

        final EditText editSubject = ((EditText) dialogView.findViewById(R.id.editNewMessage));
        if (replyTo >= 0) {
            editSubject.append("(" + replyTo + ") ");
        }

        editSubject.requestFocus();
        editSubject.requestFocusFromTouch();

        builder.setPositiveButton(R.string.sCreate, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int i) {}

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
                            S.L("vote: " + vote);
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
        newMessagePOST.cookie = API.COOKIE_SESSION_ID + "=" + sessionID + "; " + API.COOKIE_USER_ID + "=" + accountUserID;
        newMessagePOST.POSTString = API.POST_message_text + StringProcessor.mista_URL_EncodePlus(message) + "&" + API.POST_action + API.action_New + "&"
                + API.POST_topic_id + curTopicId + "&" + API.POST_user_name + StringProcessor.mista_URL_Encode(accountName) + "&" + API.POST_rnd + ""
                + Math.abs(rand.nextLong());

        if (vote > 0) {
            newMessagePOST.POSTString = newMessagePOST.POSTString + "&" + API.POST_vote + vote;
        }

        iOnPOSTRequestExecuted mCallback = (iOnPOSTRequestExecuted) activity;

        new requestAsyncPOST(activity, mCallback).execute(newMessagePOST);

    }

    public class requestAsyncPOST extends AsyncTask<WebIteraction.POST, Integer, WebIteraction.PostResult> {
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

        protected WebIteraction.PostResult doInBackground(WebIteraction.POST... urls) {
            return WebIteraction.postWebRequest(urls[0]);
        }

        protected void onPostExecute(WebIteraction.PostResult result) {

            // S.L(result.result);
            progress.dismiss();

            listener.onPOSTRequestExecuted(result.result);

        }

    }

    // ****************************ABOUT*****************************************
    public void showAbout(Topics_Activity topics_Activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(topics_Activity);

        final View dialogView = topics_Activity.getLayoutInflater().inflate(R.layout.about, null);

        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);

        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showWhatsNew(SharedPreferences sPref, Topics_Activity topics_Activity) {

        if (!sPref.getString(Settings_Activity.SETTINGS_VESION, "").equals(Settings_Activity.SETTINGS_VESION_N)) {

            Editor ed = sPref.edit();
            ed.putString(Settings_Activity.SETTINGS_VESION, Settings_Activity.SETTINGS_VESION_N);
            ed.commit();

            final AlertDialog.Builder builder = new AlertDialog.Builder(topics_Activity);

            builder.setTitle(R.string.sWHatsNew);

            final View dialogView = topics_Activity.getLayoutInflater().inflate(R.layout.whatsnew, null);

            TextView mTextView1 = (TextView) dialogView.findViewById(R.id.textWhatsNewDetail);
            mTextView1.setText(Html.fromHtml(topics_Activity.getString(R.string.sWHatsNewDetal2)));
            mTextView1.setMovementMethod(new ScrollingMovementMethod());

            builder.setView(dialogView);
            builder.setCancelable(true);
            builder.setPositiveButton("OK", null);

            final AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    public void delayedLogin(final Activity activity) {
        final int DEFAUTL_DELAY = 1000;

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

        loginHandler.postDelayed(loginProcedure, DEFAUTL_DELAY);

    }

    public void delayedStartNotifications() {
        final int DEFAUTL_DELAY = 700;

        final Handler backgroundHandler = new Handler();
        final Runnable backgroundProcedure = new Runnable() {
            @Override
            public void run() {

                // startService(intent.putExtra("time", 3).putExtra("label", "Call 1"));

            }
        };

        backgroundHandler.postDelayed(backgroundProcedure, DEFAUTL_DELAY);

    }

}
