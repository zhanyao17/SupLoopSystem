package controller.productionDepartment;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



import base.manufactureDepartment.ArrayList.generateJobCardArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
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

public class generateJobCardPreview implements Initializable
{
    // label
    @FXML private Label noticeLabel;
    @FXML private Label orderIdLabel;

    // Anchor Pane
    @FXML private AnchorPane scenePane;

    // button
    @FXML private Button generateJobCardButton;


    // Table Column & View
    @FXML private TableColumn<generateJobCardArray,String> operationCodeColumn;
    @FXML private TableColumn<generateJobCardArray,String> operationNameColumn;
    @FXML private TableColumn<generateJobCardArray,String> jcIdColumn;

    @FXML private TableView<generateJobCardArray> tableView;

    // define Vairables
    private Stage stage;
    private String oId;

    // define class
    Manufacture_Main newJobCard = new Manufacture_Main();


    /***************************************** Cancel <Method> ***********************************/  //  1 APRIL
    public void Cancel() 
    {
        salesOrderListPreview.inUpdateStatusMode = false;
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    /***************************************** Back to Previous page <Action> ***********************************/  //  1 APRIL
    public void backToPrevious(ActionEvent event) throws IOException
    {
        Cancel();
    }
    
    /***************************************** Generate Job Card Button <Action> ***********************************/  //  1 APRIL
    public void generateJobCard(ActionEvent event)throws IOException 
    {
        for (generateJobCardArray col : tableView.getItems()) 
        {
            String updateNewJobCard = "INSERT INTO job_card VALUES ( '"+col.getJc_Id()+"',"+"'"+oId+"',"+"'"+col.getOp_Code()+"' ,"+
            "'Pending',NULL,NULL);";
            newJobCard.insertData(updateNewJobCard);
        }
        String updateSalesOrderStatus = "UPDATE sales_order SET Status = 'Generated' WHERE Order_ID = " +"'"+oId+"' ;";
        newJobCard.insertData(updateSalesOrderStatus);
        Cancel();    
    }



    /***************************************** Get order ID & item code from main scene <Method> ***********************************/  //  1 APRIL
    public void previewOrderDetails(String orderId, String itemCode) 
    {
        // Prepare Statement
        String totJcId = "SELECT JC_ID from job_card; ";


        String item_operation = "SELECT ip.OP_code, o.OP_name " +
                                "from  item_operation ip " +
                                "INNER JOIN operation o on o.OP_code = ip.OP_code " +
                                "WHERE Item_code = "+"'"+itemCode+"' ;";

        // Set order id lable
        orderIdLabel.setText(orderId);
        
        oId = orderId;
        
        // show generated job id with operation name & code in table view
        noticeLabel.setText("*** GENERATE JOB CARD FOR THIS ORDER  ***");
        newJobCard.showGenerateJobCardTable(item_operation, totJcId);
        tableView.setItems(newJobCard.getGenerateJobCardList());

        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        operationCodeColumn.setCellValueFactory(new PropertyValueFactory<generateJobCardArray,String>("Op_Code"));
        operationNameColumn.setCellValueFactory(new PropertyValueFactory<generateJobCardArray,String>("Op_Name"));
        jcIdColumn.setCellValueFactory(new PropertyValueFactory<generateJobCardArray,String>("Jc_Id"));
        
    }
}
