package controller.recycleDepartment;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import base.recycleDepartment.ArrayList.recycledRMArray;
import base.recycleDepartment.Methods.recycle_Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;

public class materialTransfer_recycle implements Initializable
{


    // textField
    @FXML private TextField enterQuantity;

    // choices box
    @FXML private ChoiceBox<String> rmChoicesBox;

    // label
    @FXML private Label alertLabel;

    // Button
    @FXML private Button resetTableButton;
    @FXML private Button addToTableButton;
    @FXML private Button transferMatButton;

    // Table view & Column
    @FXML private TableColumn<recycledRMArray,String> rmIdColumn;
    @FXML private TableColumn<recycledRMArray,String> rmNameColumn;
    @FXML private TableColumn<recycledRMArray,String> materialQColumn;

    @FXML private TableView <recycledRMArray> tableView;


    // Menu Bar
    @FXML private Pane logOutButton;

    // create class
    recycle_Main rRawmaterial = new recycle_Main();
    
    // drop shawdow effect
    DropShadow shadow = new DropShadow();


    // Statement
    private String rawMaterialQuery = "SELECT rw.RM_ID, rw.Raw_material_name from raw_material rw;";
    private String recycleMaterialQuery = "SELECT recycle_material_ID, RM_ID from recycle_material;";
    private String warehouseQuery = "SELECT Warehouse_ID, recycle_material_ID , material_quantity from warehouse;";
    private boolean isRepeat=false;
    
    // define observable list
    private ObservableList <recycledRMArray> newRM =  FXCollections.observableArrayList();;

    /***************************************************  Detect duplicate <Methods>  *************************************************/  // 3 APRIL
    public void checkDuplicates() throws SQLException
    {  
        float capQ = Float.parseFloat(enterQuantity.getText());
        for (recycledRMArray col : newRM) 
        {
            if (col.getRmId().equals((rRawmaterial.getRmId(rmChoicesBox.getValue(), rawMaterialQuery)))) 
            {
                capQ += Float.parseFloat(col.getMaterial_quantity());
                newRM.add(new recycledRMArray(col.getRmId(), col.getRmName(), String.valueOf(capQ)));
                newRM.remove(col);
                isRepeat = true;
                break;
            }    
        }
    }
    /***************************************************  Add Item to Table View Button <Action>  *************************************************/  // 2 APRIL
    
    public void addToTable(ActionEvent event) throws IOException
    {
        resetTableButton.setDisable(false);
        alertLabel.setText("");
        try {
            if ((rRawmaterial.isString(enterQuantity.getText()))||(enterQuantity.getText()==null)||(rmChoicesBox.getValue()==null)) {
                enterQuantity.setText("");
                rmChoicesBox.setValue("");
                alertLabel.setText("Invalid Input !!");
            } else {
                checkDuplicates();
                if (!isRepeat) // if there is no repeat (Add new item into it)
                {
                    newRM.add(new recycledRMArray(rRawmaterial.getRmId(rmChoicesBox.getValue(), rawMaterialQuery), rmChoicesBox.getValue(), enterQuantity.getText()));   
                }
                isRepeat = false; // reset boolean
            }
            tableView.setItems(newRM);
            enterQuantity.setText("");
            rmChoicesBox.setValue("");
            // alertLabel.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************  Transfer Material to Warehouse Button <Action>  *************************************************/  // 2 APRIL
    public void transferMaterial(ActionEvent event) throws SQLException
    {
        int countRYId = rRawmaterial.generateRecycleMaterialID(recycleMaterialQuery); // Recycle Mat id
        int countWarId = rRawmaterial.generateWarehouseId(warehouseQuery); // warehouseId
        
        if (tableView.getItems().isEmpty()) // null value transfer will be capture here
        {
            alertLabel.setText("Please Add Raw Material To Table before Transfer Material !!");
        }else{
            for (recycledRMArray col : tableView.getItems()) 
        {
            if (col == null) 
            {
                alertLabel.setText("Please Add Raw Material To Table before Transfer Material !!");
                break;    
            }
            
            // check is there any raw material repeated
            String recycleId =  rRawmaterial.getRepeatRM(col.getRmId(), recycleMaterialQuery);
            if (rRawmaterial.getIsRepeat()) // if Recycle id is repeated
            {
                String exMatQ =  rRawmaterial.getExistingQuantity(recycleId, warehouseQuery);
                float newQuantity = Float.parseFloat(exMatQ) + Float.parseFloat(col.getMaterial_quantity());
                String upWarehouseQuery = "UPDATE warehouse SET material_quantity = "+"'"+String.valueOf(newQuantity)+"'" +" WHERE recycle_material_ID = "+"'"+recycleId+"'";
                rRawmaterial.insertData(upWarehouseQuery);
            }else
            {
                // Adding counter for keys
                countRYId++;
                countWarId++;

                String newRyId = "ry"+Integer.toString(countRYId);
                String newWarId = "W"+Integer.toString(countWarId);

                // Define insert Statement
                String upRecycleMatQuery = "INSERT into recycle_material VALUES ( "+"'"+newRyId+"' ,"+"'"+col.getRmId()+"' );";
                String upWarehouseQuery = "INSERT INTO warehouse VALUES ("+"'"+newWarId+"', "+"'"+col.getMaterial_quantity()+"',"+"'NA'"+",'"+newRyId+"') ;"; // label as NA - Not assign
                
                rRawmaterial.insertData(upRecycleMatQuery);
                rRawmaterial.insertData(upWarehouseQuery);
            }
        }
        alertLabel.setText("*** Raw Material Has Been Successfully Transfer To Warehouse ***");
        tableView.getItems().clear();
        newRM.clear();
        }
    }

    /***************************************************  Reset Table Button <Action>  *************************************************/  // 2 APRIL
    public void resetTable(ActionEvent event) throws IOException 
    {
        // Clear textfield & choicesBox
        enterQuantity.setText("");
        rmChoicesBox.setValue("");

        // reset label
        alertLabel.setText("");

        // Clear table view
        tableView.getItems().clear();
        
        // disable resetTableButton
        resetTableButton.setDisable(true);
    }


    /***************************************************  Menu bar effect Button <Action>  *************************************************/  // 2 APRIL
    // logout button entered
    public void logOutBarEnter() 
    {
        logOutButton.setStyle("-fx-background-color: #3d454d");   
    }
    
    // logout button entered
    public void logOutBarExited() 
    {
        logOutButton.setStyle("-fx-background-color: #4b555e");      
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
        // invisible label
        alertLabel.setText("");

        // disable resetTable button
        resetTableButton.setDisable(true);

        // define raw material name in choices box
        try {
            rmChoicesBox.getItems().addAll(rRawmaterial.getRMNameList(rawMaterialQuery));    
        } catch (Exception e) {
            e.printStackTrace();
        }

        rmIdColumn.setCellValueFactory(new PropertyValueFactory<recycledRMArray,String>("rmId"));
        rmNameColumn.setCellValueFactory(new PropertyValueFactory<recycledRMArray,String>("rmName"));
        materialQColumn.setCellValueFactory(new PropertyValueFactory<recycledRMArray,String>("material_quantity"));


    }
}
