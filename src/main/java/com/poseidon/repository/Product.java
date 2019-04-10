package com.poseidon.repository;

public class Product {

    private String levelOne;
    private String levelTwo;
    private String levelThree;
    private String partNo;
    private String description;
    private String uom;
    private String mtmlUom;
    private String explanation;
    private String picture;
    private String information;


    public Product() {
    }

    public Product(String levelOne, String levelTwo, String levelThree, String partNo, String description, String uom, String mtmlUom, String explanation, String picture, String information) {
        this.levelOne = levelOne;
        this.levelTwo = levelTwo;
        this.levelThree = levelThree;
        this.partNo = partNo;
        this.description = description;
        this.uom = uom;
        this.mtmlUom = mtmlUom;
        this.explanation = explanation;
        this.picture = picture;
        this.information = information;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(String levelTwo) {
        this.levelTwo = levelTwo;
    }

    public String getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(String levelThree) {
        this.levelThree = levelThree;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getMtmlUom() {
        return mtmlUom;
    }

    public void setMtmlUom(String mtmlUom) {
        this.mtmlUom = mtmlUom;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}


