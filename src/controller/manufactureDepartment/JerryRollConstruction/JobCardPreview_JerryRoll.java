package controller.manufactureDepartment.JerryRollConstruction;

//Programmer Name : Lin Zhan Yao TP061252
//Program Name: JobCardPreview_JerryRoll.java
//Description: To Allow Cell Assembly Workers update the job card status
//First Written on: 31 MARCH 2022
//Edited on: 10 April 2022

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

// import class
import base.manufactureDepartment.ArrayList.JobCardArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import controller.Main;
import controller.loginPage.loginController;
// javafx
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class JobCardPreview_JerryRoll implements Initializable
{
    // Text field
    @FXML private TextField enterJobCardId;

    // Choices Box
    @FXML private ChoiceBox<String> filterChoices;

    // menu bar
    @FXML private Pane logOutButton;

    // Label
    @FXML private Label usernameLabel;

    // call class
    Manufacture_Main eleJobCard = new Manufacture_Main();

    // Define main class
    private Main m = new Main();

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
    Date lastClickTime;

    // statis variable
    public static boolean inUpdateStatusMode =false;

    // define window varialbe

    private Parent root;


    /***************************************** Define Query <Variables> ****************************************/  
    private String overallEleJobCardQuery = "SELECT jc.*, op.OP_name"+" FROM job_card jc INNER JOIN `operation` op on jc.OP_code = op.OP_code "+
                                            "INNER JOIN `workstation` ws on ws.WS_ID = op.WS_ID "+
                                            "WHERE ws.WS_ID = 'WS002'"+
                                            "ORDER BY (regexp_replace(jc.JC_ID,'[^0-9]','')) +0 ;";


    private String getRequiredOrderQuantity =  "SELECT jc.JC_ID, so.Order_quantity"+" FROM `job_card` jc INNER JOIN sales_order so on jc.Order_ID = so.Order_ID "+
                                                "INNER JOIN `operation` op on jc.OP_code = op.OP_code "+
                                                "INNER JOIN `workstation` ws on ws.WS_ID = op.WS_ID "+
                                                "WHERE ws.WS_ID = 'WS002'"+
                                                "ORDER BY (regexp_replace(jc.JC_ID,'[^0-9]','')) +0 ;";


    /***************************************** Log Out  <Action>  ****************************************/  
    public void logout() throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout?");
        alert.setContentText("Are you sure");
        if(alert.showAndWait().get()== ButtonType.OK){
            m.switchScene("/fxml/loginPage/login.fxml");
        }
    }

    /***************************************** Refresh TableView <Methods>  ****************************************/  
    public void refreshJobCardPreview() 
    {
        eleJobCard.selectEleJobCard(overallEleJobCardQuery);
        tableView.setItems(eleJobCard.getJobEleCard());
    }



    /***************************************** Show up Update End Date page  <Methods>  ****************************************/  
    public void popUpEndDatePage(String jId, String fxmlPath) 
    {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        root = loader.load();
        // change controller
        updateEndData_JerryRoll upEndDate = loader.getController();
        String rQ = eleJobCard.showRequiredQuantity(jId,getRequiredOrderQuantity);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        upEndDate.previeweJobCardDetails(jId, rQ);
        
        stage.showAndWait();
        refreshJobCardPreview();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    }
    
    /***************************************** Show up Update Start Date page <Methods>  ****************************************/  
    public void popUpStartJobPage (String oId, String jId,String opCode, String fxmlPath) 
    {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        root = loader.load();
        // change controller
        updateStartDate_JerryRoll upStartDate = loader.getController();
        String rQ = eleJobCard.showRequiredQuantity(jId,getRequiredOrderQuantity);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        upStartDate.previewJobCardDetails(jId, oId,rQ, opCode);
        
        stage.showAndWait();
        refreshJobCardPreview();
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
            enterJobCardId.setText(null);
        } else {
            eleJobCard.filterEleJobCard(filterChoices.getValue(),overallEleJobCardQuery);
            tableView.setItems(eleJobCard.getJobEleCard());
            enterJobCardId.setText(null);
        }
    }
    
    /***************************************** Search Job Card ID <Action>   ****************************************/  
    public void searchJobCardId(KeyEvent event) 
    {
        try {
            if (enterJobCardId==null) {/* ignore action */}
                
            else {
                eleJobCard.searchJobCard(enterJobCardId.getText(),overallEleJobCardQuery);
                tableView.setItems(eleJobCard.getJobEleCard()); 
            }
               
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
                    if (row.getEnd_Date()==null&&row.getStart_Date()==null) 
                    {
                        inUpdateStatusMode = true;
                        // can request material and upadate start date
                        popUpStartJobPage(row.getOrder_Id(), row.getJc_Id(),row.getOp_Code(), "/fxml/manufactureDepartment/JerryRollConstruction/updateStartDate_JerryRoll.fxml");   
                    } else {
                        if (row.getEnd_Date()==null) // Complete Job
                        {
                            // can complete job only
                            inUpdateStatusMode = true;
                            popUpEndDatePage(row.getJc_Id(),"/fxml/manufactureDepartment/JerryRollConstruction/updateEndDate_JerryRoll.fxml" );
                        } else {
                            // continue;
                        }
                    }
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

        // getting employee name
        usernameLabel.setText(loginController.employeeName);

        tableView.setItems(eleJobCard.getJobEleCard());

    }
}
