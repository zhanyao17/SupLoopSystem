package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: Supplier.java
//Description: Supplier class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class Supplier {
    private String supplierID;
    private String supplierName;
    private String supplierEmail;
    private String supplierContact;
    private String status;

    public Supplier(String supplierID, String supplierName) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
    }

    public Supplier(String supplierID, String supplierName, String supplierEmail, String supplierContact, String status) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierContact = supplierContact;
        this.status = status;
    }

    public Supplier(String supplierID, String supplierName, String supplierEmail, String supplierContact) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierContact = supplierContact;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public String getStatus() {
        return status;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
