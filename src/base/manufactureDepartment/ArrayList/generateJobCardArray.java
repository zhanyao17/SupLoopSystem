package base.manufactureDepartment.ArrayList;

public class generateJobCardArray 
{
    private String Op_Code;
    private String Op_Name;
    private String Jc_Id;

    public generateJobCardArray(String Op_Code, String Op_Name, String Jc_Id)
    {
        this.Op_Code = Op_Code;
        this.Op_Name = Op_Name;
        this.Jc_Id = Jc_Id;
    }

    
    public String getOp_Code(){
        return Op_Code;
    }
    public void setOp_Code(String Op_Code) 
    {
        this.Op_Code = Op_Code;    
    }
    public String getOp_Name(){
        return Op_Name;
    }
    public void setOp_Name(String Op_Name) 
    {
        this.Op_Name = Op_Name;    
    }

    public String getJc_Id(){
        return Jc_Id;
    }
    public void setJc_Id(String Jc_Id) {
        this.Jc_Id = Jc_Id;
    }
}
