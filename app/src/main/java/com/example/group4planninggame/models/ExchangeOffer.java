package com.example.group4planninggame.models;

public class ExchangeOffer {
    private String itemName;
    private String userEmail;
    private String itemCondition;
    private String itemCategory;
    private String status;
    private String posterEmail;
    private String otherUserEmail;
    private String offerID;
    private String wantedProduct;
    public ExchangeOffer() {}

    public ExchangeOffer(String itemName, String userEmail, String itemCondition, String itemCategory, String status, String posterEmail, String offerID, String wantedProduct, String otherUserEmail) {
        this.itemName = itemName;
        this.userEmail = userEmail;
        this.itemCondition = itemCondition;
        this.itemCategory = itemCategory;
        this.status = status;
        this.posterEmail = posterEmail;
        this.offerID = offerID;
        this.wantedProduct = wantedProduct;
        this.otherUserEmail = otherUserEmail;
    }

    public String getPosterEmail() {
        return posterEmail;
    }

    public void setPosterEmail(String posterEmail) {
        this.posterEmail = posterEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getOtherUserEmail() {
        return otherUserEmail;
    }
    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getWantedProduct() {
        return wantedProduct;
    }

    public void setWantedProduct(String productName) {
        this.wantedProduct = productName;
    }

    public void setOtherUserEmail(String otherUserEmail) {
        this.otherUserEmail = otherUserEmail;
    }
}
