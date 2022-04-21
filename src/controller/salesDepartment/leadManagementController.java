package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: leadManagementController.java
//Description: To manage all the lead information in database
//First Written on: 28 March 2022
//Edited on: 18 April 2022

import controller.loginPage.loginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import base.salesDepartment.ArrayLists.Customer;
import controller.Main;
import JDBC_Connectors.DBConnectors;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.ResourceBundle;

import static base.salesDepartment.Method.publicMethod.checkLeadInfo;

public class leadManagementController extends dashboardController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> nameCol;

    @FXML
    private TableColumn<Customer, String> emailCol;

    @FXML
    private TableColumn<Customer, String> contactCol;

    @FXML
    private TableColumn<Customer, String> locationCol;

    @FXML
    private TableColumn<Customer, String> postcodeCol;

    @FXML
    private TableColumn<Customer, String> leadStatusCol;

    @FXML
    private TextField searchCustomer, emailText, nameText, contactText, postcodeText;

    @FXML
    private TextArea locationText;

    @FXML
    private CheckBox showAllCheckBox;

    @FXML
    private Label usernameLabel;

    private String filterStatus = "'Active'";
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    Customer customerSelected = null;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    public static boolean inCustomerEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        nameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
        contactCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("contact"));
        locationCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("location"));
        postcodeCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("postcode"));
        try{
            leadStatusCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("status"));
            usernameLabel.setText(loginController.employeeName);
        }catch (Exception e){

        }
        loadTableData();
    }

    //get check box value
    public void getCheckBox(){
        if(showAllCheckBox.isSelected()){
            filterStatus = "'Active', 'Closed'";
        }else{
            filterStatus = "'Active'";
        }
        loadTableData();
    }
    //get selected row in table
    public void getTableClick(){
        customerSelected = customerTable.getSelectionModel().getSelectedItem();
    }

    //load data into table
    public void loadTableData() {
        customerList.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("select * from customer WHERE Cust_status IN ("+filterStatus+")");
            resultSet = statement.executeQuery();

            while (resultSet.next()){
                //Create string
                String customerData = resultSet.getString("Cust_Name")     + "\t" +
                                      resultSet.getString("Email")         + "\t" +
                                      resultSet.getString("Cust_Contact")  + "\t" +
                                      resultSet.getString("Location")      + "\t" +
                                      resultSet.getString("Postcode")      + "\t" +
                                      resultSet.getString("Cust_status");

                //filter all the record that does not contain the word in search bar
                if(customerData.toLowerCase(Locale.ROOT).contains(searchCustomer.getText().toLowerCase(Locale.ROOT))){
                    Customer customer = new Customer(resultSet.getString("Cust_Name"),
                            resultSet.getString("Email"),
                            resultSet.getString("Cust_Contact"),
                            resultSet.getString("Location"),
                            resultSet.getString("Postcode"),
                            resultSet.getString("Cust_status"));

                    customerList.add(customer);
                }
            }
            customerTable.setItems(customerList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //add new customer record
    public void addNewCustomer(){
        try {
            if(checkCustomerInformation()){
                //retrieve data in database
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT * FROM customer WHERE Email = '"+ emailText.getText()+"'");
                resultSet = statement.executeQuery();
                if(resultSet.next()){
                    //if the customer already existed but deleted before
                    if(resultSet.getString("Cust_status").equals("Closed")){
                        statement = connection.prepareStatement("UPDATE customer SET Cust_Contact = ?, Cust_Name = ?," +
                                                                    " Location = ?, Postcode = ?," +
                                                                    " Cust_status = ? WHERE Email = ?");
                        statement.setString(1, contactText.getText().trim());
                        statement.setString(2, nameText.getText().trim());
                        statement.setString(3, locationText.getText().trim());
                        statement.setString(4, postcodeText.getText().trim());
                        statement.setString(5, "Active");
                        statement.setString(6, emailText.getText().trim());
                        statement.execute();
                    }else{ //if the customer already existed and not deleted
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Existed Customer");
                        alert.setHeaderText("Customer is existed in the database!");
                        alert.show();
                        return;
                    }
                }else{
                    //if the customer does not existed then insert data into database
                    statement = connection.prepareStatement("INSERT INTO customer VALUES (?,?,?,?,?,?)");
                    statement.setString(1, emailText.getText().trim());
                    statement.setString(2, contactText.getText().trim());
                    statement.setString(3, nameText.getText().trim());
                    statement.setString(4, locationText.getText().trim());
                    statement.setString(5, postcodeText.getText().trim());
                    statement.setString(6, "Active");
                    statement.execute();
                }


                clearField();
                loadTableData();
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
           closeConnection();
        }
    }

    //delete customer record in database
    public void deleteCustomer(){
        if(customerSelected != null){
            if(customerSelected.getStatus().equals("Active")){
                try{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Customer");
                    alert.setHeaderText("You're going to delete a customer record");
                    alert.setContentText("Are you sure?");

                    if(alert.showAndWait().get() != ButtonType.OK){
                        return;
                    }
                    //update customer status to "Closed"
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("UPDATE customer SET Cust_status = 'Closed' WHERE Email ='" + customerSelected.getEmail() +"'");
                    statement.execute();
                    loadTableData();
                    customerSelected =null;

                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Delete Customer");
                    alert2.setHeaderText("Customer record is deleted");
                    alert2.show();

                } catch (SQLException e){
                    e.printStackTrace();

                }finally {
                    closeConnection();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Customer Information");
                alert.setHeaderText("This customer's status has been closed!");
                alert.setContentText("You cannot delete this customer again");
                alert.show();
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete Customer Information");
            alert.setHeaderText("Please select a customer from the table!");
            alert.show();
        }
    }

    //pop out new window to edit customer details
    public void editCustomer(){
        if(inCustomerEditMode){ //avoid user from opening multiple edit window
            return;
        }
        if(customerSelected != null){
            //avoid user from editing a deleted record
            if(customerSelected.getStatus().equals("Closed")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Edit");
                alert.setHeaderText("This customer has been deleted!");
                alert.show();
                return;
            }
            try {
                //get resource from FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/leadEdit.fxml"));
                Parent customerEditRoot = loader.load();
                //create instance of CustomerEditController to access the method
                leadEditController leadEditController = loader.getController();
                inCustomerEditMode = true;

                Stage stageEdit = new Stage();
                Scene sceneEdit = new Scene(customerEditRoot);
                stageEdit.setScene(sceneEdit);
                stageEdit.setResizable(false);
                stageEdit.setAlwaysOnTop(true);
                //pass selected customer row in table
                leadEditController.passLeadInfo(customerSelected);
                stageEdit.showAndWait();
                inCustomerEditMode = false;
                loadTableData();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edit Customer Information");
            alert.setHeaderText("Please select a customer from the table!");
            alert.show();
        }
    }

    //switch to other view
    public void viewLeadOrder(){
         try{
             if(customerSelected !=null){
                 leadOrderController.passLead = customerSelected;
                 new Main().switchScene("/fxml/salesDepartment/leadOrder.fxml");
             }else{
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setHeaderText("No customer selected");
                 alert.setContentText("Please select a lead from the table");
                 alert.show();
             }
         }catch (Exception e) {
             e.printStackTrace();
         }
    }

    //reset all text field
    public void clearField(){
        emailText.setText(null);
        nameText.setText(null);
        contactText.setText(null);
        locationText.setText(null);
        postcodeText.setText(null);
    }

    //validate customer details
    public boolean checkCustomerInformation(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(nameText.getText().isEmpty() || contactText.getText().isEmpty() || emailText.getText().isEmpty() ||
           locationText.getText().isEmpty() || postcodeText.getText().isEmpty()){
            alert.setHeaderText("You must fill in all required field");
            alert.show();
            return false;
        }
        String value[] = checkLeadInfo(emailText.getText(), nameText.getText(), contactText.getText(),
                                       locationText.getText(), postcodeText.getText());

        if(!Boolean.parseBoolean(value[0])){
            alert.setHeaderText(value[1]);
            alert.show();
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
