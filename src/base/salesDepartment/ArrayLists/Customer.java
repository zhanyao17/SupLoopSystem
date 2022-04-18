package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: Customer.java
//Description: Customer Class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class Customer {
    private String name;
    private String email;
    private String contact;
    private String location;
    private String postcode;
    private String status;

    public Customer(String name, String email, String contact, String location, String postcode, String status) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.location = location;
        this.postcode = postcode;
        this.status = status;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getLocation() {
        return location;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
