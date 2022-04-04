package controller.stockControlDepartment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import JDBC_Connectors.DBConnectors;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class updateQuantityController implements Initializable {

    @FXML
    private TextField insertQuantityTxtField;
    @FXML
    private Button updateQuantityButton;
    @FXML
    private Label actualQuantityLabel;
    @FXML
    private Label materialQuantityLabel;
    @FXML
    private Label alertMessage;
    @FXML
    private Label warehouseIDLabel;
    @FXML
    private Label noticeLabel;
    @FXML
    private AnchorPane scenePane;

    //define variable
    private boolean isString;
    private float aQuantity, mQuantity;
    private String wareID;
    private Stage stage;


    public void viewUpdateDetails(String warehouseID, String actualQuantity, String materialQuantity) {

        warehouseIDLabel.setText(warehouseID);
        materialQuantityLabel.setText(materialQuantity);
        actualQuantityLabel.setText(actualQuantity);

        wareID = warehouseID;
        aQuantity = Float.parseFloat(actualQuantity);
        mQuantity = Float.parseFloat(materialQuantity);

        float actualQ = Float.parseFloat(actualQuantity);
        float materialQ = Float.parseFloat(materialQuantity);

        if (materialQ == actualQ) {
            noticeLabel.setText("*** MATERIAL QUANTITY IS COLLECTED! ***");
            updateQuantityButton.setDisable(true);
            insertQuantityTxtField.setDisable(true);
        }

        // Restrict the window event button
        Stage stage = (Stage) scenePane.getScene().getWindow();
        stage.setOnCloseRequest(WindowEvent -> Cancel());
    }

    public void insertData(String q1)throws SQLException{
        DBConnectors connectNow = new DBConnectors();
        Connection con = connectNow.getConnection();
        int rs = con.createStatement().executeUpdate(q1);

    }

    public boolean getIsString(String value)
    {
        try {
            float number = Float.parseFloat(value);
            isString = false;
        } catch (Exception e) {
            isString = true;
        }
        return isString;
    }

    public void Cancel()
    {
        KeepTrackTableController.inUpdateStatusMode = false;
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }


    
    public void updateQuantity(ActionEvent event) throws SQLException {
        if(getIsString(insertQuantityTxtField.getText()) || (insertQuantityTxtField.getText() == null)){
            alertMessage.setText("Invalid Input");
        }else{
            if(((Float.parseFloat(insertQuantityTxtField.getText())) + mQuantity) > aQuantity) {
                alertMessage.setText("Updated Quantity cannot more than Actual Quantity!");
            }else{
                if((Float.parseFloat(insertQuantityTxtField.getText()) + (mQuantity)) == (aQuantity)){

                    String totQ = Float.toString(((Float.parseFloat(insertQuantityTxtField.getText())) + mQuantity));
                    String q1 = "UPDATE warehouse set material_quantity = " +
                            "'" + totQ + "'" + "WHERE Warehouse_ID = " + "'" + wareID + "'";
                    String query2 = "UPDATE warehouse set warehouse_label = 'NC' WHERE Warehouse_ID = " + "'" + wareID + "'";
                    insertData(q1);
                    insertData(query2);
                }else{

                    String totQ = Float.toString(((Float.parseFloat(insertQuantityTxtField.getText())) + mQuantity));
                    String q1 = "UPDATE warehouse set material_quantity = " +
                            "'" + totQ + "'" + "WHERE Warehouse_ID = " + "'" + wareID + "'";
                    insertData(q1);
                }
                Cancel();
            }
        }
    }

    public void backToPrevious(ActionEvent event){
        Cancel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        alertMessage.setText("");
    }
}

