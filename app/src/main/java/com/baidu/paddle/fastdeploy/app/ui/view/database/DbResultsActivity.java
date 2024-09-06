package com.baidu.paddle.fastdeploy.app.ui.view.database;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.paddle.fastdeploy.app.examples.R;
import com.baidu.paddle.fastdeploy.app.ui.view.adapter.DatabaseResultAdapter;
import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel;

import java.util.ArrayList;

public class DbResultsActivity extends AppCompatActivity {
    private static final String TAG = "DbResultsActivity";
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private DatabaseResultAdapter adapter;
    private ArrayList<BaseResultModel> ocrResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_database);

        listView = findViewById(R.id.result_list_view);
        if (listView == null) {
            Log.e(TAG, "ListView not found in layout");
            Toast.makeText(this, "Error: ListView not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        ocrResultsList = new ArrayList<>();

        loadOCRResultsFromDatabase();

        adapter = new DatabaseResultAdapter(this, , ocrResultsList);
        listView.setAdapter(adapter);
    }

    private void loadOCRResultsFromDatabase() {
        try (Cursor cursor = databaseHelper.getAllOCRResults()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int index = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
                    @SuppressLint("Range") String filteredText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FILTERED_TEXT));
                    @SuppressLint("Range") float confidence = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONFIDENCE));
                    @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH));
                    @SuppressLint("Range") long processingTime = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROCESSING_TIME));

                    BaseResultModel result = new BaseResultModel(index, name, filteredText, confidence, processingTime, imagePath);
                    ocrResultsList.add(result);
                } while (cursor.moveToNext());
                Log.d(TAG, "Loaded " + ocrResultsList.size() + " OCR results from database");
            } else {
                Log.d(TAG, "No OCR results found in database");
                Toast.makeText(this, "No OCR results found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading OCR results from database", e);
            Toast.makeText(this, "Error loading OCR results", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}