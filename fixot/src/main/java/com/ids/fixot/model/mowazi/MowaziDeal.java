package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/28/2018.
 */


public class MowaziDeal {

    private int dealId;
    private int quantity;
    private int buyOrderId;
    private int sellOrderId;
    private String dealDate;
    private int price;
    private int companyId;
    private int sellerId;
    private int buyerId;
    private String company;

    public String getCompany() {
        return company;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getPrice() {
        return price;
    }

    public String getDealDate() {
        return dealDate;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public int getBuyOrderId() {
        return buyOrderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDealId() {
        return dealId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDealDate(String dealDate) {
        this.dealDate = dealDate;
    }

    public void setSellOrderId(int sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public void setBuyOrderId(int buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDealId(int dealId) {
        this.dealId = dealId;
    }
}
