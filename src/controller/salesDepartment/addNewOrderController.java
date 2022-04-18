package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: addNewOrderController.java
//Description: To add new sales order for customer
//First Written on: 8 April 2022
//Edited on: 18 April 2022

import base.salesDepartment.ArrayLists.Customer;
import base.salesDepartment.ArrayLists.Item;
import controller.Main;
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
import javafx.util.Duration;


import JDBC_Connectors.DBConnectors;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;



public class addNewOrderController implements Initializable {

    @FXML
    private TableView<Item> itemTable;

    @FXML
    private TableColumn<Item, Integer> itemAmountCol;

    @FXML
    private TableColumn<Item, String> itemCodeCol;

    @FXML
    private TableColumn<Item, String> itemNameCol;

    @FXML
    private TableColumn<Item, Float> itemPriceCol;

    @FXML
    private TextField customerName, totalText, estimatedDateText, postcodeText;

    @FXML
    private TextArea locationText;

    @FXML
    private RadioButton defaultAddressRadio, newAddressRadio;

    @FXML
    private DatePicker shippingDatePicker;

    @FXML
    private Label helpLabel;

    public static ObservableList<Item> purchaseItemList = FXCollections.observableArrayList();

    public static Customer passCustomer = null;

    private Item itemSelected = null;

    private LocalDate shippingDate;

