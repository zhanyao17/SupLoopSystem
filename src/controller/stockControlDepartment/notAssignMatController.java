package controller.stockControlDepartment;

//Programmer Name: Kon Kian Xiang TP061242
//Program Name: notAssignMatController.java
//Description: To view all recycled product which transfer from manufacture department
//First Write: 8 April 2022
//Edited on: 19 April 2022

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import JDBC_Connectors.DBConnectors;
import base.stockControlDepartment.ArrayList.notAssignMatModel;
import controller.Main;
import controller.loginPage.loginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class notAssignMatController implements Initializable
{
    // choices box
    @FXML private ChoiceBox<String> enterRawMat;

    // button
    @FXML private Button resetButton;

    // Label
    @FXML private Label usernameLabel;

    // Define main class
    private Main m = new Main();

    // tableView & column
    @FXML private TableView<notAssignMatModel> tableView;

    @FXML private TableColumn<notAssignMatModel, String> warehouseIdColumn;
    @FXML private TableColumn<notAssignMatModel, String> rmIdColumn;
    @FXML private TableColumn<notAssignMatModel, String> rmNameColumn;
    @FXML private TableColumn<notAssignMatModel, String> materialQuantitycolumn;
    @FXML private TableColumn<notAssignMatModel, String> warehouseLabelColumn;
    
    
    // define statement
    private String totNotAssignQuery = "SELECT rec.RM_ID, rm.Raw_material_name, w.Warehouse_ID, w.material_quantity, w.warehouse_label "+
                                        "from warehouse w "+
                                        "INNER JOIN recycle_material rec on rec.recycle_material_ID = w.recycle_material_ID "+
                                        "INNER JOIN raw_material rm on rm.RM_ID = rec.RM_ID;";
    
    private String rawMaterialQuery = "SELECT rw.RM_ID, rw.Raw_material_name from raw_material rw;";

    // menu bar
    @FXML private Pane stockInfoPane;
    @FXML private Pane matRequestPane;
    @FXML private Pane keepTrackPane;
    @FXML private Pane logOutButton;
    
    // define observable list
    private ObservableList<notAssignMatModel> notAssignRaw;


    public void logout() throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout?");
        alert.setContentText("Are you sure");
        if(alert.showAndWait().get()== ButtonType.OK){
            m.switchScene("/fxml/loginPage/login.fxml");
        }
    }

    /***************************************************  Add all NA label rawmaterial <Methods>  *************************************************/  // 2 APRIL 
    public void showAllNotAssignDetails(String totNotAssignQuery) throws SQLException
    {
        DBConnectors connectNow = new DBConnectors();
        Connection con = connectNow.getConnection(); 
        // observable list
        notAssignRaw = FXCollections.observableArrayList();
        ResultSet rs = con.createStatement().executeQuery(totNotAssignQuery);
        while (rs.next()) 
        {
            notAssignRaw.add(new notAssignMatModel(rs.getString("Warehouse_ID"), rs.getString("RM_ID"), rs.getString("Raw_material_name"), rs.getString("material_quantity"), rs.getString("warehouse_label")));  
        }
    }
    
    /***************************************************  Filter NA Raw Table View <Methods>  *************************************************/  
    public void filterNotAssignList(String rMName, String totNotAssignQuery ) throws SQLException
    {
        DBConnectors connectNow = new DBConnectors();
        Connection con = connectNow.getConnection(); 
        // observable list
        notAssignRaw = FXCollections.observableArrayList();
        ResultSet rs = con.createStatement().executeQuery(totNotAssignQuery);
        while (rs.next()) 
        {
            if (rs.getString("Raw_material_name").equals(rMName)) 
            {
                notAssignRaw.add(new notAssignMatModel(rs.getString("Warehouse_ID"), rs.getString("RM_ID"), rs.getString("Raw_material_name"), rs.getString("material_quantity"), rs.getString("warehouse_label")));  
            }
        }    
    }
    
    /***************************************************  Return Raw amterial name array list <Methods>  *************************************************/  
    public ArrayList<String> getRMNameList(String query) throws SQLException
    {
        List<String> rmNameList=new ArrayList<>();
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) {
            rmNameList.add(rs.getString("Raw_material_name"));
        }
        
        return (ArrayList<String>) rmNameList;
        
    }

    /***************************************************  Filter NA Raw Table View <Action>  *************************************************/  
    public void fitlerRaw(ActionEvent event) throws SQLException
    {
        if (enterRawMat.getValue().equals("All")) {
            showAllNotAssignDetails(totNotAssignQuery);
            tableView.setItems(notAssignRaw);
        } else {
            filterNotAssignList(enterRawMat.getValue(), totNotAssignQuery);
            tableView.setItems(notAssignRaw);
        }
        resetButton.setDisable(false);
        
    }

    /***************************************************  Reset table view button <Action>  *************************************************/  
    public void resetTable(ActionEvent event) throws SQLException
    {
        enterRawMat.setValue("All");
        resetButton.setDisable(true);
    }

    /***************************************************  Menu bar action <Action>  *************************************************/  

    public void jumpToOverall(){
        try {
            String fxmlPath = "/fxml/stockControlDepartment/overallstocktable.fxml";
            Main jumpToOverall = new Main();
            jumpToOverall.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpToMaterialRequest(){
        try {
            String fxmlPath = "/fxml/stockControlDepartment/MaterialRequestTable.fxml";
            Main jumpToMaterialRequest = new Main();
            jumpToMaterialRequest.switchScene(fxmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpToKeepTrack() 
    {
        try {
            String fxmlPath = "/fxml/stockControlDepartment/KeepTrackTable.fxml";
            Main jumpToKeepTrack = new Main();
            jumpToKeepTrack.switchScene(fxmlPath);

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
    public void matListEnter() {matRequestPane.setStyle("-fx-background-color: #3d454d");}
    public void matListExited() {matRequestPane.setStyle("-fx-background-color: #4b555e");}

    // Keep track pane entered & exited
    public void keepTrackEnter() {keepTrackPane.setStyle("-fx-background-color: #3d454d");}
    public void keepTrackExited() {keepTrackPane.setStyle("-fx-background-color: #4b555e");}


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        resetButton.setDisable(true);
        enterRawMat.setValue("ALL");

        try {
            enterRawMat.getItems().addAll(getRMNameList(rawMaterialQuery));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        enterRawMat.setOnAction(arg0 -> {
            try {
                fitlerRaw(arg0);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        warehouseIdColumn.setCellValueFactory(new PropertyValueFactory<notAssignMatModel,String>("warehouseId"));
        rmIdColumn.setCellValueFactory(new PropertyValueFactory<notAssignMatModel,String>("rMId"));
        rmNameColumn.setCellValueFactory(new PropertyValueFactory<notAssignMatModel,String>("rMName"));
        materialQuantitycolumn.setCellValueFactory(new PropertyValueFactory<notAssignMatModel,String>("matQuantity"));
        warehouseLabelColumn.setCellValueFactory(new PropertyValueFactory<notAssignMatModel,String>("warehouseLabel"));
        
        try {
            showAllNotAssignDetails(totNotAssignQuery);
            tableView.setItems(notAssignRaw);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // getting employee name
        usernameLabel.setText(loginController.employeeName);

    }
    
    
    
    
    
}
