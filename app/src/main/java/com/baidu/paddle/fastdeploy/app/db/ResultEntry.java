package com.baidu.paddle.fastdeploy.app.db;

import android.graphics.Bitmap;

public class ResultEntry {
    private Bitmap croppedImage;
    private long elapsedTime;
    private String ocrResults;
    private Bitmap visualization;
    private String boundingBoxes;
    private String confidences;

    public String getCroppedImage() {
        return croppedImage.toString();
    }

    public void setCroppedImage(Bitmap croppedImage) {
        this.croppedImage = croppedImage;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getOcrResults() {
        return ocrResults;
    }

    public void setOcrResults(String ocrResults) {
        this.ocrResults = ocrResults;
    }

    public Bitmap getVisualization() {
        return visualization;
    }

    public void setVisualization(Bitmap visualization) {
        this.visualization = visualization;
    }

    public void setBoundingBoxes(){
        this.boundingBoxes = boundingBoxes;
    }

    public void setConfidences(){
        this.confidences = confidences;
    }

    public String getBoundingBoxes(){
        return boundingBoxes;
    }

    public String getConfidences(){
        return confidences;
    }


}
