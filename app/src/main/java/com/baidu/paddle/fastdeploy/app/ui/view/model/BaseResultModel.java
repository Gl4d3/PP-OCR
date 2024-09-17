package com.baidu.paddle.fastdeploy.app.ui.view.model;

public class BaseResultModel {
    private int index; // Index of Result
    private String name; // Original Extracted Test
    private float confidence; // Model Confidence
    private long elapsedTime; // Processing Time
    private String imagePath; // Image Path for Visualization
    private String filteredText; // Text that does not contain 0-9 and .

    public BaseResultModel() {

    }

    public BaseResultModel(int index, String name, String filteredText, float confidence, long elapsedTime, String imagePath) {
        this.index = index;
        this.name = name;
        this.confidence = confidence;
        this.filteredText = filteredText;
        this.elapsedTime = elapsedTime;
        this.imagePath = imagePath;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilteredText() {
        return filteredText;
    }

    public void setFilteredText(String filteredText) {
        this.filteredText = filteredText;
    }

    public float getelapsedTime() {  return elapsedTime;   }

    public void setElapsedTime(long elapsedTime){ this.elapsedTime = elapsedTime; }

    public String getImagePath() {  return imagePath;   }

    public void setImagePath(String imagePath){ this.imagePath = imagePath; }
}
