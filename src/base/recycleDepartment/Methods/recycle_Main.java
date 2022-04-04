package base.recycleDepartment.Methods;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import JDBC_Connectors.DBConnectors;

public class recycle_Main 
{

    // define variable
    private String rmId;
    private boolean isRepeat = false;

    // define array list
    private List<String> rmNameList=new ArrayList<>();

    /*************************************************  Insert Data Into MySQl Function  ***********************************************/  // 2 APRIL
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


    /************************************************* Return raw Material name ArrayList  ***********************************************/  // 2 APRIL
    public ArrayList<String> getRMNameList(String query) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) {
            rmNameList.add(rs.getString("Raw_material_name"));
        }
        
        return (ArrayList<String>) rmNameList;
    }



    /************************************************* Get raw Material ID Function  ***********************************************/  // 2 APRIL
    public String getRmId(String rmName ,String query) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) 
        {
            if (rs.getString("Raw_material_name").equals(rmName)) 
            {
                rmId = rs.getString("RM_ID");
                break;
            } else {
                continue;
            }    
        }
        return rmId;
    }
    
    /************************************************* Return Recycle_Material_ID INt  ***********************************************/  // 2 APRIL
    public int generateRecycleMaterialID(String recycleMaterialQuery) throws SQLException
    {
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(recycleMaterialQuery);
        int countReMat = 0;
        while (rs.next()) {
            countReMat++;
        }
        return countReMat;
    }

    /************************************************* Generate Warehouse_ID INT  ***********************************************/  // 2 APRIL
    public int generateWarehouseId(String warehouseQuery) throws SQLException{
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(warehouseQuery);
        int countWId = 0;
        while (rs.next()) {
            countWId++;
        }
        return countWId;
    }

    /************************************************* Check repeat RMID on Recycle Department  ***********************************************/  // 3 APRIL
    public String getRepeatRM(String rId, String totRecycleQuery) throws SQLException
    {
        String capRecycleId = ""; 
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(totRecycleQuery);
        if (rs.next()) 
        {
            if (rs.getString("RM_ID").equals(rId)) 
            {
                capRecycleId = rs.getString("recycle_material_ID");
                isRepeat = true;
            }    
        }
        return capRecycleId;
    }

    /************************************************* Validate Input Boolean  ***********************************************/  // 3 APRIL
    public String getExistingQuantity(String rId, String warehouseQuery) throws SQLException
    {
        String capMatQuantity = "";
        DBConnectors demo = new DBConnectors();
        Connection con = demo.getConnection(); 
        ResultSet rs = con.createStatement().executeQuery(warehouseQuery);
        while (rs.next()) 
        {
            if (rs.getString("recycle_material_ID")!=null) 
            {
                if (rs.getString("recycle_material_ID").equals(rId)) 
                {
                    capMatQuantity = rs.getString("material_quantity");
                    break;
                }
            }    
        }
        return capMatQuantity;
    }


    /************************************************* Validate Input Boolean  ***********************************************/  // 2 APRIL
    public boolean isString(String value){
        boolean dataType;
        try {
            float number = Float.parseFloat(value);
            dataType = false;
        } catch (Exception e) {
            dataType = true;
        }
        return dataType;
    };

    /************************************************* Validate Input Boolean  ***********************************************/  
    public boolean getIsRepeat(){ return isRepeat;}

}
