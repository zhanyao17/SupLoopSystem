package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: searchSupplierController.java
//Description: To search and supplier for a raw material
//First Written on: 31 March 2022
//Edited on: 18 April 2022

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.Supplier;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class searchSupplierController implements Initializable {

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, String> supplierContactCol;

    @FXML
    private TableColumn<Supplier, String> supplierEmailCol;

    @FXML
    private TableColumn<Supplier, String> supplierIDCol;

    @FXML
    private TableColumn<Supplier, String> supplierNameCol;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private TextField searchSupplierText;

    private Stage stage;

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private Supplier supplierSelected = null;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        supplierIDCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierID"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierName"));
        supplierEmailCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierEmail"));
        supplierContactCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierContact"));
        loadTableData();
    }

    //get selected row in table
    public void getSupplierTableClick(){
        try{
            supplierSelected = supplierTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){

        }
    }
    //load data into table
    public void loadTableData(){
        try{
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT * FROM supplier");
            resultSet = statement.executeQuery();
            supplierList.clear();
            while (resultSet.next()){
                boolean supplierNotExist = true;
                //create line of string for each record
                String materialData = resultSet.getString("Supplier_ID")     + "\t" +
                        resultSet.getString("Supplier_name")         + "\t" +
                        resultSet.getString("Supplier_mail")         + "\t" +
                        resultSet.getString("Supplier_contact");

                //filter the record that do not contain the search word
                if(materialData.toLowerCase(Locale.ROOT).contains(searchSupplierText.getText().toLowerCase(Locale.ROOT))
                   && resultSet.getString("Supplier_status").equals("Active")){
                    Supplier supplier = new Supplier(resultSet.getString("Supplier_ID"),
                            resultSet.getString("Supplier_name"),
                            resultSet.getString("Supplier_mail"),
                            resultSet.getString("Supplier_contact"));

                    //check if the supplier has added into the table
                    for (Supplier spAdded: materialManagementController.searchSupplierList) {
                        if(spAdded.getSupplierID().equals(supplier.getSupplierID())){
                            supplierNotExist = false;
                            break;
                        }
                    }
                    if(supplierNotExist){
                        supplierList.add(supplier);
                    }
                }
            }
            supplierTable.setItems(supplierList);
        }catch (SQLException e){
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

    //pass selected supplier to table
    public void applySearch(){
        if(supplierSelected!= null){
            materialManagementController.searchSupplierList.add(supplierSelected);
            cancel();
        }

    }
    //close the window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }
}
