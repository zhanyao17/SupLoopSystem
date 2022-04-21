package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To display all supplier purchase history
//First Written on: 6 April 2022
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
import base.salesDepartment.ArrayLists.RawMaterial;
import base.salesDepartment.ArrayLists.Supplier;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class supplierPurchaseHistoryController extends dashboardController implements Initializable {

    @FXML
    private TableView<PurchaseInvoice> purchaseInvoiceTable;

    @FXML
    private TableColumn<PurchaseInvoice, String> purchaseDateCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> purchaseInvoiceIDCol;

    @FXML
    private TableColumn<PurchaseInvoice, Float> purchaseQuantityCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> rmIDCol;

    @FXML
    private TableColumn<PurchaseInvoice, String> rmNameCol;

    @FXML
    private TableColumn<PurchaseInvoice, Float> totalCostCol;

    @FXML
    private TableView<RawMaterial> supplierRMTable;

    @FXML
    private TableColumn<RawMaterial, Float> supplyRmCostCol;

    @FXML
    private TableColumn<RawMaterial, String> supplyRmIDCol;

    @FXML
    private TableColumn<RawMaterial, String> supplyRmNameCol;

    @FXML
    private TextField searchPurchaseText, supplierIDText, supplierNameText,supplierMailText, supplierContactText;

    @FXML
    private Label usernameLabel;

    public static Supplier passSupplier;

    private ObservableList<PurchaseInvoice> purchaseInvoiceList = FXCollections.observableArrayList();

    private ObservableList<RawMaterial> materialList = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        purchaseInvoiceIDCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("invoiceID"));
        rmIDCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("rmID"));
        rmNameCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("rmName"));
        purchaseQuantityCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Float>("purchaseQuantity"));
        purchaseDateCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, String>("purchaseDate"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Float>("totalCost"));

        supplyRmIDCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialID"));
        supplyRmNameCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialName"));
        supplyRmCostCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, Float>("materialCost"));

        //set text field with supplier details
        supplierIDText.setText(passSupplier.getSupplierID());
        supplierNameText.setText(passSupplier.getSupplierName());
        supplierContactText.setText(passSupplier.getSupplierContact());
        supplierMailText.setText(passSupplier.getSupplierEmail());
        loadPurchaseInvoiceTable();
        loadSupplyRMTable();
        usernameLabel.setText(loginController.employeeName);
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
    public void loadSupplyRMTable(){
        try{
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT * FROM supplier_rm srm INNER JOIN raw_material rm ON srm.RM_ID = rm.RM_ID" +
                                                        " WHERE srm.Supplier_ID = '"+passSupplier.getSupplierID()+"'");
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                materialList.add(new RawMaterial(resultSet.getString("RM_ID"),
                                                 resultSet.getString("Raw_material_name"),
                                                 resultSet.getFloat("Material_cost_per_gram"),
                                                 resultSet.getString("Material_status")));
            }
            supplierRMTable.setItems(materialList);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    //load data into table
    public void loadPurchaseInvoiceTable() {
        purchaseInvoiceList.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement(
                    "SELECT pi.Pur_invoice_ID, po.Supplier_ID, prm.RM_ID, rm.Raw_material_name, " +
                    "prm.tot_quantity, pi.Pur_invoice_date, pi.tot_purchase_amount " +
                    "FROM purchase_invoice pi " +
                    "INNER JOIN purchase_order po ON pi.Purchase_ID = po.Purchase_ID " +
                    "INNER JOIN purchase_rm prm ON po.Purchase_ID = prm.Purchase_ID " +
                    "INNER JOIN raw_material rm ON prm.RM_ID = rm.RM_ID WHERE po.Supplier_ID = ? ORDER BY pi.Pur_invoice_date DESC"
            );

            statement.setString(1, passSupplier.getSupplierID());
            resultSet = statement.executeQuery();

            while (resultSet.next()){
                //create line of string for each record
                String purchaseData = resultSet.getString("Pur_Invoice_ID") + "\t" +
                        resultSet.getString("RM_ID") + "\t" +
                        resultSet.getString("Raw_material_name")+ "\t" +
                        resultSet.getString("tot_quantity")+ "\t" +
                        resultSet.getString("Pur_invoice_date")+ "\t" +
                        resultSet.getString("tot_purchase_amount");
                //filter the record that do not contain the search word
                if(purchaseData.toLowerCase(Locale.ROOT).contains(searchPurchaseText.getText().toLowerCase(Locale.ROOT))) {
                    PurchaseInvoice purchaseInvoice = new PurchaseInvoice(resultSet.getString("Pur_Invoice_ID"),
                            resultSet.getString("RM_ID"),
                            resultSet.getString("Raw_material_name"),
                            resultSet.getFloat("tot_quantity"),
                            resultSet.getString("Pur_invoice_date"),
                            resultSet.getFloat("tot_purchase_amount"));

                    purchaseInvoiceList.add(purchaseInvoice);
                }
            }
            purchaseInvoiceTable.setItems(purchaseInvoiceList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }


}
