package controller;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage primaryStage;

    
    @Override
    public void start(Stage primaryStage){
        try {
            this.primaryStage = primaryStage;
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/stockControlDepartment/MaterialRequestTable.fxml"));
            Scene scene= new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    public void switchScene(String fxmlPath) throws Exception
    {
        Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));
        primaryStage.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch(args);
    }


}