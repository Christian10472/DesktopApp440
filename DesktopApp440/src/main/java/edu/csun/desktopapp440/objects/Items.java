package edu.csun.desktopapp440.objects;

import java.sql.Date;

public class Items {

    private int itemId;
    private String username, title, category, description;
    private double price;
    private Date datePosted;
    public Items(Integer itemId, String username, String title, String category, String description, Double price,Date datePosted){
        this.itemId = itemId;
        this.username = username;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.datePosted = datePosted;

    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }
}
