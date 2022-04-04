package base.stockControlDepartment.ArrayList;

public class notAssignMatModel 
{
    private String warehouseId;
    private String rMId;
    private String rMName;
    private String matQuantity;
    private String warehouseLabel;

    public notAssignMatModel(String warehouseId, String rMId, String rMName, String matQuantity,String warehouseLabel ) 
    {
        this.warehouseId = warehouseId;
        this.rMId = rMId;
        this.rMName = rMName;
        this.matQuantity = matQuantity;
        this.warehouseLabel = warehouseLabel;    
    }
    

    public String getWarehouseId() {
        return warehouseId;
    }


    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getRMId() {
        return rMId;
    }


    public void setRMId(String rMId) {
        this.rMId = rMId;
    }


    public String getRMName() {
        return rMName;
    }

    public void setRMName(String rMName) {
        this.rMName = rMName;
    }


    public String getMatQuantity() {
        return matQuantity;
    }


    public void setMatQuantity(String matQuantity) {
        this.matQuantity = matQuantity;
    }

    public String getWarehouseLabel() {
        return warehouseLabel;
    }


    public void setWarehouseLabel(String warehouseLabel) {
        this.warehouseLabel = warehouseLabel;
    }

}
