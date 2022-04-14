package controller.productionDepartment;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;


import base.manufactureDepartment.ArrayList.JobCardArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import controller.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class overallJobCardPreview implements Initializable
{
    // Text field
    @FXML private TextField enterJobCardId;
    @FXML private TextField enterOderId;

    // Choices Box
    @FXML private ChoiceBox<String> filterChoices;

    // menu bar
    @FXML private Pane salesOrderButton;
    @FXML private Pane logOutButton;
    @FXML private Pane manageDeliveryButton;


    // call class
    Manufacture_Main eleJobCard = new Manufacture_Main();

    // TableColumn & TableView
    @FXML private TableColumn<JobCardArray,String> jobCardIdColumn;
    @FXML private TableColumn<JobCardArray,String> oderIdColumn;
    @FXML private TableColumn<JobCardArray,String> jobCardStatusColumn;
    @FXML private TableColumn<JobCardArray,String> operationNameColumn;
    @FXML private TableColumn<JobCardArray,String> operationIdColumn;
    @FXML private TableColumn<JobCardArray,String> startDateColumn;
    @FXML private TableColumn<JobCardArray,String> endDateColumn;
    @FXML private TableColumn<JobCardArray,Circle> colourStatusColumn;

    @FXML private TableView<JobCardArray> tableView;

    // define varaible 
    JobCardArray temp;
    private Date lastClickTime;

    // statis variable
    public static boolean inUpdateStatusMode =false;

    // define window varialbe

    private Parent root;


    /***************************************** Define Query <Variables> ****************************************/  
    private String overallEleJobCardQuery = "SELECT jc.*, op.OP_name"+" FROM job_card jc "+
                                            "INNER JOIN `operation` op on jc.OP_code = op.OP_code "+
                                            "INNER JOIN `workstation` ws on ws.WS_ID = op.WS_ID "+
                                            "ORDER BY (regexp_replace(jc.JC_ID,'[^0-9]','')) +0 ;";


    private String getRequiredOrderQuantity =  "SELECT jc.JC_ID, so.Order_quantity"+" FROM `job_card` jc "+
                                                "INNER JOIN sales_order so on jc.Order_ID = so.Order_ID "+
                                                "INNER JOIN `operation` op on jc.OP_code = op.OP_code "+
                                                "INNER JOIN `workstation` ws on ws.WS_ID = op.WS_ID "+
                                                "ORDER BY (regexp_replace(jc.JC_ID,'[^0-9]','')) +0 ;";

    
    /***************************************** Jump to Sales Order List <Action>  ****************************************/  
    
    public void jumpToSalesOrderList() 
    {
        try {
            String fxmlPath = "/fxml/productionDepartment/salesOrderListPreview.fxml";
            Main jumpToSalesOrderList = new Main();
            jumpToSalesOrderList.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************** Jump to Manage Order <Action>  ****************************************/  
    public void jumpToManageDelivery() 
    {
        try {
            String fxmlPath = "/fxml/productionDepartment/manageDelivery.fxml";
            Main jumpToSalesOrderList = new Main();
            jumpToSalesOrderList.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************** Log Out  <Action>  ****************************************/  

    /***************************************** Show up Update Start Date page <Methods>  ****************************************/  
    public void popUpStartJobPage (String oId, String jId,String opCode, String fxmlPath) 
    {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        root = loader.load();
        // change controller
        showMaterialRequired upStartDate = loader.getController();
        String rQ = eleJobCard.showRequiredQuantity(jId,getRequiredOrderQuantity);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        upStartDate.previewJobCardDetails(jId, oId,rQ, opCode);
        
        stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    /***************************************** Filter Job Card Status <Action>  ****************************************/  
    public void getFilter(ActionEvent event) 
    {
        if (filterChoices.getValue().equals("All")) 
        {
            eleJobCard.selectEleJobCard(overallEleJobCardQuery);
            tableView.setItems(eleJobCard.getJobEleCard());
        } else {
            eleJobCard.filterEleJobCard(filterChoices.getValue(),overallEleJobCardQuery);
            tableView.setItems(eleJobCard.getJobEleCard());
        }
    }
    
    /***************************************** Search Job Card ID <Action>   ****************************************/  
    public void searchJobCardId(KeyEvent event) 
    {
        try {
            eleJobCard.searchJobCard(enterJobCardId.getText(),overallEleJobCardQuery);
            tableView.setItems(eleJobCard.getJobEleCard());    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************** Search Order ID <Action>   ****************************************/  
    public void searchOrderId(KeyEvent event) 
    {
        try {
            eleJobCard.searchOrderId(enterOderId.getText(), overallEleJobCardQuery);
            tableView.setItems(eleJobCard.getJobEleCard());
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }



    /***************************************** Select Column <Action>   ****************************************/  
    public void selectColumn() 
    {
        JobCardArray row = tableView.getSelectionModel().getSelectedItem();
        if(row == null) return;
        if(row!=temp) {
            temp=row;
            lastClickTime = new Date();
        }else{
            Date now = new Date();
            long diff = now.getTime()-lastClickTime.getTime();
            if (inUpdateStatusMode) {
                
            } else {
                if (diff<300) {
                    inUpdateStatusMode = true;
                    popUpStartJobPage(row.getOrder_Id(), row.getJc_Id(),row.getOp_Code(), "/fxml/productionDepartment/showMaterialRequired.fxml");   
                } else {
                    lastClickTime = new Date();
                }
            }
                
        }
    }

    /***************************************************  Menu bar effect Button <Action>  *************************************************/  // 2 APRIL
    // logout button entered & Exited
    public void logOutBarEnter() {logOutButton.setStyle("-fx-background-color: #3d454d");}
    public void logOutBarExited() {logOutButton.setStyle("-fx-background-color: #4b555e");}

    // sales order list entered & exited
    public void salesOrderEnter() {salesOrderButton.setStyle("-fx-background-color: #3d454d");}
    public void salesOrderExited() {salesOrderButton.setStyle("-fx-background-color: #4b555e");}

    // Manage delivery entered & exited
    public void manageDeliveryEnter() {manageDeliveryButton.setStyle("-fx-background-color: #3d454d");}
    public void manageDeliveryExited() {manageDeliveryButton.setStyle("-fx-background-color: #4b555e");}

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    
        eleJobCard.selectEleJobCard(overallEleJobCardQuery);
        
        // choices box 
        filterChoices.setValue("All");
        filterChoices.getItems().addAll("All","Completed","In_complete","Pending");
        filterChoices.setOnAction(this :: getFilter);

        
        // table view & column
        jobCardIdColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Jc_Id"));
        oderIdColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Order_Id"));
        jobCardStatusColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Jc_Status"));
        operationNameColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Op_Name"));
        operationIdColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Op_Code"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("Start_Date"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,String>("End_Date"));
        colourStatusColumn.setCellValueFactory(new PropertyValueFactory<JobCardArray,Circle>("colourStatus"));

        colourStatusColumn.setStyle("-fx-alignment: CENTER;");

        tableView.setItems(eleJobCard.getJobEleCard());

    }   
}
