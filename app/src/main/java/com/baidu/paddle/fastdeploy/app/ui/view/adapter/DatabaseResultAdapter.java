package com.baidu.paddle.fastdeploy.app.ui.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.baidu.paddle.fastdeploy.app.examples.R;
import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel;

import java.util.ArrayList;

public class DatabaseResultAdapter extends ArrayAdapter<BaseResultModel> {

    public DatabaseResultAdapter(@NonNull Context context, ArrayList<BaseResultModel> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseResultModel result = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ocr_database_item, parent, false);
        }

        TextView textViewOriginal = convertView.findViewById(R.id.text_view_original);
        TextView textViewFiltered = convertView.findViewById(R.id.text_view_filtered);
        TextView textViewConfidence = convertView.findViewById(R.id.text_view_confidence);
        TextView textViewProcessingTime = convertView.findViewById(R.id.text_view_processing_time);
        TextView textViewImagePath = convertView.findViewById(R.id.text_view_image_path);

        if (result != null) {
            textViewOriginal.setText("Original: " + result.getName());
            textViewFiltered.setText("Filtered: " + result.getFilteredText());
            textViewConfidence.setText(String.format("Confidence: %.2f", result.getConfidence()));
            textViewProcessingTime.setText(String.format("Processing Time: %d ms", result.getelapsedTime()));
            textViewImagePath.setText("Image: " + result.getImagePath());
        }

        return convertView;
    }
}