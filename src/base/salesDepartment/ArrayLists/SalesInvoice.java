package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: SalesInvoice.java
//Description: Sales Invoice class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import javafx.scene.control.ProgressBar;

public class SalesInvoice {
    private String invoiceID;
    private String orderDate;
    private String shippingDate;
    private String customerEmail;
    private String customerName;
    private String status;
    private float totalCost;
    private String progressPercent;
    private ProgressBar progress;

    public SalesInvoice(String invoiceID, String shippingDate, String progressPercent, ProgressBar progress) {
        this.invoiceID = invoiceID;
        this.shippingDate = shippingDate;
        this.progressPercent = progressPercent;
        this.progress = progress;
    }

    public SalesInvoice(String invoiceID, String orderDate, String customerEmail, String customerName, String status, float totalCost, String progressPercent) {
        this.invoiceID = invoiceID;
        this.orderDate = orderDate;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.status = status;
        this.totalCost = totalCost;
        this.progressPercent = progressPercent;
        this.progress = new ProgressBar();
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public String getProgressPercent() {
        return progressPercent;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public void setProgress(ProgressBar progress) {
        this.progress = progress;
    }

    public void setProgressPercent(String progressPercent) {
        this.progressPercent = progressPercent;
    }
}
