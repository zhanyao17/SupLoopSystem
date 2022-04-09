package controller.manufactureDepartment.PackingStation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import base.manufactureDepartment.Methods.Manufacture_Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class updateEndDate_Packing 
{
      // Label
    @FXML private Label jobCardId;
    @FXML private Label rQuantity;


    // Anchor Pane
    @FXML private AnchorPane scenePane;


    // define varialbes
    private String jCardId;
    private String orderId;

    // define window varialbe
    private Stage stage;
    
    // define new class
    Manufacture_Main updateStatus1 = new Manufacture_Main();
    Manufacture_Main updateStatus2 = new Manufacture_Main();

    /***************************************** Get Data from other scene <Mehtods>   ****************************************/  
    public void previeweJobCardDetails(String jId, String rQ, String oId) 
    {
        jCardId = jId;
        orderId = oId;
        jobCardId.setText(jId);
        rQuantity.setText(rQ+" qty");
        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }

    /***************************************** Cancel function <Methods>   ****************************************/  
    public void Cancel() 
    {
        stage = (Stage) scenePane.getScene().getWindow();
        jobCardPreview_Packing.inUpdateStatusMode = false;
        stage.close();
    }
    
    /***************************************** Back to Previous button <Action>   ****************************************/  
    public void backToPrevious(ActionEvent event) throws IOException 
    {
        Cancel();    
    }

    /***************************************** Return Warehouse ID <Methods>   ****************************************/  
    public void name() {
        
    }

    /***************************************** Alert Information <Mehtods>   ****************************************/  
    public void alertMesssage() 
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("The Job Card End Date & Job Card Status Had Been Updated");
        alert.setTitle("** Information Context **");
        alert.setHeaderText(null);

        alert.showAndWait();
    }

    /***************************************** Complete Job Button <Action>   ****************************************/  
    public void completeTask(ActionEvent event) throws IOException 
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);
        String upEndDateQuery = "UPDATE job_card SET End_Date = "+"'"+currentDateTime+"'"+" WHERE JC_ID = "+"'"+jCardId+"'"+";";
        String upJobCardStatusQuery = "UPDATE job_card SET JC_Status = 'Completed' "+" WHERE JC_ID = "+"'"+jCardId+"'"+";";
        String upWarehouseLableQuery =  "UPDATE  warehouse w "+
                                        "INNER JOIN purchase_warehouse pw on pw.Warehouse_ID = w.Warehouse_ID "+
                                        "INNER JOIN purchase_order po on po.Purchase_ID = pw.Purchase_ID "+
                                        "INNER JOIN purchase_rm pr on pr.Purchase_ID = po.Purchase_ID "+
                                        "SET w.warehouse_label = 'FG' "+
                                        "WHERE po.Order_ID = "+"'"+orderId+"' ;";
        
        updateStatus1.insertData(upEndDateQuery); // Update Job Card End date
        updateStatus2.insertData(upJobCardStatusQuery); // Update Job Card Status
        updateStatus1.insertData(upWarehouseLableQuery); // Warehouse Label
        

        if (updateStatus1.checkOrderProgress(orderId)) {
            String updateDeliveryStatus = "UPDATE sales_order SET Shipping_status = 'Completed' WHERE Order_ID = " +"'"+orderId+"' ;";
            updateStatus1.insertData(updateDeliveryStatus); // Sales order shipping_status
        }
        Cancel();
        alertMesssage();
    }  
}
