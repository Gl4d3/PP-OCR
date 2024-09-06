package com.baidu.paddle.fastdeploy.app.ui.view.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "ocr_results.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_OCR_RESULTS = "results";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_FILTERED_TEXT = "filtered_text";
    public static final String COLUMN_CONFIDENCE = "confidence";
    public static final String COLUMN_PROCESSING_TIME = "processing_time";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_OCR_RESULTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TEXT + " TEXT,"
                + COLUMN_FILTERED_TEXT + " TEXT,"
                + COLUMN_CONFIDENCE + " REAL,"
                + COLUMN_PROCESSING_TIME + " INTEGER,"
                + COLUMN_IMAGE_PATH + " TEXT)";  // Fixed: Added TEXT type for IMAGE_PATH
        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "Database created with table: " + TABLE_OCR_RESULTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCR_RESULTS);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    public long insertOCRResult(String text, String filteredText, float confidence, String imagePath, long processingTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, text);
        values.put(COLUMN_FILTERED_TEXT, filteredText);
        values.put(COLUMN_CONFIDENCE, confidence);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_PROCESSING_TIME, processingTime);

        long newRowId = db.insert(TABLE_OCR_RESULTS, null, values);
        Log.d(TAG, "Inserted new OCR result with ID: " + newRowId);
        return newRowId;
    }

    public Cursor getAllOCRResults() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OCR_RESULTS, null, null, null, null, null, null);
        Log.d(TAG, "Retrieved " + cursor.getCount() + " OCR results from database");
        return cursor;
    }
}