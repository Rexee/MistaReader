package com.mistareader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mistareader.TextProcessors.S;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME         = "main.db";
    private static final String TABLE           = "topicviewhistory";
    private static final int    DB_VERSION      = 1;

    private static final String FIELD_TOPICID   = "id";
    private static final String FIELD_MESSAGE   = "message";
    private static final String FIELD_TIMESTAMP = "timestamp";

    DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String queryString = String.format("CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY," + "%s INTEGER," + "%s INTEGER)",
                TABLE, FIELD_TOPICID, FIELD_MESSAGE, FIELD_TIMESTAMP);

        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        S.L("DB. onUpgrade: oldVersion:" + oldVersion + " newVersion:" + newVersion);

        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }

    }

    public void addBookmarkMessage(long currentTopicId, int messageID) {

        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_TOPICID, currentTopicId);
        row.put(FIELD_MESSAGE, messageID);
        row.put(FIELD_TIMESTAMP, System.currentTimeMillis());

        final long result = db.replace(TABLE, null, row);

        if (result < 0) {
            S.L("DB insert failed. topicID: " + currentTopicId + " messageID:" + messageID);
        }
        else
            S.L("addBookmarkMessage "+currentTopicId +" "+messageID);

    }

    public int getBookmarkMessage(long id) {

        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT MESSAGE FROM " + TABLE + " WHERE " + FIELD_TOPICID + " = ? LIMIT 1", new String[] { Long.toString(id) });

        int res = 0;
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }

        cursor.close();

        S.L("getBookmarkMessage "+id+ " " + res);

        return res;

    }

}
