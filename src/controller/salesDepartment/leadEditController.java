package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: leadEditController.java
//Description: To edit customer information
//First Written on: 28 March 2022
//Edited on: 18 April 2022

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import base.salesDepartment.ArrayLists.Customer;
import JDBC_Connectors.DBConnectors;

import java.sql.*;

import static base.salesDepartment.Method.publicMethod.checkLeadInfo;

public class leadEditController {
    @FXML
    private TextField emailText, nameText, contactText, postcodeText;

    @FXML
    private TextArea locationText;

    @FXML
    private Label errorLabel;

    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    //update record in database
    public void saveCustomerData(){
        try{
            if(checkCustomerInformation()){
                //update database
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("UPDATE customer SET Cust_Contact = ?, Cust_Name = ?, Location = ?, Postcode = ? WHERE Email = ?");
                statement.setString(1, contactText.getText());
                statement.setString(2, nameText.getText());
                statement.setString(3, locationText.getText());
                statement.setString(4, postcodeText.getText());
                statement.setString(5, emailText.getText());
                statement.execute();
                cancel();
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //close window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }
    //set text field with customer data
    public void passLeadInfo(Customer customer){
        emailText.setText(customer.getEmail());
        nameText.setText(customer.getName());
        contactText.setText(customer.getContact());
        locationText.setText(customer.getLocation());
        postcodeText.setText(customer.getPostcode());
    }

    //validate user input
    public boolean checkCustomerInformation(){
        if(nameText.getText().isEmpty() || contactText.getText().isEmpty() ||
           locationText.getText().isEmpty() || postcodeText.getText().isEmpty()){
            errorLabel.setText("You must fill in all required field");
            return false;
        }
        String value[] = checkLeadInfo(emailText.getText(), nameText.getText(), contactText.getText(),
                locationText.getText(), postcodeText.getText());

        if(!Boolean.parseBoolean(value[0])){
            errorLabel.setText(value[1]);
            return false;
        }
        return true;
    }

    //close connection to database
    public void closeConnection(){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
