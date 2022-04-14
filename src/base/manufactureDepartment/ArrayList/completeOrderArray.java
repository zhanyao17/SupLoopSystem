package base.manufactureDepartment.ArrayList;

public class completeOrderArray {
    private String Order_Id;
    private String Item_Code;
    private String Order_Quantity;
    private String Order_Date;
    private String Due_Date;
    private String Order_Status;

    public completeOrderArray(String Order_Id, String Item_Code, String Order_Quantity, String Order_Date, String Due_Date, String Order_Status)
    {
        this.Order_Id = Order_Id;
        this.Item_Code = Item_Code;
        this.Order_Quantity = Order_Quantity;
        this.Order_Date = Order_Date;
        this.Due_Date = Due_Date;
        this.Order_Status = Order_Status;
    }

    public String getOrder_Id(){
        return Order_Id;
    }
    
    public void setOrder_Id(String Order_Id) 
    {
        this.Order_Id = Order_Id;    
    }

    public String getItem_Code(){
        return Item_Code;
    }

    public void setItem_Code(String Item_Code) {
        this.Item_Code = Item_Code;
    }
    
    public String getOrder_Quantity (){
        return Order_Quantity;
    }
    public void setOrder_Quantity(String Order_Quantity) 
    {
        this.Order_Quantity = Order_Quantity;    
    }
    
    public String getOrder_Date(){
        return Order_Date;
    }
    public void setOrder_Date(String Order_Date) 
    {
        this.Order_Date = Order_Date;    
    }

    public String getDue_Date(){
        return Due_Date;
    }
    public void setDue_Date(String Due_Date) 
    {
        this.Due_Date = Due_Date;    
    }
    
    public String getOrder_Status(){
        return Order_Status;
    }
    public void setOrder_Status(String Order_Status) 
    {
        this.Order_Status = Order_Status;    
    }
}
