package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: PurchaseInvoice.java
//Description: Purchase Invoice Class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class PurchaseInvoice {
    private String invoiceID;
    private String orderID;
    private String rmID;
    private String supplierName;
    private String rmName;
    private float purchaseQuantity;
    private String purchaseDate;
    private float totalCost;

    public PurchaseInvoice(String invoiceID, String orderID, String supplierName, String rmName, float purchaseQuantity, String purchaseDate, float totalCost) {
        this.invoiceID = invoiceID;
        this.orderID = orderID;
        this.supplierName = supplierName;
        this.rmName = rmName;
        this.purchaseQuantity = purchaseQuantity;
        this.purchaseDate = purchaseDate;
        this.totalCost = totalCost;
    }

    public PurchaseInvoice(String invoiceID, String rmID, String rmName, float purchaseQuantity, String purchaseDate, float totalCost) {
        this.invoiceID = invoiceID;
        this.rmID = rmID;
        this.rmName = rmName;
        this.purchaseQuantity = purchaseQuantity;
        this.purchaseDate = purchaseDate;
        this.totalCost = totalCost;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getRmID() {
        return rmID;
    }

    public String getRmName() {
        return rmName;
    }

    public float getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setRmID(String rmID) {
        this.rmID = rmID;
    }

    public void setRmName(String rmName) {
        this.rmName = rmName;
    }

    public void setPurchaseQuantity(float purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
