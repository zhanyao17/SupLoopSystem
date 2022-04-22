package controller.loginPage;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: loginController.java
//Description: To login into the system
//First Written on: 18 April 2022
//Edited on: 18 April 2022

import JDBC_Connectors.DBConnectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import controller.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
    @FXML
    private TextField IDText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Label errorLabel;

    public static String employeeName ="";

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;

    public void login(){
        if(IDText.getText().isEmpty() || passwordText.getText().isEmpty()){
            errorLabel.setText("Please enter your ID and Password!");
        }else{
            try{
                //retrieve employees data from database
                connection = new DBConnectors().getConnection();
                statement = connection.prepareStatement("SELECT * FROM employees WHERE Emp_ID = ? AND Emp_pass = ?");
                statement.setString(1,IDText.getText());
                statement.setString(2,passwordText.getText());
                resultSet = statement.executeQuery();
                if(resultSet.next()){
                    if(resultSet.getString("Emp_ID").equals(IDText.getText()) &&
                            resultSet.getString("Emp_pass").equals(passwordText.getText())){

                        String departmentID = resultSet.getString("Dept_ID");
                        employeeName = resultSet.getString("Emp_name");
                        if(departmentID.equals("D0001")){
                            new Main().switchScene("/fxml/stockControlDepartment/overallstocktable.fxml");

                        }else if(departmentID.equals("D0002")){
                            new Main().switchScene("/fxml/salesDepartment/dashboard.fxml");

                        }else if(departmentID.equals("D0003")){
                            new Main().switchScene("/fxml/productionDepartment/salesOrderListPreview.fxml");

                        }else if(departmentID.equals("D0004")){
                            new Main().switchScene("/fxml/recycleDepartment/materialTransfer_recycle.fxml");

                        }else if(departmentID.equals("D0005")){
                            new Main().switchScene("/fxml/systemManager/employeeDetails.fxml");

                        }else if(departmentID.equals("D0006")){
                            new Main().switchScene("/fxml/manufactureDepartment/ElectrodeProduction/JobCardPreview.fxml");

                        }else if(departmentID.equals("D0007")){
                            new Main().switchScene("/fxml/manufactureDepartment/JerryRollConstruction/JobCardPreview_JerryRoll.fxml");

                        }else if(departmentID.equals("D0008")){
                            new Main().switchScene("/fxml/manufactureDepartment/CellAssemblyStation/JobCardPreview_CellAssembly.fxml");

                        }else if(departmentID.equals("D0009")){
                            new Main().switchScene("/fxml/manufactureDepartment/PackingStation/JobCardPreview_Packing.fxml");

                        }
                    }else{
                        errorLabel.setText("Incorrect ID or Password!");
                    }
                }else{
                    errorLabel.setText("Incorrect ID or Password!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
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