package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To manage all the product in database
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.Item;
import base.salesDepartment.ArrayLists.Operation;
import base.salesDepartment.ArrayLists.RawMaterial;
import controller.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;


public class productManagementController extends dashboardController implements Initializable {

    @FXML
    private TableView<Item> itemTable;

    @FXML
    private TableColumn<Item, String> itemCodeCol;

    @FXML
    private TableColumn<Item, String> itemNameCol;

    @FXML
    private TableColumn<Item, Float> itemPriceCol;

    @FXML
    private TableColumn<Item, ImageView> itemImageCol;

    @FXML
    private TableColumn<Item, String> itemStatusCol;

    @FXML
    private TextField searchItemText;

    @FXML
    private CheckBox showAllCheckBox;

    private String filterStatus = "'Active'";

    private InputStream inputStream;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    public Item itemSelected = null;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private ResultSet resultSet2 = null;

    Main m = new Main();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemCode"));
        itemNameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemName"));
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<Item, Float>("itemPrice"));
        try{
            itemStatusCol.setCellValueFactory(new PropertyValueFactory<Item, String>("status"));
            itemImageCol.setCellValueFactory(new PropertyValueFactory<Item, ImageView>("itemImage"));
        }catch (Exception e){

        }
        itemImageCol.setStyle("-fx-alignment: CENTER;");

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
        itemSelected = itemTable.getSelectionModel().getSelectedItem();
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
    public void loadTableData() {
        itemList.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("select * from item WHERE Item_status IN ("+filterStatus+")");
            resultSet = statement.executeQuery();

            while (resultSet.next()){
                //create line of string for each item record
                String itemData = resultSet.getString("Item_code") + "\t" +
                                  resultSet.getString("Item_name")+ "\t" +
                                  resultSet.getString("Item_per_price")+ "\t" +
                                  resultSet.getString("Item_status");

                //filter item record that do not contain the word in search bar
                if(itemData.toLowerCase(Locale.ROOT).contains(searchItemText.getText().toLowerCase(Locale.ROOT))) {
                    Item item = new Item(resultSet.getString("Item_code"),
                            resultSet.getString("Item_name"),
                            resultSet.getFloat("Item_per_price"),
                            resultSet.getString("Item_status"));

                    Blob blob = resultSet.getBlob("Image");
                    inputStream = blob.getBinaryStream();
                    ImageView image = new ImageView(new Image(inputStream));
                    image.setPreserveRatio(true);
                    image.setFitHeight(150);
                    item.setItemImage(image);
                    itemList.add(item);
                }
            }
            itemTable.setItems(itemList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //switch to bill of material view
    public void addProduct(){
        try {
            m.switchScene("/fxml/salesDepartment/billOfMaterial.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //edit product details
    public void editProduct(){
        if(itemSelected != null){
            //avoid admin from editing a deleted product
            if(itemSelected.getStatus().equals("Closed")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Edit");
                alert.setHeaderText("This item has been deleted!");
                alert.setContentText("You cannot edit this item");
                alert.show();
                return;
            }
            try {
                connection = new DBConnectors().getConnection();
                //populate select operation table in bill of material scene based on the item code
                statement = connection.prepareStatement("SELECT *" +
                        "FROM operation o INNER JOIN item_operation i ON o.OP_code = i.OP_code " +
                        "WHERE i.Item_code = '"+ itemSelected.getItemCode()+"';");
                resultSet = statement.executeQuery();

                while (resultSet.next()){
                    Operation op = new Operation(resultSet.getString("OP_code"),
                            resultSet.getString("OP_name"),
                            resultSet.getFloat("Cost_per_hour"),
                            resultSet.getInt("OP_time"));

                    billOfMaterialController.operationList.add(op);
                }

                //populate select raw material table in bill of material scene based on the item code
                statement = connection.prepareStatement("SELECT DISTINCT * " +
                        "FROM item i INNER JOIN item_raw_material ir on i.Item_code = ir.Item_code " +
                        "INNER JOIN raw_material rm on ir.RM_ID = rm.RM_ID " +
                        "WHERE i.Item_code = '" + itemSelected.getItemCode()+"' ORDER BY rm.RM_ID;");
                resultSet = statement.executeQuery();

                statement = connection.prepareStatement("SELECT * FROM item_operation_rm WHERE Item_code = '" +
                        itemSelected.getItemCode()+ "' AND RM_ID is not null ORDER BY RM_ID;");
                resultSet2 = statement.executeQuery();

                while(resultSet.next()){
                    resultSet2.next();
                    RawMaterial rm = new RawMaterial(resultSet.getString("RM_ID"),
                            resultSet.getString("Raw_material_name"),
                            resultSet.getFloat("material_quantity"),
                            resultSet.getFloat("Material_cost_per_gram"),
                            resultSet2.getString("OP_code"));

                    billOfMaterialController.rawMaterialList.add(rm);
                }
                //pass data too bill of material scene
                billOfMaterialController.passItem = itemSelected;
                billOfMaterialController.productEditMode = true;
                closeConnection();
                addProduct();


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Edit product");
            alert.setHeaderText("Please select a item in table to edit!");
        }
    }
    //delete item record in database
    public void deleteItem(){
        Alert alertError = new Alert(Alert.AlertType.ERROR);
        if(itemSelected!=null){
            //show confirmation message
            Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            alertConfirm.setTitle("Delete Item");
            alertConfirm.setHeaderText("You are going to delete Item - "+itemSelected.getItemCode());
            alertConfirm.setContentText("Are you sure?");
            if(alertConfirm.showAndWait().get() == ButtonType.OK){
                //check the item status
                if(itemSelected.getStatus().equals("Closed")){
                    alertError.setHeaderText("This item has been deleted");
                    alertError.setContentText("You cannot delete this item again!");
                    alertError.show();
                    return;
                }
                try{
                    //retrieve data from database
                    connection = new DBConnectors().getConnection();
                    statement = connection.prepareStatement("SELECT * FROM sales_order WHERE Item_code = ?");
                    statement.setString(1,itemSelected.getItemCode());
                    resultSet = statement.executeQuery();
                    while(resultSet.next()){
                        //check if this item has been ordered by a customer
                        LocalDate orderDate = LocalDate.parse(resultSet.getString("Due_date"));
                        if(orderDate.isAfter(LocalDate.now())){
                            alertError.setHeaderText("Item - "+itemSelected.getItemCode()+" cannnot be deleted!");
                            alertError.setContentText("This item is ordered by a customer!");
                            alertError.show();
                            return;
                        }
                    }
                    //delete all related data in each table
                    statement = connection.prepareStatement("DELETE FROM item_operation WHERE Item_code = ?");
                    statement.setString(1,itemSelected.getItemCode());
                    statement.execute();
                    statement = connection.prepareStatement("DELETE FROM item_operation_rm WHERE Item_code = ?");
                    statement.setString(1,itemSelected.getItemCode());
                    statement.execute();
                    statement = connection.prepareStatement("DELETE FROM item_raw_material WHERE Item_code = ?");
                    statement.setString(1,itemSelected.getItemCode());
                    statement.execute();
                    //update item status to "Closed"
                    statement = connection.prepareStatement("UPDATE item SET Item_status = 'Closed' WHERE Item_code = ?");
                    statement.setString(1,itemSelected.getItemCode());
                    statement.execute();

                    Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                    alertInfo.setTitle("Delete Item");
                    alertInfo.setHeaderText("Item deleted successfully!");
                    alertInfo.show();
                    loadTableData();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally{
                    closeConnection();
                }
            }else{

            }
        }else{
            alertError.setHeaderText("No item selected!");
            alertError.setContentText("Please select an item from the table");
            alertError.show();
        }
    }
}
