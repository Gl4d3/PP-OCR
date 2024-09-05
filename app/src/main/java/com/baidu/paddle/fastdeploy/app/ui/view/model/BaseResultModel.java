package com.baidu.paddle.fastdeploy.app.ui.view.model;

public class BaseResultModel {
    private int index;
    private String name;
    private String filteredText;
    private float confidence;

    public BaseResultModel() {

    }

    public BaseResultModel(int index, String name, String filteredText, float confidence) {
        this.index = index;
        this.name = name;
        this.confidence = confidence;
        this.filteredText = filteredText;
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
}
