package controller.manufactureDepartment.ElectrodeProduction;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.manufactureDepartment.Methods.Manufacture_Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
public class updateEndDate 
{
    // Label
    @FXML private Label jobCardId;
    @FXML private Label rQuantity;


    // Anchor Pane
    @FXML private AnchorPane scenePane;


    // define varialbes
    private String jCardId;

    // define window varialbe
    private Stage stage;
    
    // define new class
    Manufacture_Main updateStatus1 = new Manufacture_Main();
    Manufacture_Main updateStatus2 = new Manufacture_Main();

    /***************************************** Get Data from other scene <Mehtods>   ****************************************/  
    public void previeweJobCardDetails(String jId, String rQ) 
    {
        jCardId = jId;
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
        JobCardPreview.inUpdateStatusMode = false;
        stage.close();
    }
    
    /***************************************** Back to Previous button <Action>   ****************************************/  
    public void backToPrevious(ActionEvent event) throws IOException 
    {
        Cancel();    
    }

    /***************************************** Complete Job Button <Action>   ****************************************/  
    public void completeTask(ActionEvent event) throws IOException 
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);
        String upEndDateQuery = "UPDATE job_card SET End_Date = "+"'"+currentDateTime+"'"+" WHERE JC_ID = "+"'"+jCardId+"'"+";";
        String upJobCardStatusQuery = "UPDATE job_card SET JC_Status = 'Completed' "+" WHERE JC_ID = "+"'"+jCardId+"'"+";";
        updateStatus1.insertData(upEndDateQuery);
        updateStatus2.insertData(upJobCardStatusQuery);
        Cancel();
    }

    
    
    
}
