package controller.systemManager;

//Programmer Name: Joe Chok TP061451
//Program Name: modeEmpDetailsController.java
//Description: Edit existing employee details
//First Write: 5 April 2022
//Edited on: 19 April 2022

import JDBC_Connectors.DBConnectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class modeEmpDetailsController implements Initializable
{
    //text field
    @FXML private TextField enterEmpName;
    @FXML private TextField enterEmpId;
    @FXML private TextField enterContact;
    @FXML private TextField enterPassword;

    //choice box
    @FXML private ChoiceBox<String> chooseDepartment;

    // button
    @FXML private Button back;
    @FXML private Button saveRecordButton;

    // label
    @FXML private Label alertMessage;

    private Stage stage;

    // Anchor Pane
    @FXML private AnchorPane scenePane;

    //define arraylist
    List<String> deptNameList=new ArrayList();

    //define variables
    private String empId = "";


    /***************************************** Redirect data from previous window <Methods>  ****************************************/  
    public void showEmpDetails(String ed, String eName, String eCon, String ePass, String deptName) throws SQLException
    {
        // get employee id fixed
        empId = ed;
        enterEmpId.setText(empId);
        enterEmpId.setDisable(true);

        enterEmpName.setText(eName);
        enterContact.setText(eCon);
        enterPassword.setText(ePass);
        chooseDepartment.setValue(deptName);

        alertMessage.setText("");
        saveRecordButton.setDisable(true);

        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }
    
    /***************************************** Cancel Window <Methods>  ****************************************/  
    public void Cancel()
    {
        employeeDetailsController.inSelectMode =false;
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    /***************************************** Check Employee Name Format <Methods>  ****************************************/  
    public boolean isProperStr (String empName)
    {
        String capName = empName;
        capName = capName.replaceAll("[^0-9]", "");// only replace non digit value
        boolean isStry;

        if (empName==null) {
            isStry=false;
        } else {
            if (capName=="") {
                isStry = true;
            } else {
                isStry = false;
            }
        }
        return isStry;
    }

    /***************************************** Check Phone Number Format <Methods>  ****************************************/  
    public boolean isContactCorrect(String contactNo)
    {
        boolean valid =false;
        String capContactNo = contactNo;
        capContactNo = capContactNo.replaceAll("[^0-9]", "");

        int length = capContactNo.length();

        if (length==10) {
            valid=true;
        }

        return valid;
    }

    /***************************************** Insert data to database <Methods>  ****************************************/  
    public void insertData(String q1)throws SQLException
    {
        DBConnectors connectNow = new DBConnectors();
        Connection con = connectNow.getConnection();
        int rs = con.createStatement().executeUpdate(q1);

    }


    /***************************************** Get Department ID <Methods>  ****************************************/  
    public String getDeptId (String contactNumber) throws SQLException
    {
        String deptList = "SELECT Dept_name, Dept_ID from department;";
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        ResultSet rs = con.createStatement().executeQuery(deptList);
        String deptId = "";
        while (rs.next())
        {
            if((rs.getString("Dept_name")).equals(chooseDepartment.getValue()))
            {
                deptId = rs.getString("Dept_ID");
            }
        }
        return deptId;
    }


    /***************************************** Capture department list to arraylist <Methods>  ****************************************/  
    public ArrayList<String> getDeptNameList() throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection();
        ResultSet rs = con.createStatement().executeQuery("SELECT Dept_name from department;");
        while (rs.next()) {
            deptNameList.add(rs.getString("Dept_name"));
        }

        return (ArrayList<String>) deptNameList;
    }

    /***************************************** Enable button and alert text <Mehtods>   ****************************************/  
    public void enAll() {alertMessage.setText("Modified....");saveRecordButton.setDisable(false);}


    /***************************************** Alert Information <Mehtods>   ****************************************/  
    public void warningInformation()
    {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("Employee Details Informations !!\n"+
                            "Employee details had been saved.");
        alert.setTitle("** Information Context **");
        alert.setHeaderText(null);

        alert.showAndWait();
    }

    /***************************************** Back to Previous Button <Action>  ****************************************/  
    public void backToPrevious (ActionEvent event)
    {
        Cancel();
    }

    /***************************************** Save Records Button <Action>  ****************************************/  
    public void saveRecord (ActionEvent event) throws SQLException
    {
        if(isProperStr(enterEmpName.getText())&&(chooseDepartment.getValue())!=null)
        {
            if((isContactCorrect(enterContact.getText()))&&(enterContact.getText())!=null&&(enterPassword.getText()!=null))
            {
                // capture data
                String fixCon = (enterContact.getText()).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2 $3");
                String deptId = getDeptId(chooseDepartment.getValue());
                String empName= enterEmpName.getText();
                String empPassword = enterPassword.getText();

                String updateEmpName = "UPDATE employees SET Emp_name = '"+empName+"' WHERE Emp_ID = " +"'"+empId+"' ;";
                String updateEmpCont = "UPDATE employees SET Emp_contact = '"+fixCon+"' WHERE Emp_ID = " +"'"+empId+"' ;";
                String updateEmpPass = "UPDATE employees SET Emp_pass = '"+empPassword+"' WHERE Emp_ID = " +"'"+empId+"' ;";
                String updateEmpDeptId = "UPDATE employees SET Dept_ID = '"+deptId+"' WHERE Emp_ID = " +"'"+empId+"' ;";
                
                insertData(updateEmpCont);
                insertData(updateEmpName);
                insertData(updateEmpPass);
                insertData(updateEmpDeptId);
                alertMessage.setText("");
                Cancel();
                warningInformation();
            }else{
                alertMessage.setText("Invalid Input !!");
            }
        }else{
                alertMessage.setText("Invalid Input !!");
        }
    }

    /***************************************** Dectect textfield had been edited or not <Action>  ****************************************/  
    public void empNameType(KeyEvent event) { enAll(); }

    public void empContType(KeyEvent event) { enAll(); }

    public void empPassType(KeyEvent event) { enAll(); }

    public void editedDeptName(ActionEvent event) {enAll();}



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try
        {
            chooseDepartment.getItems().addAll(getDeptNameList());
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        // reset lable
        alertMessage.setText("");
        saveRecordButton.setDisable(true);

        
        

        // set action on choices box
        chooseDepartment.setOnAction(this::editedDeptName);




    }
}
