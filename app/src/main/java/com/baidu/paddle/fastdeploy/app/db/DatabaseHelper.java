package com.baidu.paddle.fastdeploy.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ocr_results.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE results (_id INTEGER PRIMARY KEY, cropped_image BLOB, elapsed_time INTEGER, ocr_results TEXT, visualization BLOB, bounding_boxes TEXT, confidences TEXT, timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS results");
        onCreate(db);
    }

    public void insertResult(Bitmap croppedImage, long elapsedTime, String[] ocrResults, int[][] boundingBoxes, float[] confidences) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("cropped_image", bitmapToByteArray(croppedImage));
        contentValues.put("elapsed_time", elapsedTime);
        contentValues.put("ocr_results", TextUtils.join(",", ocrResults));
        contentValues.put("bounding_boxes", boundingBoxesToString(boundingBoxes));
        contentValues.put("confidences", confidencesToString(confidences));
        contentValues.put("timestamp", System.currentTimeMillis());

        db.insert("results", null, contentValues);
        db.close();
    }

    private String boundingBoxesToString(int[][] boundingBoxes) {
        StringBuilder sb = new StringBuilder();
        for (int[] box : boundingBoxes) {
            sb.append(box[0]).append(",").append(box[1]).append(",").append(box[2]).append(",").append(box[3]).append(";");
        }
        return sb.toString();
    }

    private String confidencesToString(float[] confidences) {
        StringBuilder sb = new StringBuilder();
        for (float confidence : confidences) {
            sb.append(confidence).append(",");
        }
        return sb.toString();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
