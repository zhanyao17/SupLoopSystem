package controller.salesDepartment;
//Programmer Name : Kang Jia Yong TP060575
//Program Name: dashboardController.java
//Description: To act as a analytic dashboard and display the statistic and the upcoming deadline
//First Written on: 15 April 2022
//Edited on: 18 April 2022
import controller.loginPage.loginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import JDBC_Connectors.DBConnectors;
import base.salesDepartment.ArrayLists.SalesInvoice;
import controller.Main;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    @FXML
    private TableView<SalesInvoice> upcomingDeadlineTable;

    @FXML
    private TableView<SalesInvoice> dueDeadlineTable;

    @FXML
    private TableColumn<SalesInvoice, ProgressBar> workloadCol;

    @FXML
    private TableColumn<SalesInvoice, String> deadlineCol;

    @FXML
    private TableColumn<SalesInvoice, String> invoiceIDCol;

    @FXML
    private TableColumn<SalesInvoice, String> progressCol;

    @FXML
    private TableColumn<SalesInvoice, ProgressBar> dueWorkloadCol;

    @FXML
    private TableColumn<SalesInvoice, String> dueDeadlineCol;

    @FXML
    private TableColumn<SalesInvoice, String> dueInvoiceIDCol;

    @FXML
    private TableColumn<SalesInvoice, String> dueProgressCol;

    @FXML
    private TableColumn<SalesInvoice, String> overdueCol;

    @FXML
    private BarChart<String, Number> monthlySalesBarChart;

    @FXML
    private PieChart itemPieChart;

    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private Label usernameLabel;

    private Main m = new Main();

    private String[] month = {
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
    };

    private int currentYear = LocalDate.now().getYear();
    private int selectedYear = currentYear;

    private ObservableList<Integer> yearList = FXCollections.observableArrayList(
            currentYear, currentYear-1, currentYear-2, currentYear-3, currentYear-4
    );

    private ObservableList<PieChart.Data> pieList = FXCollections.observableArrayList();

    private ObservableList<SalesInvoice> dueOrderList = FXCollections.observableArrayList();

    private ObservableList<SalesInvoice> overDueOrderList = FXCollections.observableArrayList();

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private ResultSet resultSet2 = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //setup table column
        invoiceIDCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("invoiceID"));
        deadlineCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("shippingDate"));
        progressCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("progressPercent"));
        workloadCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, ProgressBar>("progress"));

        dueInvoiceIDCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("invoiceID"));
        dueDeadlineCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("shippingDate"));
        dueProgressCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("progressPercent"));
        dueWorkloadCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, ProgressBar>("progress"));
        overdueCol.setCellValueFactory(new PropertyValueFactory<SalesInvoice, String>("status"));

        usernameLabel.setText(loginController.employeeName);
        //setup combo box
        yearCombo.setItems(yearList);
        yearCombo.setValue(selectedYear);
        loadMonthlySales();
        loadDeadlineTable();
        loadOverdueTable();
        loadTopItem();
    }

    //get selected year in combo box
    public void getYear() {
        selectedYear = yearCombo.getValue();
        loadMonthlySales();
    }

    //load bar chart data
    public void loadMonthlySales(){
        XYChart.Series series = new XYChart.Series();
        series.setName("Sales");

        for(String m: month){
            try{
                //calculate the total sales in each month with query
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT DISTINCT SUM(si.Order_total_price) AS 'Total' " +
                        "FROM sales_invoice si INNER JOIN sales_order so ON si.Sales_Invoice_ID = so.Sales_Invoice_ID " +
                        "WHERE MONTHNAME(Order_date) = ? AND YEAR(so.Order_date) = ?");
                statement.setString(1,m);
                statement.setInt(2,selectedYear);
                resultSet = statement.executeQuery();

                //create ber series
                if (resultSet.next()){
                    series.getData().add(new XYChart.Data<>(m, resultSet.getFloat("Total")));
                }else {
                    series.getData().add(new XYChart.Data<>(m, 0));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        monthlySalesBarChart.getData().clear();
        monthlySalesBarChart.getData().add(series);
    }

    //load top 10 best sellling item in pie chart
    public void loadTopItem(){
        try{
            //calculate total order for each item and return top 10 item
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT SUM(Order_quantity) AS 'Total', i.Item_name FROM sales_order so " +
                                                        "INNER JOIN item i ON so.Item_code = i.Item_code " +
                                                        "GROUP BY so.Item_code ORDER BY Total DESC LIMIT 10");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                pieList.add(new PieChart.Data(resultSet.getString("Item_Name"),resultSet.getInt("Total")));
            }
            itemPieChart.setData(pieList);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    //load upcoming sales order within 7 days in table
    public void loadDeadlineTable(){
        try{
            connection = new DBConnectors().getConnection();
            //find incomplete sales order that is due within 7 days
            statement = connection.prepareStatement("SELECT DISTINCT Sales_Invoice_ID, Due_date FROM sales_order " +
                                                        "WHERE DATEDIFF(Due_date,CURDATE()) BETWEEN 0 AND 7 ORDER BY Due_date ASC;");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                //check the shipping_status of the sales order to determine whether the order is completed
                statement = connection.prepareStatement("SELECT * FROM sales_order WHERE Sales_Invoice_ID = ?");
                statement.setString(1, resultSet.getString("Sales_Invoice_ID"));
                resultSet2 = statement.executeQuery();

                double numberOfOrder = 0;
                double completedOrder = 0;
                while(resultSet2.next()){
                    if(resultSet2.getString("Shipping_status").equals("Delivered")){
                        completedOrder++;
                    }
                    numberOfOrder++;
                }
                //create progress bar
                ProgressBar progressBar = new ProgressBar();
                progressBar.setStyle("-fx-accent: #00FF00;");
                progressBar.setProgress(completedOrder/numberOfOrder);

                //create object and load to table
                SalesInvoice dueOrder = new SalesInvoice(resultSet.getString("Sales_Invoice_ID"),
                        resultSet.getString("Due_date"),
                        (((int)(completedOrder/numberOfOrder*100)) + "%"),
                        progressBar);

                dueOrderList.add(dueOrder);
            }
            upcomingDeadlineTable.setItems(dueOrderList);

        }catch (SQLException e){
            e.printStackTrace();    
        }finally {
            closeConnection();
        }
    }

    //display all the sales order that has passed it shipping date
    public void loadOverdueTable(){
        try{
            //find all the sales order that has passed it shipping date with query
            connection = new DBConnectors().getConnection();
            statement = connection.prepareStatement("SELECT DISTINCT Sales_Invoice_ID, Due_date, DATEDIFF(Due_date,CURDATE()) " +
                                                        "AS 'Days' FROM sales_order " +
                                                        "WHERE DATEDIFF(Due_date,CURDATE()) <0");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                statement = connection.prepareStatement("SELECT * FROM sales_order WHERE Sales_Invoice_ID = ?");
                statement.setString(1, resultSet.getString("Sales_Invoice_ID"));
                resultSet2 = statement.executeQuery();

                double numberOfOrder = 0;
                double completedOrder = 0;
                //check the order's shipping status
                while(resultSet2.next()){
                    if(resultSet2.getString("Shipping_status").equals("Delivered")){
                        completedOrder++;
                    }
                    numberOfOrder++;
                }

                if(completedOrder/numberOfOrder == 1){ //if it equal to 1, means the order is completed
                    continue;
                }
                //create progress bar
                ProgressBar progressBar = new ProgressBar();
                progressBar.setStyle("-fx-accent: #00FF00;");
                progressBar.setProgress(completedOrder/numberOfOrder);
                //create object and load to table
                SalesInvoice overDueOrder = new SalesInvoice(resultSet.getString("Sales_Invoice_ID"),
                        resultSet.getString("Due_date"),
                        (((int)(completedOrder/numberOfOrder*100)) + "%"),
                        progressBar);

                overDueOrder.setStatus((-resultSet.getInt("Days"))+" Days");
                overDueOrderList.add(overDueOrder);
            }
            dueDeadlineTable.setItems(overDueOrderList);

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    //menu bar function to allow user from switching to other view
    public void dashboardClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/dashboard.fxml");
    }
    public void leadManagementClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/leadManagement.fxml");
    }
    public void productManagementClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/productManagement.fxml");
    }
    public void rawMaterialClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/RawMaterialManagement.fxml");
    }
    public void supplierClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/supplierManagement.fxml");
    }
    public void salesOrderClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/salesOrderManagement.fxml");
    }
    public void purchaseOrderClick() throws Exception {
        m.switchScene("/fxml/salesDepartment/purchaseOrderManagement.fxml");
    }
    public void logout() throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout?");
        alert.setContentText("Are you sure");
        if(alert.showAndWait().get()== ButtonType.OK){
            m.switchScene("/fxml/loginPage/login.fxml");
        }
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
                resultSet.close();
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

}
