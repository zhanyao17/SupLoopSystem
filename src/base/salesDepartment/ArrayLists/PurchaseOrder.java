package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: PurchaseOrder.java
//Description: Purchase order class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import javafx.scene.control.ComboBox;

public class PurchaseOrder {
    private String rmID;
    private String rmName;
    private float rmCost;
    private float quantityRequired=0;
    private float stock=0;
    private float purchaseQuantity =0;
    private float subTotal=0;
    private ComboBox supplierName;

    public PurchaseOrder(String rmID, String rmName, float rmCost) {
        this.rmID = rmID;
        this.rmName = rmName;
        this.rmCost = rmCost;
        this.supplierName = new ComboBox();
    }

    public String getRmID() {
        return rmID;
    }

    public String getRmName() {
        return rmName;
    }

    public float getRmCost() {
        return rmCost;
    }

    public float getQuantityRequired() {
        return quantityRequired;
    }

    public float getStock() {
        return stock;
    }

    public float getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public ComboBox getSupplierName() {
        return supplierName;
    }

    public void setRmID(String rmID) {
        this.rmID = rmID;
    }

    public void setRmName(String rmName) {
        this.rmName = rmName;
    }

    public void setRmCost(float rmCost) {
        this.rmCost = rmCost;
    }

    public void setQuantityRequired(float quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public void setPurchaseQuantity(float purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public void setSupplierName(ComboBox supplierName) {
        this.supplierName = supplierName;
    }
}
