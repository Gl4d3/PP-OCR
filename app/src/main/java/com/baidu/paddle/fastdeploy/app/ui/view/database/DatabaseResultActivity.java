package com.baidu.paddle.fastdeploy.app.ui.view.database;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.paddle.fastdeploy.app.examples.R;
import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel;

import java.io.File;
import java.util.ArrayList;

public class DatabaseResultActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_database);

        databaseHelper = new DatabaseHelper(this);
        linearLayout = findViewById(R.id.linear_layout); // Add a LinearLayout to your ocr_database.xml

        ArrayList<BaseResultModel> results = databaseHelper.getResults();
        for (BaseResultModel result : results) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.ocr_database_item, null);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView imageViewResult = itemView.findViewById(R.id.image_view_result);
            TextView textViewOriginal = itemView.findViewById(R.id.text_view_original);
            TextView textViewFiltered = itemView.findViewById(R.id.text_view_filtered);
            TextView textViewConfidence = itemView.findViewById(R.id.text_view_confidence);
            TextView textViewProcessingTime = itemView.findViewById(R.id.text_view_processing_time);
            TextView textViewImagePath = itemView.findViewById(R.id.text_view_image_path);

            // Set the image source using the image path from the BaseResultModel
            File imageFile = new File(result.getImagePath());
            if (imageFile.exists()) {
                imageViewResult.setImageURI(Uri.fromFile(imageFile));
            } else {
                // Use a placeholder image if the file doesn't exist
                imageViewResult.setImageResource(R.drawable.placeholder_image);
            }

            textViewOriginal.setText("Original: " + result.getName());
            textViewFiltered.setText("Filtered: " + result.getFilteredText());
            textViewConfidence.setText(String.format("Confidence: %.2f", result.getConfidence()));
            textViewProcessingTime.setText(String.format("Processing Time: %.2f ms", result.getelapsedTime()));
            textViewImagePath.setText("Image: " + result.getImagePath());

            linearLayout.addView(itemView);
        }
    }
}
