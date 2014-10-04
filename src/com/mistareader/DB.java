package com.mistareader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mistareader.TextProcessors.S;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME                      = "main.db";
    private static final int    DB_VERSION                   = 5;

    private static final String TABLE_VIEW_HISTORY           = "topicviewhistory";
    private static final String TABLE_SUBSCRIPTIONS          = "topicsubscriptions";
    private static final String TABLE_LOG                    = "log";

    private static final String FIELD_TOPIC_ID               = "id";

    // *******Messages
    private static final String FIELD_MESSAGE_ID             = "message";
    private static final String FIELD_MESSAGE_TIMESTAMP      = "timestamp";

    // *******subscriptions
    private static final String FIELD_TOPIC_CUR_MESS_COUNT   = "curansw";
    private static final String FIELD_TOPIC_ADDED_MESS_COUNT = "addedansw";
    private static final String FIELD_TOPIC_TEXT             = "topicheader";
    private static final String FIELD_TOPIC_LAST_USER        = "lastuser";
    private static final String FIELD_TOPIC_LAST_TIME        = "lasttime";
    private static final String FIELD_TOPIC_LAST_TIME_TEXT   = "lasttimetext";
    private static final String FIELD_TOPIC_AUTHOR           = "author";
    private static final String FIELD_TOPIC_SECTION          = "section";

    private static final String FIELD_TIME                   = "time";
    private static final String FIELD_TEXT                   = "text";

    DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryString = String.format("CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY," + "%s INTEGER," + "%s INTEGER)", TABLE_VIEW_HISTORY, FIELD_TOPIC_ID,
                FIELD_MESSAGE_ID, FIELD_MESSAGE_TIMESTAMP);

        db.execSQL(queryString);

        queryString = String.format(
                "CREATE TABLE %s ( %s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_SUBSCRIPTIONS, FIELD_TOPIC_ID, FIELD_TOPIC_CUR_MESS_COUNT, FIELD_TOPIC_ADDED_MESS_COUNT, FIELD_TOPIC_TEXT, FIELD_TOPIC_LAST_USER,
                FIELD_TOPIC_LAST_TIME, FIELD_TOPIC_LAST_TIME_TEXT, FIELD_TOPIC_AUTHOR, FIELD_TOPIC_SECTION);

        db.execSQL(queryString);

        queryString = String.format("CREATE TABLE %s (" + "%s TEXT," + "%s TEXT)", TABLE_LOG, FIELD_TIME, FIELD_TEXT);

        db.execSQL(queryString);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEW_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
            onCreate(db);
        }

    }

    public void addLastPositionToMessage(long currentTopicId, int messageID) {

        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_TOPIC_ID, currentTopicId);
        row.put(FIELD_MESSAGE_ID, messageID);
        row.put(FIELD_MESSAGE_TIMESTAMP, System.currentTimeMillis());

        final long result = db.replace(TABLE_VIEW_HISTORY, null, row);

        if (result < 0) {
            S.L("DB insert failed. topicID: " + currentTopicId + " messageID:" + messageID);
        }

    }

    public int getLastPositionForMessage(long id) {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT MESSAGE FROM " + TABLE_VIEW_HISTORY + " WHERE " + FIELD_TOPIC_ID + " = ? LIMIT 1",
                new String[] { Long.toString(id) });

        int res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }

        cursor.close();

        return res;

    }

    // *****************************SUBSCRIPTIONS****************************
    public void addTopicToSubscriptions(Topic curTopic) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_TOPIC_ID, curTopic.id);
        row.put(FIELD_TOPIC_CUR_MESS_COUNT, curTopic.answ);
        row.put(FIELD_TOPIC_TEXT, curTopic.text);
        row.put(FIELD_TOPIC_ADDED_MESS_COUNT, 0);
        row.put(FIELD_TOPIC_LAST_USER, curTopic.user);
        row.put(FIELD_TOPIC_LAST_TIME, curTopic.utime);
        row.put(FIELD_TOPIC_LAST_TIME_TEXT, curTopic.time_text);
        row.put(FIELD_TOPIC_AUTHOR, curTopic.user0);
        row.put(FIELD_TOPIC_SECTION, curTopic.sect1);

        final long result = db.replace(TABLE_SUBSCRIPTIONS, null, row);

        if (result < 0) {
            S.L("DB insert failed. topicID: " + curTopic.id);
        }

    }

    public void updateTopicInSubscriptions(Topic newSubscription) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        long topicID = newSubscription.id;

        row.put(FIELD_TOPIC_CUR_MESS_COUNT, newSubscription.answ);
        row.put(FIELD_TOPIC_ADDED_MESS_COUNT, newSubscription.newAnsw);
        row.put(FIELD_TOPIC_LAST_USER, newSubscription.user);
        row.put(FIELD_TOPIC_LAST_TIME, newSubscription.utime);
        row.put(FIELD_TOPIC_LAST_TIME_TEXT, newSubscription.time_text);

        final long result = db.update(TABLE_SUBSCRIPTIONS, row, FIELD_TOPIC_ID + " = " + topicID, null);

        if (result < 0) {
            S.L("DB update failed. topicID: " + topicID);
        }

    }

    public ArrayList<Topic> getSubscriptions() {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT " + FIELD_TOPIC_ID + ", " + FIELD_TOPIC_CUR_MESS_COUNT + ", " + FIELD_TOPIC_TEXT + ", "
                + FIELD_TOPIC_ADDED_MESS_COUNT + ", " + FIELD_TOPIC_LAST_USER + ", " + FIELD_TOPIC_LAST_TIME + ", " + FIELD_TOPIC_LAST_TIME_TEXT + ", "
                + FIELD_TOPIC_AUTHOR + ", " + FIELD_TOPIC_SECTION + " FROM " + TABLE_SUBSCRIPTIONS + " ORDER BY " + FIELD_TOPIC_LAST_TIME + " DESC", null);

        ArrayList<Topic> res = new ArrayList<Topic>();

        Topic newSub;
        while (cursor.moveToNext()) {

            newSub = new Topic();

            newSub.id = cursor.getLong(0);
            newSub.answ = cursor.getInt(1);
            newSub.text = cursor.getString(2);
            newSub.newAnsw = cursor.getInt(3);
            newSub.user = cursor.getString(4);
            newSub.utime = cursor.getInt(5);
            newSub.time_text = cursor.getString(6);
            newSub.user0 = cursor.getString(7);
            newSub.sect1 = cursor.getString(8);

            res.add(newSub);
        }

        cursor.close();

        return res;

    }

    public boolean isTopicInSubscriptions(long id) {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_SUBSCRIPTIONS + " WHERE " + FIELD_TOPIC_ID + " = ? LIMIT 1",
                new String[] { Long.toString(id) });

        boolean res = false;
        if (cursor.getCount() > 0) {
            res = true;
        }
        cursor.close();

        return res;
    }

    public void removeTopicFromSubscriptions(long id) {

        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIPTIONS, FIELD_TOPIC_ID + "=?", new String[] { String.valueOf(id) });

    }

    public void removeAllSubscriptions() {

        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SUBSCRIPTIONS);

    }

    public void markAllSubscriptionsAsReaded() {

        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_SUBSCRIPTIONS + " SET " + FIELD_TOPIC_ADDED_MESS_COUNT + " = 0");

    }

    public void markTopicAsReaded(long id) {

        final SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_SUBSCRIPTIONS + " SET " + FIELD_TOPIC_ADDED_MESS_COUNT + " = 0 WHERE " + FIELD_TOPIC_ID + " = ?",
                new String[] { Long.toString(id) });

    }

    public void printAllSubscriptions() {

        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIPTIONS, null);
        while (cursor.moveToNext()) {
            S.L(">> " + cursor.getString(3) + " - " + cursor.getInt(0) + "==" + cursor.getInt(1) + "==" + cursor.getInt(2));
        }

    }

    public int getTotalSubscriptionsCount() {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SUBSCRIPTIONS, null);

        int res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();

        return res;
    }

    public int getNewSubscriptionsCount() {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT SUM(" + FIELD_TOPIC_ADDED_MESS_COUNT + ") FROM " + TABLE_SUBSCRIPTIONS, null);

        int res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();

        return res;
    }

    public void L(String text) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        String time = DateFormat.getDateTimeInstance().format(new Date());

        row.put(FIELD_TIME, time);
        row.put(FIELD_TEXT, text);

        final long result = db.insert(TABLE_LOG, null, row);

        if (result < 0)
            S.L("DB insert failed. time: " + time + " text:" + text);

    }

    public void ShowL() {
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOG, null);
        while (cursor.moveToNext()) {
            S.L(">> " + cursor.getString(0) + " - " + cursor.getString(1));
        }

    }

    public void ClearL() {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOG);

    }

}
