package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: materialManagementController.java
//Description: To manage all the raw material in database
//First Written on: 31 March 2022
//Edited on: 18 April 2022

import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.RawMaterial;
import base.salesDepartment.ArrayLists.Supplier;
import base.salesDepartment.Method.publicMethod;
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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class materialManagementController extends dashboardController implements Initializable {

    @FXML
    private TableView<RawMaterial> rawMaterialTable;

    @FXML
    private TableColumn<RawMaterial, String> rmIDCol;

    @FXML
    private TableColumn<RawMaterial, String> rmNameCol;

    @FXML
    private TableColumn<RawMaterial, Float> rmCostCol;

    @FXML
    private TableColumn<RawMaterial, String> rmStatusCol;

    @FXML
    private TableView<Supplier> searchSupplierTable;

    @FXML
    private TableColumn<Supplier, String> supplierIDCol;

    @FXML
    private TableColumn<Supplier, String> supplierNameCol;

    @FXML
    private TextField rmNameText, rmCostText,searchMaterialText;

    @FXML
    private CheckBox showAllCheckBox;

    @FXML
    private Button editButton, addButton;

    @FXML
    private Label editModeLabel;

    private String editRM_ID, filterStatus = "'Active'";

    private ObservableList<RawMaterial> materialList = FXCollections.observableArrayList();

    public static ObservableList<Supplier> searchSupplierList = FXCollections.observableArrayList();

    private RawMaterial materialSelected = null;

    private Supplier supplierSelected = null;

    private boolean inSearchMode = false;

    private boolean inEditMode = false;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup raw material table column
        rmIDCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialID"));
        rmNameCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialName"));
        rmCostCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, Float>("materialCost"));
        rmStatusCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("status"));
        //setup supplier table column
        supplierIDCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierID"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<Supplier, String>("supplierName"));
        searchSupplierList.clear();
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
    public void getMaterialTableClick(){
        try{
            materialSelected = rawMaterialTable.getSelectionModel().getSelectedItem();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //get selected row in table
    public void getSearchSupplierTableClick(){
        try{
            supplierSelected = searchSupplierTable.getSelectionModel().getSelectedItem();
        }catch (Exception e){
            e.printStackTrace();
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
            statement = connection.prepareStatement("SELECT * FROM raw_material WHERE Material_status in ("+filterStatus+")");
            resultSet = statement.executeQuery();
            materialList.clear();
            while (resultSet.next()){
                //create line of string for each material record
                String materialData = resultSet.getString("RM_ID")                     + "\t" +
                                      resultSet.getString("Raw_material_name")         + "\t" +
                                      resultSet.getFloat("Material_cost_per_gram")     + "\t" +
                                      resultSet.getString("Material_status");
                //filter out the material record that does not contains the words in search bar
                if(materialData.toLowerCase(Locale.ROOT).contains(searchMaterialText.getText().toLowerCase(Locale.ROOT))){
                    RawMaterial rawMaterial = new RawMaterial(resultSet.getString("RM_ID"),
                                                              resultSet.getString("Raw_material_name"),
                                                              resultSet.getFloat("Material_cost_per_gram"),
                                                              resultSet.getString("Material_status"));
                    materialList.add(rawMaterial);
                }
            }
            rawMaterialTable.setItems(materialList);
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //pop out new window to add supplier
    public void searchSupplier(){
        if(!inSearchMode){ // to avoid user from popping out multiple search window
            try{
                //get resource from supplier
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/searchSupplier.fxml"));
                Parent searchSupplierRoot = loader.load();
                //setup window
                Stage stageMaterial = new Stage();
                Scene sceneMaterial = new Scene(searchSupplierRoot);
                stageMaterial.setScene(sceneMaterial);
                stageMaterial.setResizable(false);
                stageMaterial.setAlwaysOnTop(true);
                inSearchMode = true;
                stageMaterial.showAndWait();
                if(!searchSupplierList.isEmpty()){
                    searchSupplierTable.setItems(searchSupplierList);
                }
                inSearchMode = false;

            }catch(Exception e){
                e.printStackTrace();
                inSearchMode = false;
            }
        }
    }

    //save new material into database
    public void addNewMaterial(){
        if(validateMaterialInfo()){
            if(inEditMode){
                updateRawMaterial();
            }else{
                try{
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM raw_material");
                    resultSet = statement.executeQuery();
                    //get unique primary for new material
                    String formattedID = "RM001";
                    if(resultSet.next()){
                        formattedID = String.format("RM%03d", (resultSet.getInt("Total")+1));
                    }
                    //insert data into raw_material table
                    statement = connection.prepareStatement("INSERT INTO raw_material VALUES (?,?,?,'Active')");
                    statement.setString(1,formattedID);
                    statement.setString(2,rmNameText.getText());
                    statement.setFloat(3, Float.parseFloat(rmCostText.getText()));
                    statement.execute();

                    //insert data into supplier_rm table
                    for (Supplier sp : searchSupplierList) {
                        statement = connection.prepareStatement("INSERT INTO supplier_rm VALUES ('"+ formattedID + "', "+
                                "'" + sp.getSupplierID() + "'); ");
                        statement.execute();
                    }
                    loadTableData();
                    clearField();
                }catch(SQLException e){
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }

        }
    }
    //reset text field
    public void clearField(){
        rmNameText.setText(null);
        rmCostText.setText(null);
        searchSupplierList.clear();
        editModeLabel.setText("Add New Material");
        addButton.setText("Add");
        editButton.setText("Edit");
    }
    //delete material record in database
    public void deleteMaterial(){
        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        if(inEditMode){
            //avoid user from deleting material record while editing
            alertInfo.setTitle("Delete Action");
            alertInfo.setHeaderText("You cannot perform this action under edit mode!");
            alertInfo.show();
            return;
        }
        if(materialSelected != null){
            //show confirmation message
            Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            alertConfirm.setTitle("Delete Raw Material");
            alertConfirm.setHeaderText("You are about to delete a raw material - "+materialSelected.getMaterialID());
            alertConfirm.setContentText("Are you sure?");

            if(alertConfirm.showAndWait().get() == ButtonType.OK){
                try{
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("SELECT * FROM item_raw_material WHERE RM_ID = '"+materialSelected.getMaterialID()+"'");
                    resultSet = statement.executeQuery();
                    //check if there is any item is using this raw material
                    //user cannot delete a raw material that is used by an item
                    if(resultSet.next()){
                        Alert alertItem = new Alert(Alert.AlertType.ERROR);
                        alertItem.setTitle("Delete Raw Material");
                        alertItem.setHeaderText("ERROR, There are item using this material - "+materialSelected.getMaterialID()+"!");
                        alertItem.setContentText("Please ensure that no item is dependent on this material first");
                        alertItem.show();
                    }else{
                        //delete record in supplier_rm table
                        statement = connection.prepareStatement("DELETE FROM supplier_rm WHERE RM_ID = '"+materialSelected.getMaterialID()+"';");
                        statement.execute();
                        //update material's status
                        statement = connection.prepareStatement("UPDATE raw_material SET Material_status = 'Closed'" +
                                                                    " WHERE RM_ID = '"+materialSelected.getMaterialID()+"';");
                        statement.execute();
                        loadTableData();
                        materialSelected = null;
                        alertInfo.setTitle("Delete Action");
                        alertInfo.setHeaderText("The raw material has been removed successfully");
                        alertInfo.show();
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }finally{
                    closeConnection();
                }
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Raw Material");
            alert.setHeaderText("You must select a raw material from table first!");
            alert.setContentText("please select a raw material");
            alert.show();
        }
    }
    //remove supplier from the table
    public void removeSupplier(){
        if(supplierSelected != null){
            searchSupplierList.remove(supplierSelected);
            searchSupplierTable.setItems(searchSupplierList);
            supplierSelected = null;
        }
    }
    //edit raw material details
    public void editRawMaterial(){
        if(inEditMode){
            inEditMode = false;
            clearField();
            return;
        }
        //avoid user from editing a deleted material
        if(materialSelected.getStatus().equals("Closed")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete raw material");
            alert.setHeaderText("This raw material's status is closed!");
            alert.setContentText("You cannot edit this raw material");
            alert.show();
            return;
        }
        if(materialSelected != null) {
            //fill in all the data into text field
            searchSupplierList.clear();
            editButton.setText("Cancel Edit");
            addButton.setText("Save");
            editModeLabel.setText("Editing " + materialSelected.getMaterialID());
            editRM_ID = materialSelected.getMaterialID();
            rmNameText.setText(materialSelected.getMaterialName());
            rmCostText.setText(String.valueOf(materialSelected.getMaterialCost()));
            inEditMode=true;

            try{
                //retrieve data from database
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT * FROM supplier WHERE Supplier_ID in " +
                                                            "(SELECT Supplier_ID FROM supplier_rm " +
                                                            "WHERE RM_ID = '"+ materialSelected.getMaterialID() +"'); ");
                resultSet = statement.executeQuery();
                while(resultSet.next()){
                    //add related supplier into table
                    Supplier sp = new Supplier(resultSet.getString("Supplier_ID"), resultSet.getString("Supplier_Name"));
                    searchSupplierList.add(sp);
                }
                searchSupplierTable.setItems(searchSupplierList);
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                closeConnection();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete raw material");
            alert.setHeaderText("Please select a raw material to edit");
            alert.show();
        }
    }

    //update raw material record
    public void updateRawMaterial(){
        try{
            //delete record in supplier_rm table
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("DELETE FROM supplier_rm WHERE RM_ID = '"+editRM_ID+"';");
            statement.execute();
            //insert new record into supplier_rm table
            for (Supplier sp : searchSupplierList) {
                statement = connection.prepareStatement("INSERT INTO supplier_rm VALUES ('"+ editRM_ID + "', "+
                        "'" + sp.getSupplierID() + "'); ");
                statement.execute();
            }
            //update record
            statement = connection.prepareStatement("UPDATE raw_material SET Raw_material_name = ?, Material_cost_per_gram = ? WHERE RM_ID = ?");
            statement.setString(1, rmNameText.getText());
            statement.setFloat(2, Float.parseFloat(rmCostText.getText()));
            statement.setString(3, editRM_ID);
            statement.execute();
            loadTableData();
            clearField();
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    //check user input
    public boolean validateMaterialInfo(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Add New Raw Material");
        if(searchSupplierList.isEmpty()){
            alert.setHeaderText("Error, no supplier selected for the raw material");
            alert.setContentText("You must have at least one supplier that provide the supply");
            alert.show();
            return false;
        }else if(rmNameText.getText().isEmpty() || rmCostText.getText().isEmpty()){
            alert.setHeaderText("You must fill in all the required field!");
            alert.setContentText("");
            alert.show();
            return false;
        }else if(!publicMethod.checkName(rmNameText.getText())){
            alert.setHeaderText("Error, invalid raw material name!");
            alert.setContentText("");
            alert.show();
            return false;
        }else if(rmNameText.getText().length() > 50){
            alert.setHeaderText("Error, length of material name exceed 50 character!");
            alert.setContentText("");
            alert.show();
            return false;
        }else if(!publicMethod.checkFloat(rmCostText.getText())){
            alert.setHeaderText("Error, invalid raw material cost");
            alert.setContentText("");
            alert.show();
            return false;
        }else if(Float.parseFloat(rmCostText.getText()) <= 0 || Float.parseFloat(rmCostText.getText()) > 9999){
            alert.setHeaderText("Error, invalid material cost");
            alert.setContentText("Please set the cost in range between 0.1 - 9999");
            alert.show();
            return false;
        }else if(rmCostText.getText().indexOf('.') != -1 &&(rmCostText.getText().length() - rmCostText.getText().indexOf('.') - 1) > 3){
            alert.setHeaderText("Error, invalid raw material cost!");
            alert.setContentText("Please keep the cost with no more than 3 decimal");
            alert.show();
            return false;
        }

        return true;
    }



}
