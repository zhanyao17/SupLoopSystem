package base.manufactureDepartment.Methods;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;


import JDBC_Connectors.DBConnectors;
import base.manufactureDepartment.ArrayList.JobCardArray;
import base.manufactureDepartment.ArrayList.generateJobCardArray;
import base.manufactureDepartment.ArrayList.rawMaterialQuantityArray;
import base.manufactureDepartment.ArrayList.salesOrderListArray;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Manufacture_Main 
{

    // observable list 
    private ObservableList<JobCardArray> jobEleCard;
    private ObservableList<rawMaterialQuantityArray> rawMaterialList;    
    private ObservableList<salesOrderListArray> salesOrderList;
    private ObservableList<generateJobCardArray> generateJobCardList;
    
    // varaible - For Production department
    private boolean canGenerated;


    // variable - For Manufcture department
    private String rQ;
    private boolean jStatusResult = true;
    private boolean isMaterialNull;
    private boolean testing = true;
    private boolean isFristOperation = false;
    private boolean isOrderComplete = true;


    /************************************************************************************************************************************/ 
    /****************************************************  PRODUCTION DEPARTMNET Function  *********************************************/ 


    /************************************************  View Overall Sales Order Function  ***********************************************/  // 31 MARCH
    public void viewOverallSalesOrder(String salesOrderListQuery) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            salesOrderList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(salesOrderListQuery);
            while (rs.next()) {
                salesOrderList.add(new salesOrderListArray(rs.getString("Order_ID"), rs.getString("Item_code"), 
                rs.getString("Order_quantity"), rs.getString("Order_date"), rs.getString("Due_date"), 
                rs.getString("Status"),rs.getString("Shipping_status")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    /************************************************  Search Order ID in Sales Order List Function  ***********************************************/  // 31 MARCH
    public void searchOrderIdSalesList(String oId, String salesOrderListQuery) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            salesOrderList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(salesOrderListQuery);
            while (rs.next()) 
            {
                String capture = rs.getString("Order_ID");

                if (capture.toLowerCase(Locale.ROOT).contains(oId.toLowerCase(Locale.ROOT))) 
                {
                    salesOrderList.add(new salesOrderListArray(rs.getString("Order_ID"), rs.getString("Item_code"), 
                    rs.getString("Order_quantity"), rs.getString("Order_date"), rs.getString("Due_date"), 
                    rs.getString("Status"),rs.getString("Shipping_status")));   
                }
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }    
    }


    /*****************************************  Filter Sales Order List Function  ***********************************/  // 31 MARCH
    public void filterStatus(String status, String salesOrderListQuery) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            salesOrderList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(salesOrderListQuery);
            while (rs.next()) 
            {
                if (rs.getString("Status").equals(status)) {
                    salesOrderList.add(new salesOrderListArray(rs.getString("Order_ID"), rs.getString("Item_code"),
                    rs.getString("Order_quantity"), rs.getString("Order_date"), rs.getString("Due_date"), 
                    rs.getString("Status"),rs.getString("Shipping_status")));
                } else {
                    continue;
                }    
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }  
    }

    /*****************************************  Filter Sales Order List Function  ***********************************/  // 31 MARCH
    public void filterOrderStatus(String orderStatus, String salesOrderListQuery) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            salesOrderList = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(salesOrderListQuery);
            while (rs.next()) 
            {
                if (rs.getString("Shipping_status").equals(orderStatus)) {
                    salesOrderList.add(new salesOrderListArray(rs.getString("Order_ID"), rs.getString("Item_code"),
                    rs.getString("Order_quantity"), rs.getString("Order_date"), rs.getString("Due_date"), 
                    rs.getString("Status"),rs.getString("Shipping_status")));
                } else {
                    continue;
                }    
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    
    /*****************************************  Add New Job Card To Table Function ***********************************/  // 31 MARCH
    public void showGenerateJobCardTable(String requiredOperation, String totJcId) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            generateJobCardList = FXCollections.observableArrayList();
            ResultSet rQ = con.createStatement().executeQuery(requiredOperation);
            ResultSet tJ = con.createStatement().executeQuery(totJcId);
            int countJcId = 0;
            
            // generate new job id
            while (tJ.next()) {
                countJcId++;
            }

            while (rQ.next()) {
                countJcId++;
                String newJId = "JC"+Integer.toString(countJcId);
                generateJobCardList.add(new generateJobCardArray(rQ.getString("OP_Code"),rQ.getString("OP_name"),newJId));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // -- FUNCTION FOR VIEW OVERALL JOB CARD
    /****************************************************  Search Order ID for Job Card Function  ***************************************************/  // 31 MARCH
    public void searchOrderId(String oId, String query) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            jobEleCard = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) 
            {
                String jobCardLIst = rs.getString("Order_ID");
                if (jobCardLIst.toLowerCase(Locale.ROOT).contains(oId.toLowerCase(Locale.ROOT))) 
                {
                    jobEleCard.add(new JobCardArray(rs.getString("JC_ID"), rs.getString("Order_ID"),  rs.getString("JC_Status"), 
                    rs.getString("OP_name"),rs.getString("OP_code"), rs.getString("Start_Date"), rs.getString("End_Date"),circleStatus(rs.getString("JC_Status"))));
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

    
    /***********************************************************************************************************************************/ 
    /**************************************************  MANUFACTURE DEPARTMNET Function  **********************************************/

    /*************************************************  View Overall Job Card Function  ************************************************/  // 6 APRIL
    public Circle circleStatus(String jcStatus)
    {
        Circle cS = new Circle(7);

        // pending, In_complete and completed   
        if (jcStatus.equals("Pending")) 
        {
            cS.setFill(Color.rgb(252, 3, 3)); // red colour
        } else {
            if (jcStatus.equals("In_complete")) 
            {
                cS.setFill(Color.rgb(252, 152, 3)); // orange colour
            } else {
                cS.setFill(Color.rgb(82, 252, 3)); // green colour
            }
        }
        return cS;
    }



    /*************************************************  View Overall Job Card Function  ************************************************/  // 26 MARCH
    public void selectEleJobCard(String showJobCardListQuery) 
    {
        try {
            // Datebase connections
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            jobEleCard = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(showJobCardListQuery);
            while(rs.next())
            {  
                jobEleCard.add(new JobCardArray(rs.getString("JC_ID"), rs.getString("Order_ID"),  rs.getString("JC_Status"), 
                rs.getString("OP_name"),rs.getString("OP_code"), rs.getString("Start_Date"), rs.getString("End_Date"),circleStatus(rs.getString("JC_Status"))));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**************************************************  Filter JC_Status Function  ***************************************************/  // 26 MARCH
    
    // pending // completed // In_complete -> INCLUDED FOUR TYPE

    public void filterEleJobCard(String jStatus, String showJobCardListQuery ) 
    {
        try {
            // Datebase connections
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            jobEleCard = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(showJobCardListQuery); // final list
            while (rs.next()) 
            {
                if (rs.getString("JC_Status").equals(jStatus)) 
                {
                    jobEleCard.add(new JobCardArray(rs.getString("JC_ID"), rs.getString("Order_ID"),  rs.getString("JC_Status"), 
                    rs.getString("OP_name"),rs.getString("OP_code"), rs.getString("Start_Date"), rs.getString("End_Date"),circleStatus(rs.getString("JC_Status"))));
                } else {
                    continue;
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***************************************************  Search Job Card ID Function  *************************************************/  // 26 MARCH
    public void searchJobCard(String jId, String showJobCardListQuery) 
    {
        try {
            // Datebase connections
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            // Obervable list
            jobEleCard = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery(showJobCardListQuery);
            while (rs.next()) 
            {
                String jobCardLIst = rs.getString("JC_ID");
                if (jobCardLIst.toLowerCase(Locale.ROOT).contains(jId.toLowerCase(Locale.ROOT))) 
                {
                    jobEleCard.add(new JobCardArray(rs.getString("JC_ID"), rs.getString("Order_ID"),  rs.getString("JC_Status"), 
                    rs.getString("OP_name"),rs.getString("OP_code"), rs.getString("Start_Date"), rs.getString("End_Date"),circleStatus(rs.getString("JC_Status"))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /***************************************** Show Total Output Item Quanitty Needed Function  ****************************************/  // 27 MARCH
    public String showRequiredQuantity(String jId, String getRequiredQuantityQuery) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            ResultSet rs = con.createStatement().executeQuery(getRequiredQuantityQuery);
            while (rs.next()) 
            {
                if (jId.equals(rs.getString("JC_ID"))) 
                {
                    rQ = rs.getString("Order_quantity");
                    break;
                } else {}    
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rQ; 
    }

    /*************************************************  Insert Data Into MySQl Function  ***********************************************/  // 28 MARCH
    public void insertData(String query) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            int rs = con.createStatement().executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
    

    /*******************************************  Get Requried RM With Total Quantity Function  ***************************************/  // 29 MARCH
    public void showRequiredRmQuantity(String jobCardRm, String orderMaterial) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            rawMaterialList = FXCollections.observableArrayList();
            ResultSet requiredRm = con.createStatement().executeQuery(jobCardRm);
            ResultSet orderRm = con.createStatement().executeQuery(orderMaterial);
            ArrayList<rawMaterialQuantityArray> tempOrderRm = new ArrayList<rawMaterialQuantityArray>();
            
            // Reformat orderrm <resultset> for validate
            while (orderRm.next()) {
                tempOrderRm.add(new rawMaterialQuantityArray(orderRm.getString("RM_ID"), 
                orderRm.getString("material_quantity"), orderRm.getString("Warehouse_ID")));
            }
            while (requiredRm.next()) 
                {
                    for (rawMaterialQuantityArray capData : tempOrderRm ) 
                    {
                        if (requiredRm.getString("RM_ID").equals(capData.getRm_Id())) {
                            // System.out.println("CAPTURED "+capData.getRm_Id()+"\t"+capData.getMat_Quantity());
                            rawMaterialList.add(new rawMaterialQuantityArray(capData.getRm_Id(), 
                            capData.getMat_Quantity(), capData.getWa_Id()));
                            break;
                        } else {
                            continue;
                        }
                    }    
                }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*********************************** Detect Null Value In Job Card Required Material Function  *************************************/  // 29 MARCH
    public boolean isRawMaterialNull(String jobCardRm)
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            rawMaterialList = FXCollections.observableArrayList();
            ResultSet requiredRm = con.createStatement().executeQuery(jobCardRm);
            while (requiredRm.next()) 
            {
                if (requiredRm.getString("RM_ID")== null) 
                {
                    isMaterialNull= true;    
                } else {
                    isMaterialNull = false;
                }    
            }
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMaterialNull;
    }

    /**************************************** Check Required Raw Material Reached Or Not Function  *************************************/  // 28 MARCH
    public boolean isReady(String queyr, String status){
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            ResultSet rs = con.createStatement().executeQuery(queyr);

            while (true) 
            {
                if (rs.next()==false) {
                    break;   
                }else{
                    if (rs.getString("warehouse_label").equals(status)){
                        testing = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return testing;
    }


    /************************************************* Check Previous Job Status Function  ********************************************/  // 28 MARCH
    public boolean checkStatus(String searchJobCardStatus ) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            ResultSet rs = con.createStatement().executeQuery(searchJobCardStatus);

            while (true) {
                if (rs.next()==false) // Capture first operation in a order
                {
                    isFristOperation = true;
                    break;    
                } else {
                    if (!rs.getString("JC_Status").equals("Completed")) {
                        jStatusResult = false;
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }   
        return jStatusResult;
    }

    public ArrayList<String> getDeptName(){
        ArrayList<String> dptName = new ArrayList<>();

        return (ArrayList<String>) dptName;
    }
    /********************************************* Check Order Have Done Or Not Function  *********************************************/  // 30 March
    public boolean checkOrderProgress(String orderId) 
    {
        try {
            DBConnectors demo = new DBConnectors();
            Connection con = demo.getConnection(); 
            String query = "SELECT JC_Status FROM job_card WHERE Order_ID = "+"'"+orderId+"' ;";
            ResultSet rs = con.createStatement().executeQuery(query);
            while (true) {
                if (rs.next()==false) {
                    break;
                } else {
                    if (!rs.getString(1).equals("Completed")) {
                        isOrderComplete = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isOrderComplete;    
    }

    
    /******************************************************  Return Value Function  ****************************************************/ 
    public ObservableList<JobCardArray> getJobEleCard(){ return jobEleCard;}
    public ObservableList<rawMaterialQuantityArray> getRawMaterialList(){ return rawMaterialList;}
    public boolean getIsFirstOperation(){ return isFristOperation;}
    public ObservableList<salesOrderListArray> getSalesOrderList (){return salesOrderList;}
    public ObservableList<generateJobCardArray> getGenerateJobCardList(){return generateJobCardList;}
    
        

    
}
