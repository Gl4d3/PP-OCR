package com.baidu.paddle.fastdeploy.app.ui.view.model;

public class BaseResultModel {
    private int index;
    private String name;
    private float confidence;
    private long elapsedTime;
    private String imagePath;
    private String filteredText;

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