    private boolean inSelectItemMode = false;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup item table
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemCode"));
        itemNameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemName"));
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<Item, Float>("itemPrice"));
        itemAmountCol.setCellValueFactory(new PropertyValueFactory<Item, Integer>("amount"));

        //setup help tips
        final Tooltip helpTip = new Tooltip();
        helpTip.setText("Estimated date is the minimum date required to complete this order");
        helpTip.setShowDelay(Duration.ZERO);
        helpLabel.setTooltip(helpTip);

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

    //set shipping address and postcode
    public void getAddress(){
        boolean editable = true;
        if(defaultAddressRadio.isSelected() && passCustomer !=null){
            editable = false;
            //set shipping address and postcode based on customer record in database
            locationText.setText(passCustomer.getLocation());
            postcodeText.setText(passCustomer.getPostcode());
        }else if(newAddressRadio.isSelected()){
            editable = true;
            locationText.setText("");
            postcodeText.setText("");
        }
        locationText.setEditable(editable);
        postcodeText.setEditable(editable);
    }

    //get selected item row in table
    public void getItemTableClick(){
        try{
            itemSelected = itemTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //pop out new window to add new item
    public void addPurchaseItem(){
        if(inSelectItemMode){ //to avoid user from pop out multiple window

        }else{
            try{
                inSelectItemMode = true;
                //get resource from the fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/searchItem.fxml"));
                Parent searchItemRoot = loader.load();

                //setup the window
                Stage stage = new Stage();
                Scene scene = new Scene(searchItemRoot);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.setAlwaysOnTop(true);
                stage.showAndWait();
                inSelectItemMode = false;

                //add selected item to the table
                itemTable.setItems(purchaseItemList);
                calculateCost();
                calculateEstimatedDate();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //remove item from the table
    public void removePurchaseItem(){
        if(itemSelected!= null){
            purchaseItemList.remove(itemSelected);
            itemTable.setItems(purchaseItemList);
        }
        calculateCost();
        calculateEstimatedDate();
    }

    //search customer to add order
    public void selectLead(){
        try{
            //get resource from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/searchLead.fxml"));
            Parent searchItemRoot = loader.load();

            //setup new window
            Stage stage = new Stage();
            Scene scene = new Scene(searchItemRoot);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
            if(passCustomer!= null){
                defaultAddressRadio.setSelected(true);
                getAddress();
                customerName.setText(passCustomer.getName());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //calculate total cost for the order
    public void calculateCost(){
        double total = 0;
        for(Item item : purchaseItemList){
            total += item.getAmount()*item.getItemPrice();
        }
        totalText.setText(String.format("%.2f",total));
        System.out.println(total);
        System.out.println(totalText.getText());
    }

    //calculate minimum manufacture time required for this order
    public void calculateEstimatedDate(){
        float timeRequired = 0;
        try{
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT SUM(Order_quantity*Item_Time/2500) AS Total_Time " +
                            "FROM (SELECT so.Order_ID, so.Item_code, so.Order_quantity, SUM(io.OP_time) as Item_Time " +
                            "FROM sales_order so " +
                            "INNER JOIN item_operation io ON so.Item_code = io.Item_code " +
                            "WHERE so.Shipping_status != 'Delivered' " +
                            "GROUP BY so.Order_ID) AS a");
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                timeRequired = resultSet.getFloat("Total_Time"); //get time required to process other order
            }
            //calculate total time require to process all item in this order
            for(Item item : purchaseItemList){
                statement = connection.prepareStatement("SELECT Item_code, SUM(OP_time) AS OP_time FROM item_operation" +
                                                            " WHERE Item_code = '"+item.getItemCode()+"'");
                resultSet = statement.executeQuery();
                if(resultSet.next()){
                    timeRequired += resultSet.getInt("OP_Time") * (float) item.getAmount()/2500;
                }
            }

            LocalDate date = LocalDate.now();
            //2 days for purchase order, another 2 days for delivery
            date = date.plusDays((long) (4 + Math.ceil(timeRequired/60/12)));
            estimatedDateText.setText(String.valueOf(date));

        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    public void getShippingDate(){
        shippingDate = shippingDatePicker.getValue();
    }

    //add new order
    public void submitOrder() throws Exception {
        if(validateOrderInput()){
            try{
                connection = new DBConnectors().getConnection();
                //add new sales invoices
                statement = connection.prepareStatement("SELECT MAX((regexp_replace(Sales_Invoice_ID,'[^0-9]',''))+0) AS 'ID' FROM sales_invoice ");
                resultSet = statement.executeQuery();

                String salesInvoiceID;
                if (resultSet.next()) {
                    salesInvoiceID = "S" + (resultSet.getInt("ID")+1);
                }else{
                    salesInvoiceID = "S1";
                }

                statement = connection.prepareStatement("INSERT INTO sales_invoice VALUES(?,?,?)");
                statement.setString(1,salesInvoiceID);
                statement.setString(2,passCustomer.getEmail());
                statement.setFloat(3, Float.parseFloat(totalText.getText()));
                statement.execute();


                //add new order based on item
                for(Item item : purchaseItemList){
                    statement = connection.prepareStatement("SELECT MAX((regexp_replace(Order_ID,'[^0-9]',''))+0) AS 'Order_ID' FROM sales_order ");
                    resultSet = statement.executeQuery();
                    String orderID;
                    if (resultSet.next()) {
                        orderID = "O" + (resultSet.getInt("Order_ID")+1);
                    }else{
                        orderID = "O1";
                    }
                    System.out.println(orderID);
                    statement = connection.prepareStatement("INSERT INTO sales_order VALUES(?,?,?,?,?,?,?,?,?,?)");
                    statement.setString(1,orderID);
                    statement.setString(2,item.getItemCode());
                    statement.setString(3,salesInvoiceID);
                    statement.setInt(4,item.getAmount());
                    statement.setString(5, String.valueOf(LocalDate.now()));
                    statement.setString(6, String.valueOf(shippingDate));
                    statement.setString(7, locationText.getText());
                    statement.setString(8, postcodeText.getText());
                    statement.setString(9,"Pending");
                    statement.setString(10,"Not_Ready");
                    statement.execute();
                }
                cancelOrder();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                closeConnection();
            }
        }


    }

    //clear all the textfield and table
    public void clearField(){
        passCustomer = null;
        purchaseItemList.clear();
        itemTable.setItems(purchaseItemList);
        defaultAddressRadio.setSelected(false);
        newAddressRadio.setSelected(false);
        getAddress();
        shippingDatePicker.setValue(null);
        getShippingDate();
        calculateCost();
    }

    //discard the order
    public void cancelOrder() throws Exception {
        clearField();
        new Main().switchScene("/fxml/salesDepartment/salesOrderManagement.fxml");

    }

    //check user input
    public boolean validateOrderInput(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        if(passCustomer == null){
            alert.setHeaderText("You must select a customer first!");
            alert.show();
            return false;
        }
        else if(locationText.getText().isEmpty()){
            alert.setHeaderText("Please enter a shipping address!");
            alert.show();
            return false;
        }
        else if(postcodeText.getText().isEmpty()){
            alert.setHeaderText("Please enter a postcode!");
            alert.show();
            return false;
        }
        else if(purchaseItemList.isEmpty()){
            alert.setHeaderText("You must at least have an item added in the table for order");
            alert.setContentText("Click on \"Add new item\" to item" );
            alert.show();
            return false;
        }
        else if(shippingDate == null){
            alert.setHeaderText("Error, shipping date is not provided");
            alert.setContentText("Please choose a date" );
            alert.show();
            return false;
        }
        else if(shippingDate.isBefore(LocalDate.parse(estimatedDateText.getText()))){
            alert.setHeaderText("The shipping date cannot be earlier than the estimatedDate");
            alert.setContentText("Please choose another date" );
            alert.show();
            return false;
        }

        return true;
    }



}
