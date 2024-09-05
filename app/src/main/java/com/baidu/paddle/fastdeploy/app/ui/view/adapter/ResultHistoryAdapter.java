package com.baidu.paddle.fastdeploy.app.ui.view.adapter;

import com.baidu.paddle.fastdeploy.app.ui.view.model.BaseResultModel;

import java.util.Date;

public class ResultHistoryAdapter extends BaseResultModel {
    private String imagePath;
    private String ocrResult;
    private Date dateTaken;
    private String category;

    public ResultHistoryAdapter(int index, String name, float confidence, String imagePath, String ocrResult, Date dateTaken, String category) {
        super(index, name, confidence);
        this.imagePath = imagePath;
        this.ocrResult = ocrResult;
        this.dateTaken = dateTaken;
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
