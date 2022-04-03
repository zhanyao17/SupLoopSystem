package base.manufactureDepartment.ArrayList;

public class rawMaterialQuantityArray 
{
    private String Rm_Id;
    private String Mat_Quantity;
    private String Wa_Id;
    
    public rawMaterialQuantityArray(String Rm_Id, String Mat_Quantity, String Wa_Id)
    {
        this.Rm_Id = Rm_Id;
        this.Mat_Quantity = Mat_Quantity;
        this.Wa_Id = Wa_Id;
    }

    public String getRm_Id(){
        return Rm_Id;
    }
    public void setRm_Id(String Rm_Id) 
    {
        this.Rm_Id = Rm_Id;    
    }

    public String getMat_Quantity()
    {
        return Mat_Quantity;
    }
    public void setMat_Quantity(String Mat_Quantity) 
    {
        this.Mat_Quantity = Mat_Quantity;
    }

    public String getWa_Id(){
        return Wa_Id;
    }
    public void setWa_Id(String Wa_Id) 
    {
        this.Wa_Id = Wa_Id;    
    }
}
