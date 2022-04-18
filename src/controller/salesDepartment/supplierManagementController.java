package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To manage all supplier in database
//First Written on: 5 April 2022
//Edited on: 18 April 2022

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import JDBC_Connectors.DBConnectors;

import base.salesDepartment.ArrayLists.Supplier;
import base.salesDepartment.Method.publicMethod;
import controller.Main;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class supplierManagementController extends dashboardController implements Initializable {
    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, String> spIDCol;

    @FXML
    private TableColumn<Supplier, String> spNameCol;

    @FXML
    private TableColumn<Supplier, String> spMailCol;

    @FXML
    private TableColumn<Supplier, String> spContactCol;

    @FXML
    private TableColumn<Supplier, String> spStatusCol;

    @FXML
    private TextField searchBarText, spNameText, spContactText, spMailText;

    @FXML
    private Label titleLabel;

    @FXML
    private Button addButton, editButton;

    @FXML
    private CheckBox showAllCheckBox;

    private String filterStatus = "'Active'";

    boolean inEditMode = false;

    private Supplier supplierSelected = null;

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table
        spIDCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierID"));
        spNameCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierName"));
        spMailCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierEmail"));
        spContactCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierContact"));
        spStatusCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("status"));
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
    public void getSupplierTableClick(){
        try{
            supplierSelected = supplierTable.getSelectionModel().getSelectedItem();
        }catch (Exception e){
            e.printStackTrace();
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
    //load data into table
    public void loadTableData(){
        try{
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT * FROM supplier WHERE Supplier_status IN ("+filterStatus+")");
            resultSet = statement.executeQuery();
            supplierList.clear();
            while (resultSet.next()){
                //create line of string for each record
                String supplierData = resultSet.getString("Supplier_ID") + "\t" +
                                      resultSet.getString("Supplier_name")+ "\t" +
                                      resultSet.getString("Supplier_mail")+ "\t" +
                                      resultSet.getString("Supplier_contact")+ "\t" +
                                      resultSet.getString("Supplier_status");
                //filter the record that do not contain the search word
                if(supplierData.toLowerCase(Locale.ROOT).contains(searchBarText.getText().toLowerCase(Locale.ROOT))){
                    Supplier supplier = new Supplier (resultSet.getString("Supplier_ID"),
                                                      resultSet.getString("Supplier_name"),
                                                      resultSet.getString("Supplier_mail"),
                                                      resultSet.getString("Supplier_contact"),
                                                      resultSet.getString("Supplier_status"));
                    supplierList.add(supplier);
                }
            }
            supplierTable.setItems(supplierList);
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    //add new supplier to database
    public void addNewSupplier(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(validateSupplierInput()){
            try{
                connection = new DBConnectors().getConnection();
                if(inEditMode){ //update supplier details
                    statement = connection.prepareStatement("UPDATE supplier SET Supplier_name = ?, Supplier_mail = ?," +
                            " Supplier_contact = ? WHERE Supplier_ID = ?");
                    statement.setString(1,spNameText.getText().trim());
                    statement.setString(2,spMailText.getText().trim());
                    statement.setString(3,spContactText.getText());
                    statement.setString(4,supplierSelected.getSupplierID());
                    statement.execute();

                    alert.setHeaderText("Supplier's Details has been saved successfully!");
                }else{ //add new supplier record
                    statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM supplier");
                    resultSet = statement.executeQuery();

                    //get primary key
                    String formattedID = "SP001";
                    if(resultSet.next()){
                        formattedID = String.format("SP%03d", (resultSet.getInt("Total")+1));
                    }
                    //insert data
                    statement = connection.prepareStatement("INSERT INTO supplier VALUES ('"+ formattedID + "', "+
                            "'" + spNameText.getText().trim() + "', "+
                            "'" + spMailText.getText().trim() + "', "+
                            "'" + spContactText.getText() + "','Active'); ");
                    statement.execute();
                    alert.setHeaderText("New supplier has been added successfully!");
                }
                alert.show();
                clearField();
                loadTableData();
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                closeConnection();
            }
        }

    }
    //switch to supplier purchase history view
    public void viewPurchaseHistory(){
        try{
            if(supplierSelected!= null){
                supplierPurchaseHistoryController.passSupplier = supplierSelected;
                new Main().switchScene("/fxml/salesDepartment/supplierPurchaseHistory.fxml");
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error, No supplier Selected!");
                alert.setContentText("Please select a supplier");
                alert.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //edit supplier details
    public void editSupplier(){
        if(inEditMode) {
            inEditMode = false;
            clearField();
        }else {
            if(supplierSelected !=null) {
                if(supplierSelected.getStatus().equals("Closed")){
                    //avoid user from editing deleted record
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Edit Supplier");
                    alert.setHeaderText("This supplier record has been delete");
                    alert.setContentText("You cannot edit this supplier");
                    alert.show();
                }else{
                    //fill in the text field with the supplier details
                    inEditMode = true;
                    editButton.setText("Cancel Edit");
                    addButton.setText("Save");
                    titleLabel.setText("Editing - " + supplierSelected.getSupplierID());
                    spNameText.setText(supplierSelected.getSupplierName());
                    spMailText.setText(supplierSelected.getSupplierEmail());
                    spContactText.setText(supplierSelected.getSupplierContact());
                }
            }else {
                //alert user to select a row in table
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Edit Supplier");
                alert.setHeaderText("Please Select a supplier to edit");
                alert.show();
            }
        }
    }
    //delete supplier record in database
    public void deleteSupplier(){
        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        if(inEditMode) {
            //avoid user from deleting a record while editing
            alertInfo.setTitle("Delete Action");
            alertInfo.setHeaderText("You cannot perform this action in edit mode!");
            alertInfo.show();
            return;
        }
        if(supplierSelected != null){
            //show confirmation message
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Raw Material");
            alert.setHeaderText("You are about to delete supplier record - "+supplierSelected.getSupplierID());
            alert.setContentText("Are you sure?");

            if(alert.showAndWait().get() == ButtonType.OK){
                if(supplierSelected.getStatus().equals("Closed")){
                    alertInfo.setTitle("Delete Action");
                    alertInfo.setHeaderText("This supplier record has been deleted");
                    alertInfo.setContentText("You cannot delete this record again");
                    alertInfo.show();
                    return;
                }
                try {
                    //retrieve data from database and check whether the supplier is supplying any raw material
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("SELECT * FROM supplier_rm WHERE Supplier_ID = '"+supplierSelected.getSupplierID()+"'");
                    resultSet = statement.executeQuery();
                    if(resultSet.next()){
                        //avoid user from deleting supplier that is supplying any raw material
                        Alert alertError = new Alert(Alert.AlertType.ERROR);
                        alertError.setTitle("Delete Supplier");
                        alertError.setHeaderText("You cannot perform this action!");
                        alertError.setContentText("There is raw material supplied by this supplier, please remove the supplier from the raw material first!");
                        alertError.show();
                    }else{
                        //delete related record
                        statement = connection.prepareStatement("DELETE FROM supplier_rm WHERE Supplier_ID = '"+supplierSelected.getSupplierID()+"';");
                        statement.execute();
                        //update status to "Closed"
                        statement = connection.prepareStatement("UPDATE supplier SET Supplier_status = 'Closed' WHERE Supplier_ID = '"+supplierSelected.getSupplierID()+"';");
                        statement.execute();
                        loadTableData();

                        alertInfo.setTitle("Delete Action");
                        alertInfo.setHeaderText("The supplier record has been removed!");
                        alertInfo.show();
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    closeConnection();
                }

            }
        }else{
            alertInfo.setTitle("Delete Action");
            alertInfo.setHeaderText("Please select a supplier from the table to delete!");
            alertInfo.show();
        }

    }
    //check user input
    public boolean validateSupplierInput(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Add new supplier");
        if(spNameText.getText().isEmpty() || spContactText.getText().isEmpty() || spMailText.getText().isEmpty()){
            alert.setHeaderText("You must fill in all required field!");
            alert.show();
            return false;
        //check name input
        }else if(spNameText.getText().charAt(0) == ' '){
            alert.setHeaderText("Please avoid trailing space in name!");
            alert.show();
            return false;
        //check name input
        }else if(spNameText.getText().length() > 30){
            alert.setHeaderText("Supplier name cannot exceed 30 character!");
            alert.show();
            return false;
        //check contact number format
        }else if(!publicMethod.checkContact(spContactText.getText())){
            alert.setHeaderText("Invalid contact number!\n Please follow the format:\n ###-###-#### or ##-####-####!");
            alert.show();
            return false;
        //check email format
        }else if(!publicMethod.checkEmail(spMailText.getText())){
            alert.setHeaderText("Invalid email address!");
            alert.show();
            return false;
        }
        return true;
    }
    //reset text field
    public void clearField(){
        titleLabel.setText("Add New Supplier");
        spNameText.setText(null);
        spMailText.setText(null);
        spContactText.setText(null);
        editButton.setText("Edit");
        addButton.setText("Add");
    }


}
