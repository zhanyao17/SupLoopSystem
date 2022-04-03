package controller.recycleDepartment;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import base.recycleDepartment.ArrayList.recycledRMArray;
import base.recycleDepartment.Methods.recycle_Main;
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

    // Table view & Column
    @FXML private TableColumn<recycledRMArray,String> rmIdColumn;
    @FXML private TableColumn<recycledRMArray,String> rmNameColumn;
    @FXML private TableColumn<recycledRMArray,String> materialQColumn;

    @FXML private TableView <recycledRMArray> tableView;


    // create class
    recycle_Main rRawmaterial = new recycle_Main();

    // Statement
    private String rawMaterialQuery = "SELECT rw.RM_ID, rw.Raw_material_name from raw_material rw;";
    private String recycleMaterialQuery = "SELECT recycle_material_ID from recycle_material;";
    private String warehouseQuery = "SELECT Warehouse_ID from warehouse;";

    /***************************************************  Add Item to Table View Button <Action>  *************************************************/  // 2 APRIL
    
    public void addToTable(ActionEvent event) throws IOException
    {
        resetTableButton.setDisable(false);
        alertLabel.setText("");
        try {
            if ((rRawmaterial.isString(enterQuantity.getText()))||(enterQuantity.getText()==null)) {
                enterQuantity.setText("");
                rmChoicesBox.setValue("");
                alertLabel.setText("Invalid Input !!");
            } else {
                recycledRMArray newRm = new  recycledRMArray(rRawmaterial.getRmId(rmChoicesBox.getValue(), 
                                        rawMaterialQuery),rmChoicesBox.getValue(), enterQuantity.getText());
            
                tableView.getItems().add(newRm);
                enterQuantity.setText("");
                rmChoicesBox.setValue("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***************************************************  Transfer Material to Warehouse Button <Action>  *************************************************/  // 2 APRIL
    public void transferMaterial(ActionEvent event) throws SQLException
    {
        // if table view == null - take action
        // else cap all value
        int countRYId = rRawmaterial.generateRecycleMaterialID(recycleMaterialQuery); // Recycle Mat id
        int countWarId = rRawmaterial.generateWarehouseId(warehouseQuery); // warehouseId
        if (tableView.getItems().isEmpty()) 
        {
            alertLabel.setText("Please Add Raw Material To Table before Transfer Material !!");
            // null value transfer will be capture here
        }else{
            for (recycledRMArray col : tableView.getItems()) 
        {
            if (col == null) 
            {
                alertLabel.setText("Please Add Raw Material To Table before Transfer Material !!");
                break;    
            }
            
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
        alertLabel.setText("*** Raw Material Has Been Successfully Transfer To Warehouse ***");
        tableView.getItems().clear();
        }

    }

    /***************************************************  Reset Table Button <Action>  *************************************************/  // 2 APRIL
    public void resetTable(ActionEvent event) throws IOException 
    {
        // Clear textfield & choicesBox
        enterQuantity.setText("");
        rmChoicesBox.setValue("");

        // Clear table view
        tableView.getItems().clear();
        
        // disable resetTableButton
        resetTableButton.setDisable(true);
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
