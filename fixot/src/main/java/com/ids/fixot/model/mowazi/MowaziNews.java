package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziNews {

    private int id,companyId;
    private String content,title,date,onHomePage,isActive,titleEn,PictureName;

    public MowaziNews() {
    }

    public String getPictureName() {
        return PictureName;
    }

    public void setPictureName(String pictureName) {
        PictureName = pictureName;
    }

    public void setOnHomePage(String onHomePage) {
        this.onHomePage = onHomePage;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String news) {
        this.content = news;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getOnHomePage() {
        return onHomePage;
    }
}

