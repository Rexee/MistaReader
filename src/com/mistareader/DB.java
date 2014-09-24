package com.mistareader;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mistareader.TextProcessors.S;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME                = "main.db";
    private static final int    DB_VERSION             = 1;

    private static final String TABLE_VIEW_HISTORY     = "topicviewhistory";
    private static final String TABLE_SUBSCRIPTIONS    = "topicsubscriptions";

    private static final String FIELD_TOPICID          = "id";
    private static final String FIELD_OLD_MESS_COUNT   = "answ";
    private static final String FIELD_ADDED_MESS_COUNT = "addedmess";
    private static final String FIELD_MESSAGE          = "message";
    private static final String FIELD_TIMESTAMP        = "timestamp";

    DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryString = String.format("CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY," + "%s INTEGER," + "%s INTEGER)", TABLE_VIEW_HISTORY, FIELD_TOPICID,
                FIELD_MESSAGE, FIELD_TIMESTAMP);

        db.execSQL(queryString);

        queryString = String.format("CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY," + "%s INTEGER," + "%s INTEGER)", TABLE_SUBSCRIPTIONS, FIELD_TOPICID,
                FIELD_OLD_MESS_COUNT, FIELD_ADDED_MESS_COUNT);

        db.execSQL(queryString);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        S.L("DB. onUpgrade: oldVersion:" + oldVersion + " newVersion:" + newVersion);

        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEW_HISTORY);
            onCreate(db);
        }

    }

    public void addLastPositionToMessage(long currentTopicId, int messageID) {

        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_TOPICID, currentTopicId);
        row.put(FIELD_MESSAGE, messageID);
        row.put(FIELD_TIMESTAMP, System.currentTimeMillis());

        final long result = db.replace(TABLE_VIEW_HISTORY, null, row);

        if (result < 0) {
            S.L("DB insert failed. topicID: " + currentTopicId + " messageID:" + messageID);
        }
        else
            S.L("addLastPositionToMessage " + currentTopicId + " " + messageID);

    }

    public int getLastPositionForMessage(long id) {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT MESSAGE FROM " + TABLE_VIEW_HISTORY + " WHERE " + FIELD_TOPICID + " = ? LIMIT 1",
                new String[] { Long.toString(id) });

        int res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }

        cursor.close();

        S.L("getLastPositionForMessage " + id + " " + res);

        return res;

    }

    // *****************************SUBSCRIPTIONS****************************
    public void addTopicToSubscriptions(long currentTopicId, int answ) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_TOPICID, currentTopicId);
        row.put(FIELD_OLD_MESS_COUNT, answ);

        final long result = db.replace(TABLE_SUBSCRIPTIONS, null, row);

        if (result < 0) {
            S.L("DB insert failed. topicID: " + currentTopicId);
        }
        else
            S.L("addTopicToSubscriptions " + currentTopicId);

    }

    public void updateTopicInSubscriptions(long currentTopicId, int answ) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_ADDED_MESS_COUNT, answ);
        
        final long result = db.update(TABLE_SUBSCRIPTIONS, row, FIELD_TOPICID + " = " + currentTopicId, null);

        if (result < 0) {
            S.L("DB update failed. topicID: " + currentTopicId);
        }
        else
            S.L("updateTopicInSubscriptions " + currentTopicId);

    }
    
    public ArrayList<Subscriptions_Service.subscription> getSubscriptions() {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT " + FIELD_TOPICID + ", " + FIELD_OLD_MESS_COUNT +" FROM " + TABLE_SUBSCRIPTIONS, null);

        ArrayList<Subscriptions_Service.subscription> res = new ArrayList<Subscriptions_Service.subscription>();

        Subscriptions_Service.subscription newSub;
        while (cursor.moveToNext()) {
            
            newSub = new Subscriptions_Service.subscription();
            
            newSub.topicId = cursor.getLong(0);
            newSub.answ = cursor.getInt(1);
            newSub.newMessages = 0;
            
            res.add(newSub);
        }

        cursor.close();

        S.L("getSubscriptions " + res.size());

        return res;

    }

    public boolean isTopicInSubscriptions(long id) {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_SUBSCRIPTIONS + " WHERE " + FIELD_TOPICID + " = ? LIMIT 1",
                new String[] { Long.toString(id) });

        boolean res = false;
        if (cursor.getCount() > 0) {
            res = true;
        }
        cursor.close();

        S.L("isTopicInSubscriptions " + res);

        return res;
    }

    public void removeTopicFromSubscriptions(long id) {

        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIPTIONS, FIELD_TOPICID + "=?", new String[] { String.valueOf(id) });

    }

    public void removeAllSubscriptions() {

        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SUBSCRIPTIONS);

    }
    
    public void printAllSubscriptions() {

        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIPTIONS, null);
        while (cursor.moveToNext()) {
            S.L(">> "+cursor.getInt(0) + "==" + cursor.getInt(1)+"=="+cursor.getInt(2));
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

        S.L("getTotalSubscriptionsCount " + res);

        return res;
    }

}
