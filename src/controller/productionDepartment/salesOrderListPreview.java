package controller.productionDepartment;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import base.manufactureDepartment.ArrayList.salesOrderListArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import controller.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class salesOrderListPreview implements Initializable
{
    // Text field
    @FXML private TextField enterOrderId;

    // Choices Box
    @FXML private ChoiceBox<String> filterOrderStatus;
    @FXML private ChoiceBox<String> filterStatus;

    // label
    @FXML private Label salesOrderListLabel;
    @FXML private Label overallJobCardList;

    // menu bar
    @FXML private Pane overallJobCardListButton;
    @FXML private Pane logOutButton;
    @FXML private Pane manageDeliveryButton;

    // TableColumn & Table View
    @FXML private TableColumn<salesOrderListArray,String> oderIdColumn;
    @FXML private TableColumn<salesOrderListArray,String> itemCodeColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderQuantityColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderDateColumn;
    @FXML private TableColumn<salesOrderListArray,String> dueDateColumn;
    @FXML private TableColumn<salesOrderListArray,String> statusColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderStatusColumn;

    @FXML private TableView<salesOrderListArray> tableView;

    // Create a Class
    Manufacture_Main viewOrder = new Manufacture_Main();


    // define variable
    private salesOrderListArray temp;
    private Date lastClickTime;
    private Parent root;

    // Static Variable
    public static boolean inUpdateStatusMode = false;

    /***************************************** Define Query <Variables> ****************************************/  
    
    private String salesOrderListQuery =   "SELECT Order_ID, Item_code, Order_quantity, Order_date, Due_date, Status, Shipping_status " +
                                            "FROM sales_order "+
                                            "ORDER BY (regexp_replace(Order_ID,'[^0-9]','')) +0; "; // order by


    /***************************************** Jump to Overall Job Card List <Action>  ****************************************/  
    public void jumpToOverallJobCard() 
    {
        try {
            String fxmlPath = "/fxml/productionDepartment/overallJobCardPreview.fxml";
            Main jumpToOverallJobCardList = new Main();
            jumpToOverallJobCardList.switchScene(fxmlPath);
            
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

    /***************************************** Refresh TableView <Methods>  ****************************************/  
    public void refreshSalesOrderlist() 
    {
        viewOrder.viewOverallSalesOrder(salesOrderListQuery);
        tableView.setItems(viewOrder.getSalesOrderList());
    }

    /***************************************** Filter Status <Action>  ****************************************/  

    public void getFilterStatus(ActionEvent evnet) 
    {
        if (filterStatus.getValue().equals("All")) 
        {
            viewOrder.viewOverallSalesOrder(salesOrderListQuery);    
            tableView.setItems(viewOrder.getSalesOrderList());
        } else {
            viewOrder.filterStatus(filterStatus.getValue(), salesOrderListQuery);
            tableView.setItems(viewOrder.getSalesOrderList());
        }    
    }

    /***************************************** Filter Order_Status <Action>  ****************************************/  

    public void getFilterOrderStatus(ActionEvent event) 
    {
        if (filterOrderStatus.getValue().equals("All")) 
        {
            viewOrder.viewOverallSalesOrder(salesOrderListQuery);    
            tableView.setItems(viewOrder.getSalesOrderList());     
        } else {
            viewOrder.filterOrderStatus(filterOrderStatus.getValue(), salesOrderListQuery);
            tableView.setItems(viewOrder.getSalesOrderList());
        }    
    }
    
    /***************************************** Search Order ID Text Field <Action>  ****************************************/  

    public void searchOrderId(KeyEvent event) 
    {
        try {
            viewOrder.searchOrderIdSalesList(enterOrderId.getText(), salesOrderListQuery);
            tableView.setItems(viewOrder.getSalesOrderList());
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    /***************************************** Pop Up Generate Job Card Table <Action>  ****************************************/  
    public void popUpGenerateJobCardPage(String orderId, String itemCode, String fxmlPath ) 
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();
            // change controller
            generateJobCardPreview newJobCard = loader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            newJobCard.previewOrderDetails(orderId, itemCode);
            
            stage.showAndWait();
            refreshSalesOrderlist();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }
    

    /***************************************** Select column <Action>  ****************************************/  
    public void selectColumn() 
    {
        salesOrderListArray row = tableView.getSelectionModel().getSelectedItem();
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
                    if (row.getStatus().equals("Ready")) {
                        inUpdateStatusMode = true;
                        popUpGenerateJobCardPage(row.getOrder_Id(), row.getItem_Code(), "/fxml/productionDepartment/generateJobCardPreview.fxml");
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

    // overall jobcard list entered & exited
    public void overallJcEntered() {overallJobCardListButton.setStyle("-fx-background-color: #3d454d");}
    public void overallJcExited() {overallJobCardListButton.setStyle("-fx-background-color: #4b555e");}

    // Manage delivery entered & exited
    public void manageDeliveryEnter() {manageDeliveryButton.setStyle("-fx-background-color: #3d454d");}
    public void manageDeliveryExited() {manageDeliveryButton.setStyle("-fx-background-color: #4b555e");}


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // call methods
        viewOrder.viewOverallSalesOrder(salesOrderListQuery);


        // choices box - filterOrderStatus 
        filterOrderStatus.setValue("All");
        filterOrderStatus.getItems().addAll("All","Delivered","Completed","In_complete","Pending");
        filterOrderStatus.setOnAction(this :: getFilterOrderStatus);

        // choies box - filterStatus
        filterStatus.setValue("All");
        filterStatus.getItems().addAll("All","Not_Ready","Ready","Generated");
        filterStatus.setOnAction(this :: getFilterStatus);

        oderIdColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Id"));
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Item_Code"));
        orderQuantityColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Quantity"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Date"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Due_Date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Status"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Status"));
     
        tableView.setItems(viewOrder.getSalesOrderList());
    }

}
