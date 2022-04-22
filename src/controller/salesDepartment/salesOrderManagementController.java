package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To manage all the sales order and sales invoice where admin can add new order, generate purchase order
//             and delete the invoice
//First Written on: 8 April 2022
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
import JDBC_Connectors.DBConnectors;

import base.salesDepartment.ArrayLists.SalesInvoice;
import base.salesDepartment.ArrayLists.SalesOrder;
import controller.Main;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class salesOrderManagementController extends dashboardController implements Initializable {

    @FXML
    private TableView<SalesOrder> salesOrderTable;

    @FXML
    private TableColumn<SalesOrder,  String> invoiceIDCol;

    @FXML
    private TableColumn<SalesOrder,  String> itemCodeCol;

    @FXML
    private TableColumn<SalesOrder,  String> orderDateCol;

    @FXML
    private TableColumn<SalesOrder,  String> orderIDCol;

    @FXML
    private TableColumn<SalesOrder,  String> postcodeCol;

    @FXML
    private TableColumn<SalesOrder,  Integer> quantityCol;

    @FXML
    private TableColumn<SalesOrder,  String> shippingAddressCol;

    @FXML
    private TableColumn<SalesOrder,  String> shippingDateCol;

    @FXML
    private TableColumn<SalesOrder,  String> shippingStatusCol;

    @FXML
    private TableColumn<SalesOrder,  String> statusCol;

    @FXML
    private TableView<SalesInvoice> invoiceTable;

    @FXML
    private TableColumn<SalesInvoice, String> emailCol;

    @FXML
    private TableColumn<SalesInvoice, String> invoiceDateCol;

    @FXML
    private TableColumn<SalesInvoice, String> salesInvoiceIDCol;

    @FXML
    private TableColumn<SalesInvoice, String> invoiceStatusCol;

    @FXML
    private TableColumn<SalesInvoice, String> nameCol;

    @FXML
    private TableColumn<SalesInvoice, Float> totalCostCol;

    @FXML
    private TableColumn<SalesInvoice, String> progressPercentCol;

    @FXML
    private TableColumn<SalesInvoice, ProgressBar> invoiceProgressCol;

    @FXML
    private TextField searchOrderText, searchInvoiceText;

    @FXML
    private Label usernameLabel;

    @FXML
    private ComboBox<String> shippingStatusCombo, statusCombo;

    private ObservableList<SalesOrder> salesOrderList = FXCollections.observableArrayList();

    private ObservableList<SalesInvoice> salesInvoiceList = FXCollections.observableArrayList();

    private final ObservableList<String> shippingStatusFilter = FXCollections.observableArrayList(
        "Show All", "Delivered","Completed", "In_complete", "Pending"
    );

    private final ObservableList<String> statusFilter = FXCollections.observableArrayList(
            "Show All", "Generated", "Ready", "Not_Ready"
    );

    private Boolean inPurchaseWindow = false;

    private SalesOrder salesOrderSelected = null;

    private SalesInvoice salesInvoiceSelected = null;

    private SalesInvoice temp = null;

    private Date lastClickTime = new Date();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private ResultSet resultSet2 = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Setup table column
        invoiceIDCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("invoiceID"));
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("itemCode"));
        orderIDCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("orderID"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("orderDate"));
        postcodeCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("postcode"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, Integer>("orderQuantity"));
        shippingAddressCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("shippingAddress"));
        shippingDateCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("shippingDate"));
        shippingStatusCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("shippingStatus"));
        statusCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("status"));

        salesInvoiceIDCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("invoiceID"));
        invoiceDateCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("orderDate"));
        emailCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("customerEmail"));
        nameCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("customerName"));
        invoiceStatusCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, Float>("totalCost"));
        progressPercentCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("progressPercent"));
        invoiceProgressCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, ProgressBar>("progress"));

        //setup combo box
        shippingStatusCombo.setItems(shippingStatusFilter);
        statusCombo.setItems(statusFilter);
        shippingStatusCombo.setValue("Show All");
        statusCombo.setValue("Show All");
        loadOrderTableData();
        loadInvoiceTableData();
        usernameLabel.setText(loginController.employeeName);
    }

    //get selected row in table
    public void getOrderTableClick(){
        try{
            salesOrderSelected = salesOrderTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //get selected row in table
    public void getInvoiceTableClick(){
        try{
            salesInvoiceSelected = invoiceTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //pop out a window to generate purchase order
    public void generatePurchaseOrder(){
        try{
            if(salesOrderSelected != null){
                if(!inPurchaseWindow){
                    //avoid user from generating purchase order twice for an order
                    if(salesOrderSelected.getStatus().equals("Ready") || salesOrderSelected.getStatus().equals("Generated")){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("The purchase order for this sale order has been generated");
                        alert.setContentText("You cannot generate multiple purchase order!");
                        alert.show();
                        return;
                    }
                    inPurchaseWindow = true;
                    //get resource from fxml file
                    generatePurchaseOrderController.passOrder = salesOrderSelected;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/generatePurchaseOrder.fxml"));
                    Parent Root = loader.load();
                    //setup new window
                    Stage stage = new Stage();
                    Scene scene = new Scene(Root);
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);
                    stage.showAndWait();
                    inPurchaseWindow = false;
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("No order selected!");
                alert.setContentText("Please select an order first");
                alert.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //load data into order table
    public void loadOrderTableData() {
        salesOrderList.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("select * from sales_order ORDER BY Order_date DESC");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                //get filter option
                if(!shippingStatusCombo.getValue().equals("Show All") &&
                   !shippingStatusCombo.getValue().equals(resultSet.getString("Shipping_status"))){
                    continue;
                }
                //get filter option
                if(!statusCombo.getValue().equals("Show All") &&
                   !statusCombo.getValue().equals(resultSet.getString("Status"))){
                    continue;
                }
                //create line of string for each record
                String orderData = resultSet.getString("Sales_Invoice_ID") + "\t" +
                        resultSet.getString("Order_ID") + "\t" +
                        resultSet.getString("Item_code")+ "\t" +
                        resultSet.getString("Order_quantity")+ "\t" +
                        resultSet.getString("Order_date")+ "\t" +
                        resultSet.getString("Due_date")+ "\t" +
                        resultSet.getString("Shipping_address")+ "\t" +
                        resultSet.getString("Postcode")+ "\t" +
                        resultSet.getString("Shipping_status")+ "\t" +
                        resultSet.getString("Status");
                //filter the record that do not contain the search word
                if(orderData.toLowerCase(Locale.ROOT).contains(searchOrderText.getText().toLowerCase(Locale.ROOT))) {
                    SalesOrder salesOrder = new SalesOrder(resultSet.getString("Sales_Invoice_ID"),
                            resultSet.getString("Order_ID"),
                            resultSet.getString("Item_code"),
                            resultSet.getInt("Order_quantity"),
                            resultSet.getString("Order_date"),
                            resultSet.getString("Due_date"),
                            resultSet.getString("Shipping_address"),
                            resultSet.getString("Postcode"),
                            resultSet.getString("Shipping_status"),
                            resultSet.getString("Status"));

                    salesOrderList.add(salesOrder);
                }
            }
            salesOrderTable.setItems(salesOrderList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //load data into invoice table
    public void loadInvoiceTableData() {
        salesInvoiceList.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT DISTINCT si.Sales_Invoice_ID, so.Order_date, si.Email, " +
                                                        "c.Cust_Name ,si.Order_total_price " +
                                                        "FROM sales_invoice si " +
                                                        "INNER JOIN sales_order so on si.Sales_Invoice_ID = so.Sales_Invoice_ID " +
                                                        "INNER JOIN customer c on si.Email = c.Email ORDER BY si.Sales_Invoice_ID DESC;");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                statement = connection.prepareStatement("SELECT * FROM sales_order WHERE Sales_Invoice_ID = ?;");
                statement.setString(1,resultSet.getString("Sales_Invoice_ID"));
                resultSet2 = statement.executeQuery();
                String status = "Completed";
                double numberOfOrder = 0;
                double completedOrder = 0;
                //set status
                while(resultSet2.next()){
                   if(resultSet2.getString("Shipping_status").equals("In_complete") ||
                      resultSet2.getString("Shipping_status").equals("Pending") ){
                        status= "In_Progress";
                   }else{
                       completedOrder++;
                   }
                   numberOfOrder++;

                }
                //create line of string for each record
                String invoiceData =
                        resultSet.getString("Sales_Invoice_ID") + "\t" +
                        resultSet.getString("Order_date") + "\t" +
                        resultSet.getString("Email") + "\t" +
                        resultSet.getString("Cust_Name") + "\t" +
                        status + "\t" +
                        resultSet.getString("Order_total_price");
                //filter the record that do not contain the search word
                if(invoiceData.toLowerCase(Locale.ROOT).contains(searchInvoiceText.getText().toLowerCase(Locale.ROOT))) {
                    SalesInvoice salesInvoice = new SalesInvoice(
                            resultSet.getString("Sales_Invoice_ID"),
                            resultSet.getString("Order_date"),
                            resultSet.getString("Email"),
                            resultSet.getString("Cust_Name"),
                            status,
                            resultSet.getFloat("Order_total_price"),
                            ((int)(completedOrder/numberOfOrder*100)) + "%");

                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setStyle("-fx-accent: #00FF00;");


                    progressBar.setProgress(completedOrder/numberOfOrder);
                    salesInvoice.setProgress(progressBar);
                    salesInvoiceList.add(salesInvoice);
                }
            }
            invoiceTable.setItems(salesInvoiceList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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
        if(resultSet2 != null){
            try {
                resultSet2.close();
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
    //switch to addNewOrder view
    public void addNewOrder(){
        try {
            new Main().switchScene("/fxml/salesDepartment/addNewOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //detele sales order
    public void deleteOrder(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(salesInvoiceSelected != null){
            if(!salesInvoiceSelected.getStatus().equals("Completed")){
                try{
                    //show confirmation message
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setTitle("Cancel Order");
                    alert1.setHeaderText("You are going to cancel an order");
                    alert1.setContentText("Are you sure?");
                    if(alert1.showAndWait().get() != ButtonType.OK){
                        return;
                    }

                    //retrieve data from database
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("SELECT * FROM sales_order WHERE Sales_Invoice_ID = ?");
                    statement.setString(1,salesInvoiceSelected.getInvoiceID());
                    resultSet = statement.executeQuery();
                    //check sales order's status
                    Boolean checkStatus = true;
                    while(resultSet.next()){
                        if(resultSet.getString("Status").equals("Ready") || resultSet.getString("Status").equals("Generated")){
                            checkStatus = false;
                            break;
                        }
                    }
                    //delete order if the purchase order hasn't generated
                    if(checkStatus){
                        statement = connection.prepareStatement("DELETE FROM sales_order WHERE Sales_Invoice_ID = ?");
                        statement.setString(1, salesInvoiceSelected.getInvoiceID());
                        statement.execute();

                        statement = connection.prepareStatement("DELETE FROM sales_invoice WHERE Sales_Invoice_ID = ?");
                        statement.setString(1, salesInvoiceSelected.getInvoiceID());
                        statement.execute();
                        alert.setHeaderText("Order has been cancelled successfully");
                    }else{
                        alert.setHeaderText("Selected sales order cannot be deleted!");
                        alert.setContentText("Purchase order has been generated!");
                    }
                    loadOrderTableData();
                    loadInvoiceTableData();


                }catch (SQLException e){
                    e.printStackTrace();
                }finally{
                    closeConnection();
                }
            }else{
                alert.setHeaderText("Selected sales order cannot be deleted!");
                alert.setContentText("The sales order has been completed");
            }
        }else{
            alert.setHeaderText("Please select a sales order in the table!");
        }
        alert.show();
    }


}