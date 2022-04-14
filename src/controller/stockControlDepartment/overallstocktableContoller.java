package controller.stockControlDepartment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import JDBC_Connectors.DBConnectors;
import base.stockControlDepartment.ArrayList.OverallstocktableModel;
import controller.Main;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.ResourceBundle;


public class overallstocktableContoller implements Initializable {

    @FXML
    private TableView<OverallstocktableModel> overalltableview;
    @FXML
    private TableColumn<OverallstocktableModel, String> materialIDcolumn;
    @FXML
    private TableColumn<OverallstocktableModel, String> materialNamecolumn;
    @FXML
    private TableColumn<OverallstocktableModel, Integer> remainingQuantitycolumn;
    @FXML
    private TableColumn<OverallstocktableModel, String> warehouseLabelcolumn;
    @FXML
    private TableColumn<OverallstocktableModel, String> purchaseIDcolumn;
    @FXML
    private TableColumn<OverallstocktableModel, String> orderIDcolumn;
    @FXML
    private TextField keywordTextField;
    @FXML
    private ChoiceBox<String> warehouseCategory;

    // menu bar
    @FXML private Pane keepTrackPane;
    @FXML private Pane matRequestPane;
    @FXML private Pane recycleMatPane;
    @FXML private Pane logOutButton;

    //define variables
    private String productViewQuery =   "SELECT PRM.RM_ID, RM.Raw_material_name, W.material_quantity, W.warehouse_label, PO.Purchase_ID, PO.Order_ID\n" +
                                        "FROM purchase_rm PRM INNER JOIN raw_material RM on PRM.RM_ID = RM.RM_ID\n" +
                                        "INNER JOIN purchase_warehouse PW on PRM.Purchase_ID = PW.Purchase_ID\n" +
                                        "INNER JOIN warehouse W on PW.Warehouse_ID = W.Warehouse_ID\n" +
                                        "INNER JOIN purchase_order PO on PRM.Purchase_ID = PO.Purchase_ID\n" +
                                        "ORDER BY PO.Order_ID;\n";

    ObservableList<OverallstocktableModel> overallstocktableModelsObservableList = FXCollections.observableArrayList();

    public void filterOrderID(String orderID, String showOrderIDQuery)
    {
        try {
            // Datebase connections
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            overallstocktableModelsObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(showOrderIDQuery);
            while (rs.next())
            {
                String orderIDList = rs.getString("Order_ID");
                if (orderIDList.toLowerCase(Locale.ROOT).contains(orderID.toLowerCase(Locale.ROOT)))
                {
                    overallstocktableModelsObservableList.add(new OverallstocktableModel(rs.getString("RM_ID"), rs.getString("Raw_material_name"),
                            rs.getString("material_quantity"), rs.getString("warehouse_label"),
                            rs.getString("Purchase_ID"), rs.getString("Order_ID")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAllWarehouseDetails(String productViewQuery)
    {
        try{
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            overallstocktableModelsObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(productViewQuery);
            while (rs.next()){
                overallstocktableModelsObservableList.add(new OverallstocktableModel(rs.getString("RM_ID"), rs.getString("Raw_material_name"),
                                                                                    rs.getString("material_quantity"), rs.getString("warehouse_label"),
                                                                                    rs.getString("Purchase_ID"), rs.getString("Order_ID")));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filterwarehouseLabel(String wl_status, String productViewQuery )
    {
        try {
            // Datebase connections
            DBConnectors connectNow = new DBConnectors();
            Connection con = connectNow.getConnection();
            // Obervable list
            overallstocktableModelsObservableList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(productViewQuery); // final list
            while (rs.next())
            {
                if (rs.getString("warehouse_label").equals(wl_status))
                {
                    overallstocktableModelsObservableList.add(new OverallstocktableModel(rs.getString("RM_ID"), rs.getString("Raw_material_name"),
                            rs.getString("material_quantity"), rs.getString("warehouse_label"),
                            rs.getString("Purchase_ID"), rs.getString("Order_ID")));
                } else {
                    continue;
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filterLabel(ActionEvent event){
        if(warehouseCategory.getValue().equals("All")){
            showAllWarehouseDetails(productViewQuery);
            overalltableview.setItems(overallstocktableModelsObservableList);
        }else{
            filterwarehouseLabel(warehouseCategory.getValue(), productViewQuery);
            overalltableview.setItems(overallstocktableModelsObservableList);
        }
    }

    public void searchOrderID(KeyEvent event){
        try{
            filterOrderID(keywordTextField.getText(), productViewQuery);
            overalltableview.setItems(overallstocktableModelsObservableList);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************  Menu bar swithc window action <Action>  *************************************************/  
    public void jumpToKeepTrack(){
        try {
            String fxmlPath = "/fxml/stockControlDepartment/KeepTrackTable.fxml";
            Main jumpToKeepTrack = new Main();
            jumpToKeepTrack.switchScene(fxmlPath);

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
    public void keepTrackEnter() {keepTrackPane.setStyle("-fx-background-color: #3d454d");}
    public void keepTrackExited() {keepTrackPane.setStyle("-fx-background-color: #4b555e");}

    // Material Request list entered & exited
    public void matListEnter() {matRequestPane.setStyle("-fx-background-color: #3d454d");}
    public void matListExited() {matRequestPane.setStyle("-fx-background-color: #4b555e");}

    // Material Request list entered & exited
    public void recycleMatEnter() {recycleMatPane.setStyle("-fx-background-color: #3d454d");}
    public void recycleMatExited() {recycleMatPane.setStyle("-fx-background-color: #4b555e");}
    
    @Override
    public void initialize(URL url, ResourceBundle resource){
        warehouseCategory.setValue("All"); // defualt label filter item
        warehouseCategory.getItems().addAll("All", "NR", "NC","WIP", "RFM", "FG");
        warehouseCategory.setOnAction(this :: filterLabel); // auto refresh

            //getter&setter variable
            materialIDcolumn.setCellValueFactory(new PropertyValueFactory<>("materialID"));
            materialNamecolumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
            remainingQuantitycolumn.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));
            warehouseLabelcolumn.setCellValueFactory(new PropertyValueFactory<>("warehouseLabel"));
            purchaseIDcolumn.setCellValueFactory(new PropertyValueFactory<>("purchaseID"));
            orderIDcolumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));

            showAllWarehouseDetails(productViewQuery);
            overalltableview.setItems(overallstocktableModelsObservableList);
    }
}


