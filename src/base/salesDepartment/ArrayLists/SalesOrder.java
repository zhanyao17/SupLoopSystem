package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: SalesOrder.java
//Description: Sales Order Class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class SalesOrder {
    private String invoiceID;
    private String orderID;
    private String itemCode;
    private String itemName;
    private int orderQuantity;
    private String orderDate;
    private String shippingDate;
    private String shippingAddress;
    private String postcode;
    private String shippingStatus;
    private String status;
    private float subTotal;
    private float totalCost;

    public SalesOrder(String orderID, String itemCode, String itemName, int orderQuantity) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.orderQuantity = orderQuantity;
    }

    public SalesOrder(String invoiceID, String orderDate, String shippingDate, String shippingAddress, float totalCost) {
        this.invoiceID = invoiceID;
        this.orderDate = orderDate;
        this.shippingDate = shippingDate;
        this.shippingAddress = shippingAddress;
        this.totalCost = totalCost;
    }

    public SalesOrder(String invoiceID, String orderID, String itemCode, int orderQuantity, String orderDate, String shippingDate, String shippingAddress, String postcode, String shippingStatus, String status) {
        this.invoiceID = invoiceID;
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.orderQuantity = orderQuantity;
        this.orderDate = orderDate;
        this.shippingDate = shippingDate;
        this.shippingAddress = shippingAddress;
        this.postcode = postcode;
        this.shippingStatus = shippingStatus;
        this.status = status;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public String getStatus() {
        return status;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String getItemName() {
        return itemName;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }
}
