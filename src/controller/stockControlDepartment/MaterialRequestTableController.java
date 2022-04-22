package controller.stockControlDepartment;

//Programmer Name: Kon Kian Xiang TP061242
//Program Name: MaterialRequestTableController.java
//Description: To view and update in-stock information warehouse label from “RFM” to “WIP”
//First Write: 2 April 2022
//Edited on: 19 April 2022

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import JDBC_Connectors.DBConnectors;
import base.stockControlDepartment.ArrayList.MaterialRequestTableModel;
import controller.Main;
import controller.loginPage.loginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;


public class MaterialRequestTableController implements Initializable {

    @FXML
    private TableView<MaterialRequestTableModel> overalltableview;
    @FXML
    private TableColumn<MaterialRequestTableModel, String> workstationIDcolumn;
    @FXML
    private TableColumn<MaterialRequestTableModel, String> materialIDcolumn;
    @FXML
    private TableColumn<MaterialRequestTableModel, String> materialNamecolumn;
    @FXML
    private TableColumn<MaterialRequestTableModel, String> warehouseIDcolumn;
    @FXML
    private TableColumn<MaterialRequestTableModel, String> warehouseLabelcolumn;

    ObservableList<MaterialRequestTableModel> MaterialRequestTableModelObservableList = FXCollections.observableArrayList();

    // menu bar
    @FXML private Pane stockInfoPane;
    @FXML private Pane keepTrackPane;
    @FXML private Pane recycleMatPane;
    @FXML private Pane logOutButton;


    // Label
    @FXML private Label usernameLabel;
    @FXML private Label warningMessages;

    // Define main class
    private Main m = new Main();

    // define varaible
    private MaterialRequestTableModel temp;
    private Date lastClickTime;
    public static boolean inUpdateStatusMode =false;


    private String q2 = "SELECT po.Order_ID, w.Warehouse_ID, pr.RM_ID, r.Raw_material_name, w.warehouse_label from  warehouse w " +
                        "INNER JOIN purchase_warehouse pw on pw.Warehouse_ID = w.Warehouse_ID " +
                        "INNER JOIN purchase_order po on po.Purchase_ID = pw.Purchase_ID INNER JOIN purchase_rm pr on pr.Purchase_ID = po.Purchase_ID " +
                        "INNER JOIN raw_material r on r.RM_ID = pr.RM_ID;";

    public void logout() throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout?");
        alert.setContentText("Are you sure");
        if(alert.showAndWait().get()== ButtonType.OK){
            m.switchScene("/fxml/loginPage/login.fxml");
        }
    }

    public void showAllMaterialRequestDetails(String q2) {
        try {
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            MaterialRequestTableModelObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(q2);
            while (rs.next()) {

                String q1 = "SELECT ior.RM_ID, o.WS_ID, so.Order_ID from sales_order so inner JOIN item_operation_rm ior on ior.Item_code = so.Item_code " +
                            "INNER JOIN operation o on o.OP_code = ior.OP_code WHERE ior.RM_ID = " +
                            "'" + rs.getString("RM_ID") + "' " +
                            "  and so.Order_ID = " + "'" + rs.getString("Order_ID") + "'; ";
                ResultSet rs2 = con.createStatement().executeQuery(q1);
                rs2.next();

                if (rs.getString("warehouse_label").equals("RFM")) {
                    MaterialRequestTableModelObservableList.add(new MaterialRequestTableModel(rs2.getString("WS_ID"), rs.getString("RM_ID"), rs.getString("Raw_material_name"), rs.getString("Warehouse_ID"), rs.getString("warehouse_label")));
                } else {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertData(String insertQuery)
    {
        try {
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            int rs = con.createStatement().executeUpdate(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectColumn()
    {
        MaterialRequestTableModel row = overalltableview.getSelectionModel().getSelectedItem();
        if(row == null) return;
        if(row != temp) {
            temp = row;
            lastClickTime = new Date();
        }else{
            Date now = new Date();
            long diff = now.getTime()-lastClickTime.getTime();
            if(inUpdateStatusMode){

            }else{
                if(diff<300){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update Confirmation");
                    alert.setHeaderText("Changing warehouse label from RFM to WIP. Are you confirm?");

                    if (alert.showAndWait().get()== ButtonType.OK)
                    {
                        // change label - warehouse
                        String statement = "UPDATE warehouse SET warehouse_label = 'WIP' WHERE Warehouse_ID =" +"'" + row.getWarehouseID() +"'";
                        insertData(statement);
                        refreshMaterialRequestTable();
                    }else{
                        lastClickTime = new Date();
                    }
                }
            }
        }
    }

    public void refreshMaterialRequestTable()
    {
        showAllMaterialRequestDetails(q2);
        overalltableview.setItems(MaterialRequestTableModelObservableList);
    }

    public void jumpToOverall(){
        try {
            String fxmlPath = "/fxml/stockControlDepartment/overallstocktable.fxml";
            Main jumpToOverall = new Main();
            jumpToOverall.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpToKeepTrack(){
        try {
            String fxmlPath = "/fxml/stockControlDepartment/KeepTrackTable.fxml";
            Main jumpToKeepTrack = new Main();
            jumpToKeepTrack.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpToRecycleRaw() {
        try {
            String fxmlPath = "/fxml/stockControlDepartment/notAssignMat.fxml";
            Main jumpToMaterialRequest = new Main();
            jumpToMaterialRequest.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************  Menu bar effect Button <Action>  *************************************************/  // 2 APRIL
    // logout button entered & Exited
    public void logOutBarEnter() {logOutButton.setStyle("-fx-background-color: #3d454d");}
    public void logOutBarExited() {logOutButton.setStyle("-fx-background-color: #4b555e");}

    // over stock information entered & exited
    public void stockInfoEnter() {stockInfoPane.setStyle("-fx-background-color: #3d454d");}
    public void stockInfoExited() {stockInfoPane.setStyle("-fx-background-color: #4b555e");}

    // Material Request list entered & exited
    public void keepTrackEnter() {keepTrackPane.setStyle("-fx-background-color: #3d454d");}
    public void keepTrackExited() {keepTrackPane.setStyle("-fx-background-color: #4b555e");}

    // Material Request list entered & exited
    public void recycleMatEnter() {recycleMatPane.setStyle("-fx-background-color: #3d454d");}
    public void recycleMatExited() {recycleMatPane.setStyle("-fx-background-color: #4b555e");}


    @Override
    public void initialize(URL url, ResourceBundle resource) {
        
        warningMessages.setText("");
        //getter&setter variable
        workstationIDcolumn.setCellValueFactory(new PropertyValueFactory<>("workStationID"));
        materialIDcolumn.setCellValueFactory(new PropertyValueFactory<>("materialID"));
        materialNamecolumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        warehouseIDcolumn.setCellValueFactory(new PropertyValueFactory<>("warehouseID"));
        warehouseLabelcolumn.setCellValueFactory(new PropertyValueFactory<>("warehouseLabel"));

        showAllMaterialRequestDetails(q2);
        overalltableview.setItems(MaterialRequestTableModelObservableList);

        if (MaterialRequestTableModelObservableList.isEmpty()) 
        {
            warningMessages.setText("Currently Did Not Have Any Material Request !!");
        }

        // getting employee name
        usernameLabel.setText(loginController.employeeName);
    }
}

