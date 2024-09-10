package com.baidu.paddle.fastdeploy.app.ui.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.baidu.paddle.fastdeploy.app.examples.R;
import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel;

import java.io.File;
import java.util.ArrayList;

public class DatabaseResultAdapter extends ArrayAdapter<BaseResultModel> {
    private Context context;

    public DatabaseResultAdapter(@NonNull Context context, ArrayList<BaseResultModel> results) {
        super(context, 0, results);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseResultModel result = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ocr_database_item, parent, false);
        }

        ImageView imageViewResult = convertView.findViewById(R.id.image_view_result);
        TextView textViewOriginal = convertView.findViewById(R.id.text_view_original);
        TextView textViewFiltered = convertView.findViewById(R.id.text_view_filtered);
        TextView textViewConfidence = convertView.findViewById(R.id.text_view_confidence);
        TextView textViewProcessingTime = convertView.findViewById(R.id.text_view_processing_time);
        TextView textViewImagePath = convertView.findViewById(R.id.text_view_image_path);

        if (result != null) {
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
            textViewProcessingTime.setText(String.format("Processing Time: %d ms", result.getelapsedTime()));
            textViewImagePath.setText("Image: " + result.getImagePath());
        }

        return convertView;
    }
}