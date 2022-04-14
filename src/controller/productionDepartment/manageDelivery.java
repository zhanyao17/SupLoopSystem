package controller.productionDepartment;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import base.manufactureDepartment.ArrayList.salesOrderListArray;
import base.manufactureDepartment.Methods.Manufacture_Main;
import controller.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class manageDelivery implements Initializable{
    // Text field
    @FXML private TextField enterOrderId;



    // label
    @FXML private Label salesOrderListLabel;
    @FXML private Label overallJobCardList;

    // menu bar
    @FXML private Pane overallJobCardListButton;
    @FXML private Pane salesOrderButton;
    @FXML private Pane logOutButton;

    // TableColumn & Table View
    @FXML private TableColumn<salesOrderListArray,String> oderIdColumn;
    @FXML private TableColumn<salesOrderListArray,String> itemCodeColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderQuantityColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderDateColumn;
    @FXML private TableColumn<salesOrderListArray,String> dueDateColumn;
    @FXML private TableColumn<salesOrderListArray,String> orderStatusColumn;

    @FXML private TableView<salesOrderListArray> tableView;


    // Create a class
    Manufacture_Main deliverOrder = new Manufacture_Main();

    /***************************************** Define Query <Variables> ****************************************/  
    
    private String compleOrderQuery =   "SELECT Order_ID, Item_code, Order_quantity, Order_date, Due_date, Status, Shipping_status " +
                                            "FROM sales_order "+
                                            "WHERE Shipping_status = 'Completed' "+
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
    /***************************************** Jump to Overall Job Card List <Action>  ****************************************/  
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
    /***************************************** Alert Information <Mehtods>   ****************************************/  
    public void alertMesssage() 
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("New Delivery Informations !!\n"+
                            "This Order Had been successfully delivered.");
        alert.setTitle("** Information Context **");
        alert.setHeaderText(null);

        alert.showAndWait();
    }

    /***************************************** Deliver order <Action>   ****************************************/  
    public void delivered(ActionEvent event) throws SQLException
    {
        // seletect object from table view
        salesOrderListArray selectedRow = tableView.getSelectionModel().getSelectedItem();
        
        // validate is there any data selected
        if (selectedRow!=null) 
        {
            String updateDeliveryStatus = "UPDATE sales_order SET Shipping_status = 'Delivered' WHERE Order_ID = " +"'"+selectedRow.getOrder_Id()+"' ;";
            deliverOrder.insertData(updateDeliveryStatus);
        }
        
        alertMesssage();
        // refresh table view
        refreshTable();
    }
    /***************************************** Refresh TableView <Methods>  ****************************************/  
    public void refreshTable() 
    {
        deliverOrder.viewOverallSalesOrder(compleOrderQuery);
        tableView.setItems(deliverOrder.getSalesOrderList());
    }

    /***************************************** Select column <Action>  ****************************************/  
    public void searchOrderId(KeyEvent event) 
    {
        try {
            deliverOrder.searchOrderIdSalesList(enterOrderId.getText(), compleOrderQuery);
            tableView.setItems(deliverOrder.getSalesOrderList());
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    // logout button entered
    public void logOutBarEnter() {logOutButton.setStyle("-fx-background-color: #3d454d");}

    // logout button entered
    public void logOutBarExited() {logOutButton.setStyle("-fx-background-color: #4b555e");}

    // overall jobcard list entered
    public void overallJcEntered() {overallJobCardListButton.setStyle("-fx-background-color: #3d454d");}

    // overall jobcard list exited
    public void overallJcExited() {overallJobCardListButton.setStyle("-fx-background-color: #4b555e");}

    // sales order list entered
    public void salesOrderEnter() {salesOrderButton.setStyle("-fx-background-color: #3d454d");}

    // sales order list exited
    public void salesOrderExited() {salesOrderButton.setStyle("-fx-background-color: #4b555e");}

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        oderIdColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Id"));
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Item_Code"));
        orderQuantityColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Quantity"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Date"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Due_Date"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<salesOrderListArray,String>("Order_Status"));
        
        deliverOrder.viewOverallSalesOrder(compleOrderQuery);
        tableView.setItems(deliverOrder.getSalesOrderList());
    }

}