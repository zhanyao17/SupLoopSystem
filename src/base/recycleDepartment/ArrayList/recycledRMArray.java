package base.recycleDepartment.ArrayList;

public class recycledRMArray 
{
    private String rmId;
    private String rmName;
    private String material_quantity;

    public recycledRMArray(String rmId, String rmName, String material_quantity)
    {
        this.rmId = rmId;
        this.rmName = rmName;
        this.material_quantity = material_quantity;
    }
    
    public String getRmId() {
        return rmId;
    }


    public void setRmId(String rmId) {
        this.rmId = rmId;
    }


    public String getMaterial_quantity() {
        return material_quantity;
    }

    public void setMaterial_quantity(String material_quantity) {
        this.material_quantity = material_quantity;
    }


    
    public String getRmName() {
        return rmName;
    }


    public void setRmName(String rmName) {
        this.rmName = rmName;
    }

}
