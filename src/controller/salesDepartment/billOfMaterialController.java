package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: billOfMaterialController.java
//Description: To allow user from providing a bill of material for an item
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class billOfMaterialController implements Initializable {

    @FXML
    private TableView<RawMaterial> materialTable;

    @FXML
    private TableView<Operation> operationTable;

    @FXML
    private TableColumn<RawMaterial, String> materialIDCol;

    @FXML
    private TableColumn<RawMaterial, String> materialNameCol;

    @FXML
    private TableColumn<RawMaterial, Float> materialQuantityCol;

    @FXML
    private TableColumn<RawMaterial, Float> materialCostCol;

    @FXML
    private TableColumn<RawMaterial, String> rmOperationCol;

    @FXML
    private TableColumn<Operation, String> opCodeCol;

    @FXML
    private TableColumn<Operation, String > opNameCol;

    @FXML
    private TableColumn<Operation, Float> opCostCol;

    @FXML
    private TableColumn<Operation, Integer> opTimeCol;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField itemNameText, itemPriceText, rmCostText, operatingCostText, totalCostText;

    private File sourceFile;

    private InputStream fileInputStream;

    private Stage stage;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private ImageView itemImage;

    private Image image;

    private RawMaterial rawMaterialSelected = null;

    private Operation operationSelected = null;

    private String itemCode = "i0001";

    public static Item passItem = null;

    public static boolean productEditMode = false;

    public static ObservableList<Operation> operationList = FXCollections.observableArrayList();

    public static ObservableList<RawMaterial> rawMaterialList = FXCollections.observableArrayList();

    private boolean selectOperationMode = false, selectMaterialMode = false;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        materialIDCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialID"));
        materialNameCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialName"));
        materialQuantityCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, Float>("materialQuantity"));
        materialCostCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, Float>("materialCost"));
        rmOperationCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialOperation"));

        opCodeCol.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationCode"));
        opNameCol.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationName"));
        opCostCol.setCellValueFactory(new PropertyValueFactory<Operation, Float>("operationCost"));
        opTimeCol.setCellValueFactory(new PropertyValueFactory<Operation, Integer>("operationTime"));

        //fill in item's data to all field
        if(productEditMode){
            titleLabel.setText("Bill of Material - "+ passItem.getItemCode());
            itemPriceText.setText(String.valueOf(passItem.getItemPrice()));
            itemNameText.setText(passItem.getItemName());
            materialTable.setItems(rawMaterialList);
            operationTable.setItems(operationList);
            calculateCost();

            //display item's image
            try{
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT * FROM item WHERE Item_code = '"+passItem.getItemCode()+"'");
                resultSet = statement.executeQuery();
                if(resultSet.next()){
                    Blob blob = resultSet.getBlob("Image");
                    fileInputStream = blob.getBinaryStream();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                closeConnection();
            }
            itemImage.setImage(new Image(fileInputStream));
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

    //get selected material row in table
    public void getMaterialTableClick(){
        try{
            rawMaterialSelected = materialTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){

        }
    }

    //get selected operation row in table
    public void getOperationTableClick(){
        try{
            operationSelected = operationTable.getSelectionModel().getSelectedItem();
        }catch(Exception e){

        }
    }

    //pop out new window to add material
    public void addMaterial(){
        //to alert user to add operation first before adding raw material
        if(operationList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Empty operation");
            alert.setHeaderText("You must have at least one operation before adding raw material!");
            alert.setContentText("Please add operation at table above");
            alert.show();
            return;
        }
        try {
            if(!selectMaterialMode){
                //get resource from FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/selectMaterial.fxml"));
                Parent selectMaterialRoot = loader.load();

                selectMaterialMode = true;

                //setup window
                Stage stageMaterial = new Stage();
                Scene sceneMaterial = new Scene(selectMaterialRoot);
                stageMaterial.setScene(sceneMaterial);
                stageMaterial.setResizable(false);
                stageMaterial.setAlwaysOnTop(true);
                stageMaterial.showAndWait();
                selectMaterialMode = false;
                if(!rawMaterialList.isEmpty()){
                    materialTable.setItems(rawMaterialList);
                }
                calculateCost();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //pop out new window to add operation
    public void addOperation(){
        try {
            if(!selectOperationMode){
                //get resource from FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salesDepartment/selectOperation.fxml"));
                Parent selectOperationRoot = loader.load();

                selectOperationMode = true;
                //setup window
                Stage stageOperation = new Stage();
                Scene sceneOperation = new Scene(selectOperationRoot);
                stageOperation.setScene(sceneOperation);
                stageOperation.setResizable(false);
                stageOperation.setAlwaysOnTop(true);
                stageOperation.showAndWait();
                selectOperationMode = false;

                if(!operationList.isEmpty()){
                    operationList.sort(Operation::compareTo);
                    operationTable.setItems(operationList);
                }

                calculateCost();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //remove selected raw material in table
    public void deleteRawMaterial(){
        if(rawMaterialSelected != null){
            rawMaterialList.remove(rawMaterialSelected);
            materialTable.setItems(rawMaterialList);
            rawMaterialSelected = null;
        }else{
            //alert user to select a row in table
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Raw Material");
            alert.setHeaderText("You must select a raw material from the table First!");
            alert.show();
        }
    }

    //remove selected raw material in table
    public void deleteOperation(){
        if(operationSelected != null){
            //remove all the raw material from the table that has the operation selected
            rawMaterialList.removeIf(rm -> rm.getMaterialOperation().equals(operationSelected.getOperationCode()));
            materialTable.setItems(rawMaterialList);
            operationList.remove(operationSelected);
            operationTable.setItems(operationList);

            //notice user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Remove Operation");
            alert.setHeaderText("Notes: All raw material that belong to " + operationSelected.getOperationCode() + " will be removed");
            alert.setContentText("You may re-enter the data");
            alert.show();
            operationSelected = null;

        }else{
            //alert user to select a row in table
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Operation");
            alert.setHeaderText("You must select an operation from the table First!");
            alert.show();
        }
    }

    //calculate all cost required
    public void calculateCost(){
        float rmCost = 0, operatingCost = 0, total;
        //calculate raw material cost
        for (RawMaterial rawMaterial : rawMaterialList) {
            rmCost += rawMaterial.getMaterialCost() * rawMaterial.getMaterialQuantity();
        }
        //calculate operation cost
        for (Operation operation : operationList) {

            operatingCost += ((operation.getOperationCost()/60) * operation.getOperationTime())/2500;
        }
        //calculate total cost
        total = rmCost + operatingCost;

        rmCostText.setText(String.format("%.2f",rmCost));
        operatingCostText.setText(String.format("%.2f",operatingCost));
        totalCostText.setText(String.format("%.2f",total));
    }

    //save data into database
    public void saveBOM(){
        String query;
        if(!validateInput()){ //check user input
            return;
        }
        if(productEditMode){ //call other function if it is edit mode
            productEditMode = false;
            editBOM();
        }else{
            //check if the image is null
            if(sourceFile ==null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Please upload an image for the item");
                alert.show();
                return;
            }
            //save record in database
            try{
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM item");
                resultSet = statement.executeQuery();

                //get unique item code
                if(resultSet.next()){
                    itemCode = String.format("i%04d", (resultSet.getInt("Total")+1));
                }
                //insert data
                statement = connection.prepareStatement("INSERT INTO item VALUES (?,?,?,?,?)");
                statement.setString(1, itemCode);
                statement.setString(2, itemNameText.getText());
                statement.setFloat(3, Float.parseFloat(itemPriceText.getText()));
                fileInputStream = new FileInputStream(sourceFile);
                statement.setBinaryStream(4, fileInputStream, (int)sourceFile.length());
                statement.setString(5,"Active");
                statement.execute();

            }catch (SQLException | FileNotFoundException e){
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        try{
            connection = new DBConnectors().getConnection();
            for (RawMaterial rawMaterial : rawMaterialList) {
                //insert data into item_raw_material table
                query = "INSERT INTO item_raw_material VALUES (" + "'" + itemCode + "'," +
                                 "'" + rawMaterial.getMaterialID() + "'," +
                                 ""  + rawMaterial.getMaterialQuantity() + "); ";
                statement = connection.prepareStatement(query);
                statement.execute();
                //insert data into item_operation_rm table
                query = "INSERT INTO item_operation_rm VALUES (" + "'" + itemCode + "'," +
                                           "'" + rawMaterial.getMaterialOperation() + "'," +
                                           "'" + rawMaterial.getMaterialID() + "'); ";

                statement = connection.prepareStatement(query);
                statement.execute();
            }

            for (Operation operation : operationList) {
                //insert data into item_operation table
                query = "INSERT INTO item_operation VALUES (" + "'" + operation.getOperationCode() + "'," +
                                        "'" + itemCode + "'," +
                                        "" + operation.getOperationTime() + "); ";
                statement = connection.prepareStatement(query);
                statement.execute();

                //check if whether the operation use any raw material
                boolean opNotExist = true;
                for (int i = 0; i < rawMaterialList.toArray().length; i++) {
                    if (operation.getOperationCode().equals(rawMaterialList.get(i).getMaterialOperation())) {
                        opNotExist = false;
                    }
                }
                //set the raw material id column to null if no raw material needed
                if (opNotExist) {
                    query = " INSERT INTO item_operation_rm VALUES (" + "'" + itemCode + "'," +
                            "'" + operation.getOperationCode() + "'," + "null);";
                    statement = connection.prepareStatement(query);
                    statement.execute();
                }
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        } finally {
            closeConnection();
            cancel();

        }
    }

    //update existing item's bill of material
    public void editBOM(){
        try {
            itemCode = passItem.getItemCode();
            String query;
            connection = new DBConnectors().getConnection();

            //update query
            if(sourceFile != null){
                statement = connection.prepareStatement("UPDATE item SET Item_name = ?, Item_per_price = ?, Image = ? " +
                        "WHERE Item_code = ?");
                statement.setString(1,itemNameText.getText());
                statement.setFloat(2, Float.parseFloat(itemPriceText.getText()));
                fileInputStream = new FileInputStream(sourceFile);
                statement.setBinaryStream(3, fileInputStream, (int)sourceFile.length());
                statement.setString(4, passItem.getItemCode());
                
            }else{
                statement = connection.prepareStatement("UPDATE item SET Item_name = '" + itemNameText.getText() +"', Item_per_price = '" + itemPriceText.getText()+"' " +
                        "WHERE Item_code = '"+passItem.getItemCode()+"'");
            }

            statement.execute();

            //delete the all the data first and insert it back in saveBOM() method
            String[] tableName = {"item_operation","item_raw_material","item_operation_rm"};
            for (String table: tableName){
                query = "DELETE FROM "+table+" WHERE Item_code = '" + passItem.getItemCode() +"'; ";
                statement = connection.prepareStatement(query);
                statement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void cancel(){
        productEditMode=false;
        passItem =null;
        operationList.clear();
        rawMaterialList.clear();
        try {
            new Main().switchScene("/fxml/salesDepartment/productManagement.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check user input
    public boolean validateInput(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Item Operation");
        if(itemNameText.getText().isEmpty() || itemPriceText.getText().isEmpty()){
            alert.setHeaderText("You must fill in all the required field!");
            alert.show();
            return false;
        }
        else if(checkNumeric(itemNameText.getText()) || itemNameText.getText().charAt(0) == ' '){
            alert.setHeaderText("Invalid item name");
            alert.show();
            return false;
        }else if(!checkNumeric(itemPriceText.getText())){
            alert.setHeaderText("Invalid item price");
            alert.setContentText("Please avoid spacing, alphabet and special character");
            alert.show();
            return false;
        }else if(Float.parseFloat(itemPriceText.getText()) <= 0 || Float.parseFloat(itemPriceText.getText()) > 99999){
            alert.setHeaderText("Invalid item price");
            alert.setContentText("Please set the price in range between 1 - 99999");
            alert.show();
            return false;
        }else if(itemPriceText.getText().indexOf('.') != -1 &&(itemPriceText.getText().length() - itemPriceText.getText().indexOf('.') - 1) > 2){
            alert.setHeaderText("Invalid item price");
            alert.setContentText("Please set the price with no more than 2 decimal place");
            alert.show();
            return false;
        }else if(operationList.isEmpty()){
            alert.setHeaderText("You must have at least one operation!");
            alert.setContentText("Please add operation for the item!");
            alert.show();
            return false;
        }else if(rawMaterialList.isEmpty()){
            alert.setHeaderText("You must have at least one raw material added!");
            alert.setContentText("Please add raw material for the item!");
            alert.show();
            return false;
        }
        return true;
    }

    public boolean checkNumeric(String str){
        try {
            if(Long.parseLong(str) < 0){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            try{
                Float.parseFloat(str);
                return true;
            }catch (NumberFormatException e2){
                return false;
            }
        }
    }

    //upload item image
    public void uploadImage() throws IOException {
        stage = (Stage) scenePane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        

        //create file chooser
        sourceFile = fileChooser.showOpenDialog(stage);
        if(sourceFile != null){
            String sourcePath = sourceFile.getAbsolutePath();
            String extension = sourcePath.substring(sourcePath.lastIndexOf('.')+1);
            if(!(extension.equals("jpg") || extension.equals("png"))){  //limit file extension
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("You can only select files in .jpg or .png"); //alert user
                alert.show();
                sourceFile = null;
            }else{
                image = new Image("file:"+sourceFile.getAbsolutePath());
                itemImage.setImage(image);
            }
        }
    }

}
