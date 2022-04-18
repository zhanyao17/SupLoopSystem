package controller.salesDepartment;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: searchItemController.java
//Description: to add item to order
//First Written on: 8 April 2022
//Edited on: 18 April 2022

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import base.salesDepartment.ArrayLists.Item;
import base.salesDepartment.Method.publicMethod;


public class searchItemController extends productManagementController{

    @FXML
    private AnchorPane scenePane;

    private Stage stage;

    @FXML
    private TextField itemAmountText;

    @FXML
    private Label errorLabel;

    public void applyItem(){

        if(itemSelected!= null){
            //validate data
            if(!publicMethod.checkNumeric(itemAmountText.getText())){
                errorLabel.setText("Invalid amount");
                return;
            }else if(Integer.parseInt(itemAmountText.getText()) <=0 || Integer.parseInt(itemAmountText.getText()) > 99999){
                errorLabel.setText("Please enter the amount in range between 1 - 99999");
                return;
            }
            //get amount
            int amount = Integer.parseInt(itemAmountText.getText()); //add 10 more

            for(Item duplicateItem : addNewOrderController.purchaseItemList){
                if(itemSelected.getItemCode().equals(duplicateItem.getItemCode())){
                    amount += duplicateItem.getAmount(); //10 + current stock
                    addNewOrderController.purchaseItemList.remove(duplicateItem); //delete the duplicate item in table
                    break;
                }
            }
            //pass item to the table
            itemSelected.setAmount(amount);
            addNewOrderController.purchaseItemList.add(itemSelected); //add back
            cancel();
        }else{
            errorLabel.setText("Please select an item from the table!");
        }

    }

    //close window
    public void cancel(){
        stage = (Stage) scenePane.getScene().getWindow(); //get the current stage or window
        stage.close();
    }



}
