package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To add operation for an item in bill of material
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.Operation;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class selectOperationController implements Initializable {

    @FXML
    private TableView<Operation> selectOperationTable;

    @FXML
    private TableColumn<Operation, String> opCodeCol;

    @FXML
    private TableColumn<Operation, String > opNameCol;

    @FXML
    private TableColumn<Operation, Float> opCostCol;

    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    @FXML
    private TextField opTimeText;

    @FXML
    private Label opTimeLabel;

    private Operation operationSelected = null;

    private ObservableList<Operation> operations = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table
        opCodeCol.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationCode"));
        opNameCol.setCellValueFactory(new PropertyValueFactory<Operation, String>("operationName"));
        opCostCol.setCellValueFactory(new PropertyValueFactory<Operation, Float>("operationCost"));
        loadTable();
    }
    //get selected row in table
    public void getTableClick(){
        operationSelected = selectOperationTable.getSelectionModel().getSelectedItem();
    }

    //load data into table
    public void loadTable(){
        operations.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("select * from operation");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                Boolean operationNotExist = true;

                Operation operation = new Operation(resultSet.getString("OP_code"),
                                                    resultSet.getString("OP_name"),
                                                    resultSet.getFloat("Cost_per_hour"));
                //filter the operation that has added to the table
                for (Operation operationAdded : billOfMaterialController.operationList) {
                    if (operationAdded.getOperationCode().equals(operation.getOperationCode())){
                        operationNotExist = false;
                        break;
                    }
                }
                if(operationNotExist){
                    operations.add(operation);
                }
            }
            selectOperationTable.setItems(operations);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //close database connection
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
    //check user input and add operation to the table
    public void addOperationNeeded(){
        if(opTimeText.getText().isEmpty()){
            opTimeLabel.setText("You must fill in the operation time");
            return;
        }
        else if(!checkNumeric(opTimeText.getText())){
            opTimeLabel.setText("Invalid input!, please enter a number");
            return;
        }
        else if(Long.parseLong(opTimeText.getText()) > 999 || Long.parseLong(opTimeText.getText()) < 1) {
            opTimeLabel.setText("Please enter the time in range between 1 - 999");
            return;
        }
        if(operationSelected == null){
            opTimeLabel.setText("You must select a operation from the table!");
        }else{
            operationSelected.setOperationTime(Integer.parseInt(opTimeText.getText()));
            billOfMaterialController.operationList.add(operationSelected);
            cancel();
        }
    }
    //close the window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }

    //check the input whether is a string or numeric
    public boolean checkNumeric(String str){
        try {
            if(Long.parseLong(str) < 0){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

}
