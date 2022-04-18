package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To add raw material for an item in bill of material
//First Written on: 1 April 2022
//Edited on: 18 April 2022

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.RawMaterial;
import base.salesDepartment.Method.publicMethod;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static controller.salesDepartment.billOfMaterialController.operationList;

public class selectMaterialController implements Initializable {
    @FXML
    private TableView<RawMaterial> selectMaterialTable;

    @FXML
    private TableColumn<RawMaterial, Long> materialCost;

    @FXML
    private TableColumn<RawMaterial, String> materialIDCol;

    @FXML
    private TableColumn<RawMaterial, String> materialNameCol;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private TextField materialQuantityText;

    @FXML
    private ChoiceBox<String> RM_OperationChoice;

    @FXML
    private Label materialErrorLabel;

    private Stage stage;

    private String selectedChoice;

    private RawMaterial materialSelected = null;

    private ObservableList<RawMaterial> materials = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table
        materialIDCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialID"));
        materialNameCol.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("materialName"));
        materialCost.setCellValueFactory(new PropertyValueFactory<RawMaterial, Long>("materialCost"));

        //setup combo box
        String[] operationChoice = new String[operationList.toArray().length];
        for (int i = 0; i < operationList.toArray().length; i++) {
            operationChoice[i] = operationList.get(i).getOperationCode();
        }
        RM_OperationChoice.getItems().addAll(operationChoice);
        RM_OperationChoice.setOnAction(this::getChoice);
        loadTable();
    }
    //get combo box value
    private void getChoice(ActionEvent event) {
        selectedChoice = RM_OperationChoice.getValue();
    }

    //get selected row in table
    public void getTableClick(){
        materialSelected = selectMaterialTable.getSelectionModel().getSelectedItem();
    }

    //load data into table
    public void loadTable(){
        materials.clear();
        try {
            //retrieve data from database
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("select * from raw_material WHERE Material_status = 'Active'");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                Boolean rawMaterialNotExist = true;
                //create line of string for each record
                RawMaterial rawMaterial = new RawMaterial(resultSet.getString("RM_ID"),
                                                          resultSet.getString("Raw_material_name"),
                                                          resultSet.getFloat("Material_cost_per_gram"),
                                                          resultSet.getString("Material_status"));
                //filter out the record that has added to the table
                for (RawMaterial materialAdded : billOfMaterialController.rawMaterialList) {
                    if (materialAdded.getMaterialID().equals(rawMaterial.getMaterialID())){
                        rawMaterialNotExist = false;
                        break;
                    }
                }
                if(rawMaterialNotExist){
                    materials.add(rawMaterial);
                }
            }
            selectMaterialTable.setItems(materials);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //close connection to the database
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

    //check user input and add raw material to table
    public void addMaterialNeeded(){
        if(materialQuantityText.getText().isEmpty()){
            materialErrorLabel.setText("Please fill in the material quantity in gram");
        }
        else if(Float.parseFloat(materialQuantityText.getText()) <=0 || Float.parseFloat(materialQuantityText.getText()) > 99999){
            materialErrorLabel.setText("Please set the quantity in range between 0.1 - 99999");
        }
        else if(!publicMethod.checkFloat(materialQuantityText.getText())) {
            materialErrorLabel.setText("Invalid input! Please enter a number");
        }
        else if(materialSelected == null){
            materialErrorLabel.setText("You must select a raw material from the table!");
        }else {
            materialSelected.setMaterialQuantity(Float.parseFloat(materialQuantityText.getText()));
            materialSelected.setMaterialOperation(selectedChoice);
            billOfMaterialController.rawMaterialList.add(materialSelected);
            cancel();
        }
    }
    //close window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }

}
