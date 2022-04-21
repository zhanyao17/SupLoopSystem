package controller.productionDepartment;

//Programmer Name : Lin Zhan Yao TP061252
//Program Name: showMaterialRequired.java
//Description: Showing Additional Information Of The Job Card
//First Written on: 5 April 2022
//Edited on: 15 April 2022

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


import base.manufactureDepartment.ArrayList.rawMaterialQuantityArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class showMaterialRequired implements Initializable
{
    // Label
    @FXML private Label jobCardId;
    @FXML private Label rQuantity;
    @FXML private Label noticeLabel;
    @FXML private Label addInformation;



    // Anchor Pane
    @FXML private AnchorPane scenePane;

    // Table Column & View
    @FXML private TableColumn<rawMaterialQuantityArray, String> warehouseIdColumn;
    @FXML private TableColumn<rawMaterialQuantityArray, String> rmIdColumn;
    @FXML private TableColumn<rawMaterialQuantityArray, String> matQuantityColumn;

    @FXML private TableView<rawMaterialQuantityArray> tableView;


    // define variables
    private Stage stage;
    private ObservableList<rawMaterialQuantityArray> emptyList;

    // define class
    Manufacture_Main jobCardData = new Manufacture_Main();



    /****************************************************  Cancel <Mehtods>   *****************************************************/  
    public void Cancel()
    {
        overallJobCardPreview.inUpdateStatusMode = false;
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }



    /*************************************************  Back to Previous <Action>   *************************************************/

    public void backToPrevious(ActionEvent event) throws IOException 
    {
        Cancel();    
    }

    /***************************************** Get data and Set Table View Itmes <Mehtods>   ****************************************/  

    public void previewJobCardDetails(String jId, String oI, String rQ, String opCode) 
    {
        jobCardId.setText(jId);
        rQuantity.setText(rQ+" QTY");


        // Prepare Statement for Finding Required Raw Material
        String jobCardRawMaterial = "SELECT jc.JC_ID, ior.RM_ID "+
                                    "FROM `item_operation_rm` ior "+
                                    "INNER JOIN `sales_order` so on so.Item_code = ior.Item_code "+
                                    "INNER JOIN `job_card` jc on jc.OP_code = ior.OP_code "+
                                    "WHERE so.Order_ID = "+"'"+oI+"'"+" AND jc.JC_ID = "+"'"+jId+"'"+" ;";

        String rawMaterialQuantity =    "SELECT pr.RM_ID, w.material_quantity, w.warehouse_label, w.Warehouse_ID "+
                                        "FROM `warehouse` w "+
                                        "INNER JOIN `purchase_warehouse` pw on pw.Warehouse_ID = w.Warehouse_ID "+
                                        "INNER JOIN `purchase_order` po on po.Purchase_ID = pw.Purchase_ID "+
                                        "INNER JOIN `purchase_rm` pr on pr.Purchase_ID = po.Purchase_ID "+
                                        "WHERE po.Order_ID = "+"'"+oI+"'"+";";
    
        
        if (!jobCardData.isReady(rawMaterialQuantity,"NR")) {
            if (jobCardData.isRawMaterialNull(jobCardRawMaterial)) {
                noticeLabel.setText("*** RAW MATERIAL IS NOT READY YET ***");
                addInformation.setText("This Job Card Did Not Required Any Material !!");
                tableView.setItems(emptyList);
            } else {
                jobCardData.showRequiredRmQuantity(jobCardRawMaterial, rawMaterialQuantity);
                noticeLabel.setText("*** RAW MATERIAL IS NOT READY YET ***");
                addInformation.setText("Real-Time Stock Quantity Shown Above !!");
                tableView.setItems(jobCardData.getRawMaterialList());
            }
        } else {
            if (jobCardData.isRawMaterialNull(jobCardRawMaterial)) {
                noticeLabel.setText("*** THIS JOB CARD DID NOT REQUIRED ANY MATERIALS ***");
                tableView.setItems(emptyList);
            } else {
                jobCardData.showRequiredRmQuantity(jobCardRawMaterial, rawMaterialQuantity);
                noticeLabel.setText("*** REQUIRED MATERIAL FOR THIS JOB CARD ***");
                tableView.setItems(jobCardData.getRawMaterialList());
            }
        }
    
        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }


    /***********************************************  Default Paremeters <Overwrite>   ***********************************************/

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Wa_Id"));
        rmIdColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Rm_Id"));
        matQuantityColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Mat_Quantity"));        

        addInformation.setText("");
    }
}
