package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To manage all purchase order in database
//First Written on: 13 April 2022
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
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.PurchaseInvoice;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class purchaseOrderManagementController extends dashboardController implements Initializable {

    @FXML
    private TableView<PurchaseInvoice> purchaseOrderTable;

    @FXML
    private TableColumn<PurchaseInvoice, String> purchaseIDCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> orderIDCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> supplierNameCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> rawMaterialCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> purchaseDateCol;

    @FXML
    private TableColumn<PurchaseInvoice, Float> purchaseQuantityCol;

    @FXML
    private TableColumn<PurchaseInvoice, Float> totalCostCol;

    @FXML
    private TextField searchText;

    @FXML
    private Label usernameLabel;

    private ObservableList<PurchaseInvoice> purchaseList = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        purchaseIDCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("invoiceID"));
        orderIDCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("orderID"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("supplierName"));
        rawMaterialCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("rmName"));
        purchaseDateCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("purchaseDate"));
        purchaseQuantityCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Float>("purchaseQuantity"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Float>("totalCost"));
        loadPurchaseTableData();
        usernameLabel.setText(loginController.employeeName);
    }

    //load data into table
    public void loadPurchaseTableData(){
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT po.Purchase_ID, po.Order_ID,s.Supplier_name, rm.Raw_material_name, pi.Pur_invoice_date,prm.tot_quantity, pi.tot_purchase_amount " +
                                                        "FROM purchase_order po " +
                                                        "INNER JOIN purchase_invoice pi ON po.Purchase_ID = pi.Purchase_ID " +
                                                        "INNER JOIN purchase_rm prm ON po.Purchase_ID = prm.Purchase_ID " +
                                                        "INNER JOIN supplier s ON po.Supplier_ID = s.Supplier_ID " +
                                                        "INNER JOIN raw_material rm ON prm.RM_ID = rm.RM_ID " +
                                                        "ORDER BY (regexp_replace(po.Purchase_ID,'[^0-9]','')) +0 DESC");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                //create line of string for each record
                String purchaseData = resultSet.getString("Purchase_ID") + "\t" +
                                      resultSet.getString("Order_ID")+ "\t" +
                                      resultSet.getString("Supplier_name")+ "\t" +
                                      resultSet.getString("Raw_material_name")+ "\t" +
                                      resultSet.getFloat("tot_quantity")+ "\t" +
                                      resultSet.getString("Pur_invoice_date")+ "\t" +
                                      resultSet.getFloat("tot_purchase_amount");
                //filter all the purchase order record that do not contain the search word
                if(purchaseData.toLowerCase(Locale.ROOT).contains(searchText.getText().toLowerCase(Locale.ROOT))){
                    PurchaseInvoice pi = new PurchaseInvoice(resultSet.getString("Purchase_ID"),
                            resultSet.getString("Order_ID"),
                            resultSet.getString("Supplier_name"),
                            resultSet.getString("Raw_material_name"),
                            resultSet.getFloat("tot_quantity"),
                            resultSet.getString("Pur_invoice_date"),
                            resultSet.getFloat("tot_purchase_amount"));

                    purchaseList.add(pi);
                }
            }
            purchaseOrderTable.setItems(purchaseList);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }

    }

    //close the connection to database
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
