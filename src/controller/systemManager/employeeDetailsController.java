package controller.systemManager;

import JDBC_Connectors.DBConnectors;
import base.systemManager.arrayList.employeeDetailsArray;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class employeeDetailsController implements Initializable
{
    //text field
    @FXML private TextField enterEmployeeId;

    //choice box
    @FXML private ChoiceBox<String> enterDeptName;

    //reset button
    @FXML private Button resetButton;

    //table view and column
    @FXML private TableColumn<employeeDetailsArray,String> employeeIdColumn;
    @FXML private TableColumn<employeeDetailsArray,String> employeeNameColumn;
    @FXML private TableColumn<employeeDetailsArray,String> employeeContactColumn;
    @FXML private TableColumn<employeeDetailsArray,String> departmentIdColumn;
    @FXML private TableColumn<employeeDetailsArray,String> departmentNameColumn;

    @FXML private TableView<employeeDetailsArray> tableView;

    //observable list
    private ObservableList<employeeDetailsArray> empList;

    //define root
    private Parent root;

    // define varaible 
    employeeDetailsArray temp;
    private Date lastClickTime;

    //define arraylist
    List<String> deptNameList=new ArrayList();

    // define static variables
    public static boolean inSelectMode = false;

    //define statement
    private String employeeListQuery =  "SELECT e.Emp_ID, e.Emp_name,e.Emp_contact, e.Dept_ID,e.Emp_pass, d.Dept_name " +
                                        "from employees e " +
                                        "INNER JOIN department d on d.Dept_ID = e.Dept_ID;";

    private String employeeListQuery1 =  "SELECT e.Emp_ID, e.Emp_name,e.Emp_contact, e.Dept_ID, d.Dept_name " +
                                            "from employees e " +
                                             "INNER JOIN department d on d.Dept_ID = e.Dept_ID " +
                                            "ORDER BY (regexp_replace(e.Emp_ID,'[^0-9]','')) +0 DESC LIMIT 1;";


    /***************************************** Show All Employee details <Methods>  ****************************************/  
    public void showEmployeeList(String employeeListQuery) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        // Observable list
        empList = FXCollections.observableArrayList();
        ResultSet rs = con.createStatement().executeQuery(employeeListQuery);
        while (rs.next())
        {
            empList.add(new employeeDetailsArray(rs.getString("Emp_ID"), rs.getString("Emp_name"),
                    rs.getString("Emp_contact"), rs.getString("Dept_ID"),
                    rs.getString("Dept_name")));
        }
    }

    /***************************************** Insert Data into Database <Methods>  ****************************************/  
     public void insertData(String q1)throws SQLException
     {
         DBConnectors connectNow = new DBConnectors();
         Connection con = connectNow.getConnection();
         int rs = con.createStatement().executeUpdate(q1);
     }
    
    /***************************************** Filter Department Name <Methods>  ****************************************/  
    public void filterDeptName(String deptName, String employeeListQuery) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        // Observable list
        empList = FXCollections.observableArrayList();
        ResultSet rs = con.createStatement().executeQuery(employeeListQuery);
        while (rs.next())
        {
            if((rs.getString("Dept_name")).equals(deptName))
            {
                empList.add(new employeeDetailsArray(rs.getString("Emp_ID"), rs.getString("Emp_name"),
                        rs.getString("Emp_contact"), rs.getString("Dept_ID"),
                        rs.getString("Dept_name")));
            }
        }
    }

    /***************************************** Filter Employee ID <Methods>  ****************************************/  
    public void filterEmpId(String eId, String employeeListQuery)
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection();
            // Obervable list
            empList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(employeeListQuery);
            while (rs.next())
            {
                String empIdList = rs.getString("Emp_ID") + rs.getString("Emp_name");
                if (empIdList.toLowerCase(Locale.ROOT).contains(eId.toLowerCase(Locale.ROOT)))
                {
                    empList.add(new employeeDetailsArray(rs.getString("Emp_ID"), rs.getString("Emp_name"),
                            rs.getString("Emp_contact"), rs.getString("Dept_ID"),
                            rs.getString("Dept_name")));
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************** Capture Department Name to ArrayList <Methods>  ****************************************/  
    public ArrayList<String> getDeptNameList(String query) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        ResultSet rs = con.createStatement().executeQuery(query);
        deptNameList.add("All");
        while (rs.next()) {
            deptNameList.add(rs.getString("Dept_name"));
        }

        return (ArrayList<String>) deptNameList;
    }

    /*************************x**************** Generate new emp id <Methods>  ****************************************/  
    public String generateEmpId(String empListQuery) throws SQLException{
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        ResultSet rs = con.createStatement().executeQuery(empListQuery);
        String newEId = "";
        rs.next();
        int captureNum = (Integer.parseInt((rs.getString("Emp_ID")).replaceAll("[^0-9]", ""))+1);
        
        // reformat id to fit with 0000 point
        newEId = ("E"+(String.format("%04d", captureNum)));

        return newEId;
    }

    /*************************x**************** Generate new emp id <Methods>  ****************************************/  
    public String getEmpPass(String eId,String empListQuery) throws SQLException{
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        ResultSet rs = con.createStatement().executeQuery(empListQuery);
        String empPass = "";
        while (rs.next()) 
        {
            if ((eId).equals(rs.getString("Emp_ID"))) {
                empPass = rs.getString("Emp_pass");
                break;
            }
        }
        return empPass;
    }



    /*************************x**************** Pop up Add New Employee Page <Methods>  ****************************************/  
    public void popUpAddNewEmpPage (String fxmlPath)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();
            // change controller
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            addNewEmpController addEmp = loader.getController();
            
            // generate new id
            String eId = generateEmpId(employeeListQuery1);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            addEmp.showEmployeeId(eId);
            stage.showAndWait();
            
            // refresh table view
            showEmployeeList(employeeListQuery);
            tableView.setItems(empList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************************x**************** Pop up MOdify Emp List <Methods>  ****************************************/  
    public void popUpModePage( String ed, String eName, String eCon, String ePass, String deptName, String fxmlPath) 
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root = loader.load();

            // create scene & stage
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // get methods from other controller
            modeEmpDetailsController addEmp = loader.getController();

            stage.setScene(scene);

            // restrict window size in coordinates
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);

            addEmp.showEmpDetails(ed, eName, eCon, ePass, deptName);
            stage.showAndWait();
            
            // refresh table view
            showEmployeeList(employeeListQuery);
            tableView.setItems(empList);
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    /***************************************** Alert Information <Mehtods>   ****************************************/  
    public void warningInformation() 
    {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("Employee Details Informations !!\n"+
                            "Employee details had sucessfully deleted.");
        alert.setTitle("** Information Context **");
        alert.setHeaderText(null);

        alert.showAndWait();
    }

    /***************************************** Search Employee ID Text field <Action>  ****************************************/  
    public void searchEmpId(KeyEvent event)
    {
        try {
            filterEmpId(enterEmployeeId.getText(),employeeListQuery);
            tableView.setItems(empList);
            enterDeptName.setValue("All");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************** Filter Deaprtment Name Choices Box <Action>  ****************************************/  
    public void searchDeptName(ActionEvent event)
    {
        try
        {
            if((enterDeptName.getValue()).equals("All"))
            {
              showEmployeeList(employeeListQuery);
              tableView.setItems(empList);
              resetButton.setDisable(true);
            }else{
                filterDeptName(enterDeptName.getValue(),employeeListQuery);
                tableView.setItems(empList);
                resetButton.setDisable(false);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /***************************************** Reset Table  Button <Action>  ****************************************/  
    public void resetTable(ActionEvent event) throws SQLException
    {
        showEmployeeList(employeeListQuery);
        tableView.setItems(empList);
        resetButton.setDisable(true);
        enterDeptName.setValue("All");
    }

    /***************************************** Add new employee Button <Action>  ****************************************/  
    public void addEmp(ActionEvent event)
    {
        if (inSelectMode) {

        } else {
            inSelectMode = true;
            popUpAddNewEmpPage("/fxml/systemManager/addEmployee.fxml");    
        }
    }

    /***************************************** Delete Record Button <Action>  ****************************************/  
    public void deleteRecord(ActionEvent event) throws SQLException
    {
        // seletect object from table view
        employeeDetailsArray selectedEmp = tableView.getSelectionModel().getSelectedItem();
        
        // validate is there any data selected
        if (selectedEmp!=null) 
        {
            String dltEmpId ="DELETE FROM employees WHERE Emp_ID = "+"'"+selectedEmp.getEmployeeId()+"' ;"; 
            insertData(dltEmpId);
            warningInformation();
        }
        
        // refresh table view
        showEmployeeList(employeeListQuery);
        tableView.setItems(empList);
    }

    /***************************************** Select Column for modify Emp List <Action>  ****************************************/  
    public void selectColumn() throws SQLException
    {
        employeeDetailsArray row = tableView.getSelectionModel().getSelectedItem();
        if(row == null) return;
        if(row!=temp) {
            temp=row;
            lastClickTime = new Date(); // first click
        }else{
            Date now = new Date(); // second click
            long diff = now.getTime()-lastClickTime.getTime();
            if (inSelectMode) {
                
            } else {
                if (diff<300) {
                    inSelectMode = true;
                    String fxmlPath = "/fxml/systemManager/modeEmpDetail.fxml";
                    popUpModePage(row.getEmployeeId(), row.getEmployeeName(), row.getEmployeeContactNumber(), getEmpPass(row.getEmployeeId(),employeeListQuery), row.getDepartmentName(), fxmlPath);

                } else {
                    lastClickTime = new Date();
                }
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try
        {
            showEmployeeList(employeeListQuery);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        // define multiple choice
        try
        {
            enterDeptName.setValue("All");
            enterDeptName.getItems().addAll(getDeptNameList("SELECT Dept_name from department;"));
            enterDeptName.setOnAction(this :: searchDeptName);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

       // disable reset button
        resetButton.setDisable(true);

        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<employeeDetailsArray,String>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<employeeDetailsArray,String>("employeeName"));
        employeeContactColumn.setCellValueFactory(new PropertyValueFactory<employeeDetailsArray,String>("employeeContactNumber"));
        departmentIdColumn.setCellValueFactory(new PropertyValueFactory<employeeDetailsArray,String>("departmentId"));
        departmentNameColumn.setCellValueFactory(new PropertyValueFactory<employeeDetailsArray,String>("departmentName"));

        tableView.setItems(empList);
    }
}
