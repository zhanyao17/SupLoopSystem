package controller.manufactureDepartment.ElectrodeProduction;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


import base.manufactureDepartment.ArrayList.rawMaterialQuantityArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class updateStartDate implements Initializable
{
    // Label
    @FXML private Label jobCardId;
    @FXML private Label rQuantity;
    @FXML private Label noticeLabel;
    @FXML private Label addInformation;

    // button
    @FXML private Button requestStartJobButton;

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
    private String jCardId;
    private String oderId;

    // define class
    Manufacture_Main jobCardData = new Manufacture_Main();



    /***************************************** Cancel <Mehtods> ****************************************/  
    public void Cancel()
    {
        JobCardPreview.inUpdateStatusMode = false;
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }


    /***************************************** Back to Previous <Action>  ****************************************/  
    public void backToPrevious(ActionEvent event) throws IOException 
    {
        Cancel();    
    }

    /***************************************** Start Job button <Action>  ****************************************/  
    public void startJob(ActionEvent event) throws IOException 
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);
        String updateStartDateQuery = "UPDATE job_card SET Start_Date = "+"'"+currentDateTime+"'"+" WHERE JC_ID = "+"'"+jCardId+"'"+";";
        String updateJobCardStatusQuery = "UPDATE job_card SET JC_Status = 'In_complete' "+" WHERE JC_ID = "+"'"+jCardId+"'"+";";

        try {
            for (rawMaterialQuantityArray col : tableView.getItems()) 
            {
                String updateWarehouseLabelQuery = "UPDATE `warehouse` SET warehouse_label = 'RFM' WHERE Warehouse_ID= "+"'"+col.getWa_Id()+"' ;";
                jobCardData.insertData(updateWarehouseLabelQuery);
            }
            // udapte Start date and status in job card
            jobCardData.insertData(updateStartDateQuery);
            jobCardData.insertData(updateJobCardStatusQuery);
            
            // reset delivery status
            if (jobCardData.getIsFirstOperation()) {
                String updateDeliveryStatus = "UPDATE sales_order SET Shipping_status = 'Pending' WHERE Order_ID = " +"'"+oderId+"' ;";
                jobCardData.insertData(updateDeliveryStatus);
            }
        } catch (Exception e) {
            jobCardData.insertData(updateStartDateQuery);
            jobCardData.insertData(updateJobCardStatusQuery);
        }
        Cancel();
    }


    /***************************************** Get data and Set Table View Itmes <Mehtods>   ****************************************/  
    public void previewJobCardDetails(String jId, String oI, String rQ, String opCode) 
    {
        // Set Default Label
        jCardId = jId;
        oderId = oI;
        jobCardId.setText(jId);
        rQuantity.setText(rQ+" QTY");

        // Prepare statement for Finding Previous Job Status & Warehouse Label
        String searchJobCardStatus =    "SELECT JC_Status from job_card "+
                                        "WHERE Order_ID = "+"'"+oI+"' "+
                                        "AND OP_code< "+"'"+opCode+"' "+
                                        "ORDER BY OP_code DESC LIMIT 1 ;";



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
    
        
        // Validation on Material Request Action
        if (!jobCardData.isReady(rawMaterialQuantity)) {
            // ifelse - determine the job card required any raw material or not
            if (jobCardData.isRawMaterialNull(jobCardRawMaterial)) {
                noticeLabel.setText("*** RAW MATERIAL IS NOT READY YET ***");
                addInformation.setText("This Job Card Did Not Required Any Material !!");
                tableView.setItems(emptyList);
                requestStartJobButton.setDisable(true);
            } else {
                jobCardData.showRequiredRmQuantity(jobCardRawMaterial, rawMaterialQuantity);
                noticeLabel.setText("*** RAW MATERIAL IS NOT READY YET ***");
                addInformation.setText("Real-Time Stock Quantity Shown Above !!");
                tableView.setItems(jobCardData.getRawMaterialList());
                requestStartJobButton.setDisable(true);
            }
        } else {
            if (!jobCardData.checkStatus(searchJobCardStatus)) {
                noticeLabel.setText("** PREVIOUS JOB ARE STILL IN-COMPLETED **"); 
                requestStartJobButton.setDisable(true);
            } else {
                if (jobCardData.isRawMaterialNull(jobCardRawMaterial)) {
                    noticeLabel.setText("*** THIS JOB CARD DID NOT REQUIRED ANY MATERIALS ***");
                    tableView.setItems(emptyList);
                    requestStartJobButton.setText("Start Job");
                } else {
                    jobCardData.showRequiredRmQuantity(jobCardRawMaterial, rawMaterialQuantity);
                    noticeLabel.setText("*** REQUEST MATERIAL BEFORE START JOB ***");
                    tableView.setItems(jobCardData.getRawMaterialList());
                    requestStartJobButton.setText("Request Material & Start Job");
                }
            }
        }
            
            
        
        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }

    

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Wa_Id"));
        rmIdColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Rm_Id"));
        matQuantityColumn.setCellValueFactory(new PropertyValueFactory<rawMaterialQuantityArray,String>("Mat_Quantity"));        

        addInformation.setText("");
    }
    
}
