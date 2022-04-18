package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: productManagement.java
//Description: To search and add order for the customer
//First Written on: 8 April 2022
//Edited on: 18 April 2022

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class searchLeadController extends leadManagementController implements Initializable {


    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    //pass selected customer details
    public void applyItem(){
        if(customerSelected!= null){
            addNewOrderController.passCustomer = customerSelected;
        }
        cancel();
    }

    //close window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }

}
