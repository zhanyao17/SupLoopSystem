package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: Item.java
//Description: Item class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import javafx.scene.image.ImageView;

public class Item {
    private String itemCode;
    private String itemName;
    private float itemPrice;
    private int amount;
    private ImageView itemImage;
    private String status;

    public Item(String itemCode, String itemName, float itemPrice, String status) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemImage = new ImageView();
        this.status = status;
    }

    public ImageView getItemImage() {
        return itemImage;
    }

    public String getStatus() {
        return status;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setItemImage(ImageView itemImage) {
        this.itemImage = itemImage;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
