package base.manufactureDepartment.ArrayList;

import javafx.scene.shape.Circle;

public class JobCardArray {
    private String Jc_Id;
    private String Order_Id;
    private String Op_Code;
    private String Jc_Status;
    private String Start_Date;
    private String End_Date;
    private String Op_Name;
    private Circle colourStatus;

    public JobCardArray(String Jc_Id,String Order_Id, String Jc_Status, String Op_Name, String Op_Code,  String Start_Date, String End_Date, Circle colourStatus)
    {
        this.Jc_Id = Jc_Id;
        this.Order_Id = Order_Id;
        this.Jc_Status = Jc_Status;
        this.Op_Name = Op_Name;
        this.Op_Code = Op_Code;
        this.Start_Date = Start_Date;
        this.End_Date = End_Date;
        this.colourStatus = colourStatus;
    }

    public String getJc_Id() 
    {
        return Jc_Id;    
    }
    public void setJc_Id(String Jc_Id) 
    {
        this.Jc_Id = Jc_Id;    
    }

    public String getOrder_Id(){
        return Order_Id;
    }
    
    public void setOrder_Id(String Order_Id) 
    {
        this.Order_Id = Order_Id;    
    }

    public String getOp_Code(){
        return Op_Code;
    }
    public void setOp_Code(String Op_Code) 
    {
        this.Op_Code = Op_Code;
    }
    public String getJc_Status(){
        return Jc_Status;
    }
    public void setJc_Status(String Jc_Status) {
        this.Jc_Status = Jc_Status;
    }
    public String getStart_Date(){
        return Start_Date;
    }
    public void setStart_Date(String Start_Date) {
        this.Start_Date = Start_Date;
    }
    public String getEnd_Date (){
        return End_Date;
    }
    public void setEnd_Date(String End_Date) {
        this.End_Date = End_Date;
    }
    public String getOp_Name(){
        return Op_Name;
    }
    public void setOp_Name(String Op_Name) {
        this.Op_Name= Op_Name;
    }

    public Circle getColourStatus() {
        return colourStatus;
    }

    public void setColourStatus(Circle colourStatus) {
        this.colourStatus = colourStatus;
    }

}
