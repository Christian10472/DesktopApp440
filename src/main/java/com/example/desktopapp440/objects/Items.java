package com.example.desktopapp440.objects;

public class Items {

      private String title,
             category,
             description;
      private double price;

     public Items(String title, String category, String description, double price) {
          this.title = title;
          this.category = category;
          this.description = description;
          this.price = price;
     }

     public Items() {

     }

     public String getTitle() {
          return title;
     }

     public String getCategory() {
          return category;
     }

     public String getDescription() {
          return description;
     }

     public double getPrice() {
          return price;
     }

     public void setTitle(String title) {
          this.title = title;
     }

     public void setCategory(String category) {
          this.category = category;
     }

     public void setDescription(String description) {
          this.description = description;
     }

     public void setPrice(double price) { this.price = price; }
}
