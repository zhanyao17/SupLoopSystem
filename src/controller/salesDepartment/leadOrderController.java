package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: leadOrderController.java
//Description: To view all the historical order from a lead
//First Written on: 29 March 2022
//Edited on: 18 April 2022

import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.Customer;
import base.salesDepartment.ArrayLists.SalesOrder;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class leadOrderController extends dashboardController implements Initializable {
    @FXML
    private TableView<SalesOrder> leadOrderTable;

    @FXML
    private TableColumn<SalesOrder, String> invoiceIDCol;

    @FXML
    private TableColumn<SalesOrder, String> orderDateCol;

    @FXML
    private TableColumn<SalesOrder, String> shippingAddressCol;

    @FXML
    private TableColumn<SalesOrder, String> shippingDateCol;

    @FXML
    private TableColumn<SalesOrder, Float> totalCostCol;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField searchText, leadNameText;

    public static Customer passLead = null;

    private SalesOrder orderSelected = null;

    private ObservableList<SalesOrder> orderList = FXCollections.observableArrayList();

    private SalesOrder temp = null;

    private Date lastClickTime = new Date();

    private Boolean inViewMode = false;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        invoiceIDCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("invoiceID"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("orderDate"));
        shippingDateCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("shippingDate"));
        shippingAddressCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("shippingAddress"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, Float>("totalCost"));
        usernameLabel.setText(loginController.employeeName);
        leadNameText.setText(passLead.getName());
        loadTableData();
    }
    //get selected row in table
    public void getTableClick(){
        orderSelected = leadOrderTable.getSelectionModel().getSelectedItem();
        if(orderSelected == null){
            return;
        }
        //check double click
        if(orderSelected != temp){
            temp = orderSelected;
        }else if(orderSelected == temp){
            Date now = new Date();
            //call viewDetails() if period between the double click is less than 1000 milli second
            long period = now.getTime()  - lastClickTime.getTime();
            if(period < 1000){
                viewDetails();
            }else{
                lastClickTime = new Date();
            }
        }
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

    //load data into table
    public void loadTableData(){
        try{
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT DISTINCT si.Sales_Invoice_ID, si.Order_total_price, " +
                                                        "so.Order_date, so.Due_date, so.Shipping_address " +
                                                        "FROM sales_order so INNER JOIN sales_invoice si " +
                                                        "ON so.Sales_Invoice_ID = si.Sales_Invoice_ID "+
                                                        "WHERE si.Email = '"+ passLead.getEmail()+"'");
            resultSet = statement.executeQuery();
            orderList.clear();
            while (resultSet.next()){
                //Create line of string for each record
                String leadOrderData = resultSet.getString("Sales_Invoice_ID") + "\t" +
                        resultSet.getString("Order_date")+ "\t" +
                        resultSet.getString("Due_date")+ "\t" +
                        resultSet.getString("Shipping_address")+ "\t" +
                        resultSet.getFloat("Order_total_price");

                //filter all the record that does not contains the words in search bar
                if(leadOrderData.toLowerCase(Locale.ROOT).contains(searchText.getText().toLowerCase(Locale.ROOT))){
                    SalesOrder salesOrder = new SalesOrder (resultSet.getString("Sales_Invoice_ID"),
                            resultSet.getString("Order_date"),
                            resultSet.getString("Due_date"),
                            resultSet.getString("Shipping_address"),
                            resultSet.getFloat("Order_total_price"));
                    orderList.add(salesOrder);
                }
            }
            leadOrderTable.setItems(orderList);
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    //pop out new window to show the details of invoice
    public void viewDetails(){
        if(orderSelected != null){
            if(!inViewMode){
                try {
                    viewLeadOrderDetailsController.passOrder = orderSelected;
                    //get resource from FXML file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/viewLeadOrderDetails.fxml"));
                    Parent root = loader.load();
                    inViewMode = true;
                    //setup window
                    Stage stageEdit = new Stage();
                    Scene sceneEdit = new Scene(root);
                    stageEdit.setScene(sceneEdit);
                    stageEdit.setResizable(false);
                    stageEdit.setAlwaysOnTop(true);
                    stageEdit.showAndWait();
                    inViewMode = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No invoice selected!");
            alert.setHeaderText("Please select invoice from the table!");
            alert.show();
        }
    }
}
