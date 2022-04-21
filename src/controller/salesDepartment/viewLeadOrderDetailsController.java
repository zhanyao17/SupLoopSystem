package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To display all the order for a sales invoices
//First Written on: 7 April 2022
//Edited on: 18 April 2022

import controller.loginPage.loginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.SalesOrder;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class viewLeadOrderDetailsController implements Initializable {

    @FXML
    private TableView<SalesOrder> leadOrderDetailsTable;

    @FXML
    private TableColumn<SalesOrder, String> orderIDCol;

    @FXML
    private TableColumn<SalesOrder, String> itemCodeCol;

    @FXML
    private TableColumn<SalesOrder, String> itemNameCol;

    @FXML
    private TableColumn<SalesOrder, Integer> quantityCol;

    @FXML
    private TableColumn<SalesOrder, Float> subTotalCol;

    @FXML
    private TextField totalText;

    @FXML
    private Label invoiceIDLabel;

    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    public static SalesOrder passOrder = null;

    private ObservableList<SalesOrder> orderDetailsList = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        orderIDCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("orderID"));
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("itemCode"));
        itemNameCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, String>("itemName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, Integer>("orderQuantity"));
        subTotalCol.setCellValueFactory(new PropertyValueFactory<SalesOrder, Float>("subTotal"));
        totalText.setText(String.valueOf(passOrder.getTotalCost())); //set total cost value
        invoiceIDLabel.setText("Sales Invoice ID: "+passOrder.getInvoiceID());
        loadTableData();
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
            statement = connection.prepareStatement("SELECT so.Order_ID, so.Order_quantity, so.Item_code, i.Item_name, i.Item_per_price " +
                                                        "FROM sales_order so INNER JOIN item i on so.Item_code = i.Item_code "+
                                                        "WHERE Sales_Invoice_ID = '"+passOrder.getInvoiceID()+"'");
            resultSet = statement.executeQuery();
            orderDetailsList.clear();
            while (resultSet.next()) {
                //create object of Sales order
                SalesOrder salesOrder = new SalesOrder(resultSet.getString("Order_ID"),
                        resultSet.getString("Item_code"),
                        resultSet.getString("Item_name"),
                        resultSet.getInt("Order_quantity"));

                salesOrder.setSubTotal(resultSet.getFloat("Item_per_price")*salesOrder.getOrderQuantity());

                orderDetailsList.add(salesOrder);
            }

            leadOrderDetailsTable.setItems(orderDetailsList);
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    //close window
    public void back(){
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }
}
