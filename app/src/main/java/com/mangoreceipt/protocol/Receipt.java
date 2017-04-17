package com.mangoreceipt.protocol;

import io.realm.RealmObject;

/**
 * Created by hyungsoklee on 2017. 4. 8..
 */

public class Receipt extends RealmObject {
    private String date;
    private int price;
    private int weekNumber;
    private String receiptImageUrl;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getReceiptImageUrl() {
        return receiptImageUrl;
    }

    public void setReceiptImageUrl(String receiptImageUrl) {
        this.receiptImageUrl = receiptImageUrl;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "date='" + date + '\'' +
                ", price=" + price +
                ", weekNumber=" + weekNumber +
                ", receiptImageUrl='" + receiptImageUrl + '\'' +
                '}';
    }
}
