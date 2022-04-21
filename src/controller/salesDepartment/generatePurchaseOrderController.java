package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: generatePurchaseOrderController.java
//Description: To generate a purchase order to supplier for a sales order
//First Written on: 11 April 2022
//Edited on: 18 April 2022

import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.PurchaseOrder;
import base.salesDepartment.ArrayLists.SalesOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class generatePurchaseOrderController implements Initializable {
    @FXML
    private TableView<PurchaseOrder> purchaseOrderTable;

    @FXML
    private TableColumn<PurchaseOrder, Float> purchaseQuantityCol;

    @FXML
    private TableColumn<PurchaseOrder, Float> quantityRequiredCol;

    @FXML
    private TableColumn<PurchaseOrder, Float> rmCostCol;

    @FXML
    private TableColumn<PurchaseOrder, String> rmIDCol;

    @FXML
    private TableColumn<PurchaseOrder, String> rmNameCol;

    @FXML
    private TableColumn<PurchaseOrder, Float> stockCol;

    @FXML
    private TableColumn<PurchaseOrder, Float> subTotalCol;

    @FXML
    private TableColumn<PurchaseOrder, String> supplierNameCol;

    @FXML
    private TextField orderIDText, itemCodeText, totalCostText;

    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    private PurchaseOrder purchaseOrderSelected = null;

    public static SalesOrder passOrder = null;

    private ObservableList<PurchaseOrder> purchaseOrdersList = FXCollections.observableArrayList();

    private float amount;

    @FXML
    private Label submitErrorLabel;

    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    ResultSet resultSet2 = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        rmIDCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, String>("rmID"));
        rmNameCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, String>("rmName"));
        rmCostCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, Float>("rmCost"));
        quantityRequiredCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, Float>("quantityRequired"));
        stockCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, Float>("stock"));
        purchaseQuantityCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, Float>("purchaseQuantity"));
        subTotalCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, Float>("subTotal"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<PurchaseOrder, String>("supplierName"));
        amount = passOrder.getOrderQuantity();
        orderIDText.setText(passOrder.getOrderID());
        itemCodeText.setText(passOrder.getItemCode());
        loadPurchaseOrderTable();

    }
    //close database connection
    public void closeConnection(){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(resultSet2 != null){
            try {
                resultSet2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //show all the required raw_material to manufacture the item
    public void loadPurchaseOrderTable(){
        float requiredQuantity;
        float totalCost = 0;
        try{
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT * FROM raw_material rm INNER JOIN item_raw_material irm on rm.RM_ID = irm.RM_ID WHERE irm.Item_code = '"+passOrder.getItemCode()+"'");
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                PurchaseOrder purchaseOrder = new PurchaseOrder(resultSet.getString("RM_ID"),
                        resultSet.getString("Raw_material_name"),
                        resultSet.getFloat("Material_cost_per_gram"));

                //calculate total required material in gram
                requiredQuantity = (amount * resultSet.getFloat("material_quantity"))/1000; //convert from g to kg
                purchaseOrder.setQuantityRequired(requiredQuantity);

                //get raw material quantity in stock
                statement = connection.prepareStatement("SELECT * FROM warehouse w " +
                                                            "INNER JOIN recycle_material rc on w.recycle_material_ID = rc.recycle_material_ID " +
                                                            "WHERE RM_ID = '"+resultSet.getString("RM_ID")+"'");
                resultSet2 = statement.executeQuery();
                if(resultSet2.next()){
                    purchaseOrder.setStock(resultSet2.getFloat("material_quantity"));
                }else{
                    purchaseOrder.setStock(0);
                }

                //contra value
                if(purchaseOrder.getStock() >= requiredQuantity){
                    purchaseOrder.setPurchaseQuantity(0);
                }else{
                    purchaseOrder.setPurchaseQuantity(Float.parseFloat(String.format("%.2f",(requiredQuantity - purchaseOrder.getStock()))));
                }
                //calculate sub-total
                purchaseOrder.setSubTotal(Float.parseFloat(String.format("%.2f",(purchaseOrder.getRmCost() * purchaseOrder.getPurchaseQuantity() * 1000))));
                totalCost += purchaseOrder.getSubTotal();
                //set supplier
                ObservableList<String> supplier = FXCollections.observableArrayList();

                //get the supplier for the particular material
                statement = connection.prepareStatement("SELECT * FROM supplier_rm srm INNER JOIN supplier s on srm.Supplier_ID = s.Supplier_ID " +
                                                            "WHERE srm.RM_ID ='"+resultSet.getString("RM_ID")+"'");
                resultSet2 = statement.executeQuery();
                while (resultSet2.next()){
                    supplier.add(resultSet2.getString("Supplier_ID")+"-"+resultSet2.getString("Supplier_name"));
                }
                //setup a combo box to select supplier
                ComboBox supplierBox = new ComboBox(supplier);
                supplierBox.setMinWidth(240);
                purchaseOrder.setSupplierName(supplierBox);
                purchaseOrdersList.add(purchaseOrder);
            }

            totalCostText.setText(String.format("%.2f",totalCost));
            purchaseOrderTable.setItems(purchaseOrdersList);

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }
    //get selected row in table
    public void getPurchaseOrderClick(){
        try{
            purchaseOrderSelected = purchaseOrderTable.getSelectionModel().getSelectedItem();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //close the window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }

    //generate the purchase order and save the record in database
    public void submitPurchaseOrder(){
        for(PurchaseOrder po: purchaseOrdersList){
            if(po.getSupplierName().getValue() == null){
                if(po.getPurchaseQuantity() != 0){
                    submitErrorLabel.setText("You must select a supplier for all raw material!");
                    return;
                }
            }
        }

        try{
            connection = new DBConnectors().getConnection();

            //Insert value into table: purchase_order
            for(PurchaseOrder po: purchaseOrdersList){
                //get largest value for primary key by counting the rows
                String purchaseID_PK = "";
                statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM purchase_order ;");
                resultSet = statement.executeQuery();
                if(resultSet.next()){
                    purchaseID_PK = "P"+(resultSet.getInt("Total")+1);
                }else{
                    purchaseID_PK = "P1";
                }

                //insert data into table: purchase_order
                statement = connection.prepareStatement("INSERT INTO purchase_order VALUES (?,?,?)");
                statement.setString(1, purchaseID_PK);
                if(po.getStock() >= po.getQuantityRequired()){
                    statement.setNull(2, Types.CHAR);
                }else{
                    statement.setString(2,String.valueOf(po.getSupplierName().getValue()).substring(0,5));
                }
                statement.setString(3,orderIDText.getText());
                statement.execute();

                //Insert data into purchase_rm table
                statement = connection.prepareStatement("INSERT INTO purchase_rm VALUES (?,?,?)");
                statement.setString(1, purchaseID_PK);
                statement.setString(2,po.getRmID());
                statement.setFloat(3,po.getPurchaseQuantity());
                statement.execute();

                //Update data in warehouse
                statement = connection.prepareStatement("SELECT * FROM warehouse w INNER JOIN " +
                                                            "recycle_material rm on w.recycle_material_ID = rm.recycle_material_ID " +
                                                            "WHERE RM_ID = '"+po.getRmID()+"';");
                resultSet2 = statement.executeQuery();
                if(resultSet2.next()){
                    String recycleID = resultSet2.getString("recycle_material_ID");
                    if(po.getStock() <= po.getQuantityRequired()){ //if the stock quantity is not enough

                        statement = connection.prepareStatement("UPDATE warehouse " +
                                                                    "SET recycle_material_ID = null, warehouse_label = 'NR' " +
                                                                    "WHERE recycle_material_ID = ?");
                        statement.setString(1,recycleID);
                        statement.execute();

                        //delete data in recycle_material because all material is claimed
                        statement = connection.prepareStatement("DELETE FROM recycle_material WHERE recycle_material_ID =?");
                        statement.setString(1,recycleID);
                        statement.execute();

                        //add record in purchase_warehouse table to link purchaseID to warehouseID
                        statement = connection.prepareStatement("INSERT INTO purchase_warehouse VALUES(?,?)");
                        statement.setString(1, purchaseID_PK);
                        statement.setString(2, resultSet2.getString("Warehouse_ID"));
                        statement.execute();

                        continue;

                    }else if(po.getStock() > po.getQuantityRequired()){ //if the stock quantity is more than enough
                        //update label in warehouse
                        statement = connection.prepareStatement("UPDATE warehouse " +
                                "SET recycle_material_ID = null, warehouse_label = 'NR',material_quantity = ? " +
                                "WHERE recycle_material_ID = ?");
                        statement.setFloat(1, po.getQuantityRequired());
                        statement.setString(2,recycleID);
                        statement.execute();

                        //add record in purchase_warehouse table to link purchaseID to warehouseID
                        statement = connection.prepareStatement("INSERT INTO purchase_warehouse VALUES(?,?)");
                        statement.setString(1, purchaseID_PK);
                        statement.setString(2, resultSet2.getString("Warehouse_ID"));
                        statement.execute();

                        //add remaining stock to warehouse
                        statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM warehouse");
                        resultSet2 = statement.executeQuery();

                        if(resultSet2.next()){
                            statement = connection.prepareStatement("INSERT INTO warehouse VALUES (?,?,?,?)");
                            statement.setString(1,"W"+(resultSet2.getInt("Total")+1));
                            statement.setFloat(2, (po.getStock() - po.getQuantityRequired()));
                            statement.setString(3,"NA");
                            statement.setString(4,recycleID);
                            statement.execute();
                        }
                        continue;
                    }
                }else{ //when there is no stock in warehouse
                    statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM warehouse");
                    resultSet2 = statement.executeQuery();
                    if(resultSet2.next()){
                        statement = connection.prepareStatement("INSERT INTO warehouse VALUES (?,?,?,null)");
                        statement.setString(1,"W"+(resultSet2.getInt("Total")+1));
                        statement.setFloat(2, 0);
                        statement.setString(3,"NR");
                        statement.execute();
                    }

                    //add record in purchase_warehouse table to link purchaseID to warehouseID
                    statement = connection.prepareStatement("INSERT INTO purchase_warehouse VALUES(?,?)");
                    statement.setString(1, purchaseID_PK);
                    statement.setString(2, "W"+(resultSet2.getInt("Total")+1));
                    statement.execute();

                }

                statement = connection.prepareStatement("SELECT COUNT(*) AS Total FROM purchase_invoice");
                resultSet2 = statement.executeQuery();
                //insert data into purchase_invoice table
                if(resultSet2.next()){
                    statement = connection.prepareStatement("INSERT INTO purchase_invoice VALUES(?,?,?,?)");
                    statement.setString(1,"PI"+(resultSet2.getInt("Total")+1));
                    statement.setString(2,purchaseID_PK);
                    statement.setString(3, String.valueOf(LocalDate.now()));
                    statement.setFloat(4,po.getSubTotal());
                    statement.execute();
                }

            }
            //update status to indicate the purchase order has been generated
            statement = connection.prepareStatement("UPDATE sales_order SET Status = 'Ready' WHERE Order_ID = ?");
            statement.setString(1,passOrder.getOrderID());
            resultSet2 = statement.executeQuery();
            cancel();

        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

}
