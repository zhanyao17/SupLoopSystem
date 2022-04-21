package controller.stockControlDepartment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;
import base.stockControlDepartment.ArrayList.KeepTrackTableModel;
import controller.Main;
import controller.loginPage.loginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class KeepTrackTableController implements Initializable {

    @FXML
    private TableView<KeepTrackTableModel> overalltableview;
    @FXML
    private TableColumn<KeepTrackTableModel, String> materialIDcolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, String> warehouseIDcolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, Integer> warehouseLabelcolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, String> materialQuantitycolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, String> totalRequiredQuantitycolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, String> purchaseTotalQuantitycolumn;
    @FXML
    private TableColumn<KeepTrackTableModel, String> orderIDcolumn;
    
    // textfield
    @FXML private TextField keywordTextField;
    
    // menu bar
    @FXML private Pane stockInfoPane;
    @FXML private Pane matRequestPane;
    @FXML private Pane recycleMatPane;
    @FXML private Pane logOutButton;

    private KeepTrackTableModel temp;
    private Date lastClickTime;
    public static boolean inUpdateStatusMode =false;
    private Parent root;

     // Define main class
     private Main m = new Main();

    // Label
    @FXML private Label usernameLabel;

    private ObservableList<KeepTrackTableModel> KeepTrackTableModelObservableList = FXCollections.observableArrayList();



    private String q1 = "SELECT PRM.RM_ID, W.Warehouse_ID, W.warehouse_label, W.material_quantity, PRM.tot_quantity, PO.Order_ID\n" +
                        "FROM purchase_rm PRM INNER JOIN purchase_warehouse PW on PRM.Purchase_ID = PW.Purchase_ID\n" +
                        "INNER JOIN warehouse W on PW.Warehouse_ID = W.Warehouse_ID\n" +
                        "INNER JOIN purchase_order PO on PRM.Purchase_ID = PO.Purchase_ID "+
                        "Where W.warehouse_label = 'NR'";

    
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
    
    
    public void filterOrderID(String orderID, String showOrderIDQuery)
    {
        try {
            // Datebase connections
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            KeepTrackTableModelObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(showOrderIDQuery);
            while (rs.next())
            {
                String orderIDList = rs.getString("Order_ID");
                if (orderIDList.toLowerCase(Locale.ROOT).contains(orderID.toLowerCase(Locale.ROOT)))
                {
                    String q1 = "SELECT so.Order_ID, irm.RM_ID, ROUND(((so.Order_quantity* irm.material_quantity)/1000),3) as 'actual_quantity' from sales_order so " +
                            "INNER JOIN item_raw_material irm on irm.Item_code = so.Item_code WHERE so.Order_ID = "+ "'" + rs.getString("Order_ID") +"' AND irm.RM_ID = " + "'" + rs.getString("RM_ID") + "'";

                    ResultSet rs2 = con.createStatement().executeQuery(q1);
                    rs2.next();

                    KeepTrackTableModelObservableList.add(new KeepTrackTableModel(rs.getString("RM_ID"), rs.getString("Warehouse_ID"),
                            rs.getString("warehouse_label"), rs.getString("material_quantity"),
                            rs2.getString("actual_quantity"), rs.getString("tot_quantity"), rs.getString("Order_ID")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAllNRRaw(String q2)
    {
        try{
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            KeepTrackTableModelObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(q2);
            while (rs.next()){
                String q1 = "SELECT so.Order_ID, irm.RM_ID, ROUND(((so.Order_quantity* irm.material_quantity)/1000),3) as 'actual_quantity' from sales_order so " +
                        "INNER JOIN item_raw_material irm on irm.Item_code = so.Item_code WHERE so.Order_ID = "+ "'" + rs.getString("Order_ID") +"' AND irm.RM_ID = " + "'" + rs.getString("RM_ID") + "'";
                ResultSet rs2 = con.createStatement().executeQuery(q1);
                rs2.next();
                KeepTrackTableModelObservableList.add(new KeepTrackTableModel(rs.getString("RM_ID"), rs.getString("Warehouse_ID"),
                        rs.getString("warehouse_label"), rs.getString("material_quantity"),
                        rs2.getString("actual_quantity"), rs.getString("tot_quantity"), rs.getString("Order_ID")));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void filterwarehouseLabel(String wl_status, String q2)
    {
        try {
            // Datebase connections
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            KeepTrackTableModelObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(q2); // final list
            while (rs.next())
            {
                String q1 = "SELECT so.Order_ID, irm.RM_ID, ROUND(((so.Order_quantity* irm.material_quantity)/1000),3) as 'actual_quantity' from sales_order so INNER JOIN item_raw_material irm on irm.Item_code = so.Item_code WHERE so.Order_ID = "+ "'" + rs.getString("Order_ID") +"' AND irm.RM_ID = " + "'" + rs.getString("RM_ID") + "'";
                ResultSet rs2 = con.createStatement().executeQuery(q1);
                rs2.next();

                if (rs.getString("warehouse_label").equals(wl_status))
                {
                    KeepTrackTableModelObservableList.add(new KeepTrackTableModel(rs.getString("RM_ID"), rs.getString("Warehouse_ID"),
                            rs.getString("warehouse_label"), rs.getString("material_quantity"),
                            rs2.getString("actual_quantity"), rs.getString("tot_quantity"), rs.getString("Order_ID")));
                } else {
                    continue;
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchOrderID(KeyEvent event){
        try{
            filterOrderID(keywordTextField.getText(), q1);
            overalltableview.setItems(KeepTrackTableModelObservableList);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void popUpUpdatePage (String warehouseID, String actualQuantity, String materialQuantity, String fxmlPath)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();
            updateQuantityController u_quantity = loader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            u_quantity.viewUpdateDetails(warehouseID, actualQuantity, materialQuantity);

            stage.showAndWait();
            refreshKeepTrackTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void refreshKeepTrackTable()
    {
        showAllNRRaw(q1);
        overalltableview.setItems(KeepTrackTableModelObservableList);
    }

    public void doubleClickColumn() {
        KeepTrackTableModel row = overalltableview.getSelectionModel().getSelectedItem();
        if (row == null) return;
        if (row != temp) {
            temp = row;
            lastClickTime = new Date();
        } else {
            Date now = new Date();
            long diff = now.getTime() - lastClickTime.getTime();
            if (inUpdateStatusMode) {

            } else {
                if (diff < 300) {
                    inUpdateStatusMode = true;
                    popUpUpdatePage(row.getWarehouseID(), row.getActualQuantity(), row.getMaterialQuantity(), "/fxml/stockControlDepartment/updateQuantity.fxml");
                }else{
                    lastClickTime = new Date();
                }
            }
        }
    }


    /***************************************************  Menu bar swithc window action <Action>  *************************************************/  
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
    public void matListEnter() {matRequestPane.setStyle("-fx-background-color: #3d454d");}
    public void matListExited() {matRequestPane.setStyle("-fx-background-color: #4b555e");}

    // Material Request list entered & exited
    public void recycleMatEnter() {recycleMatPane.setStyle("-fx-background-color: #3d454d");}
    public void recycleMatExited() {recycleMatPane.setStyle("-fx-background-color: #4b555e");}



    @Override
    public void initialize(URL url, ResourceBundle resource)
    {
        //getter&setter variable
        materialIDcolumn.setCellValueFactory(new PropertyValueFactory<>("materialID"));
        warehouseIDcolumn.setCellValueFactory(new PropertyValueFactory<>("warehouseID"));
        warehouseLabelcolumn.setCellValueFactory(new PropertyValueFactory<>("warehouseLabel"));
        materialQuantitycolumn.setCellValueFactory(new PropertyValueFactory<>("materialQuantity"));
        totalRequiredQuantitycolumn.setCellValueFactory(new PropertyValueFactory<>("actualQuantity"));
        purchaseTotalQuantitycolumn.setCellValueFactory(new PropertyValueFactory<>("purchaseTotQuantity"));
        orderIDcolumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        showAllNRRaw(q1);
        overalltableview.setItems(KeepTrackTableModelObservableList);

        // getting employee name
        usernameLabel.setText(loginController.employeeName);

    }
}