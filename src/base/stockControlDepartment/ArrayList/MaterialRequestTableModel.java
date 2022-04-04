package base.stockControlDepartment.ArrayList;

public class MaterialRequestTableModel {
    private String workStationID;
    private String materialID;
    private String materialName;
    private String warehouseID;
    private String warehouseLabel;

    public MaterialRequestTableModel(String workStationID, String materialID, String materialName, String warehouseID, String warehouseLabel) {
        this.workStationID = workStationID;
        this.materialID = materialID;
        this.materialName = materialName;
        this.warehouseID = warehouseID;
        this.warehouseLabel = warehouseLabel;
    }

    public String getWorkStationID() {
        return workStationID;
    }

    public void setWorkStationID(String workStationID) {
        this.workStationID = workStationID;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getWarehouseLabel() {
        return warehouseLabel;
    }

    public void setWarehouseLabel(String warehouseLabel) {
        this.warehouseLabel = warehouseLabel;
    }
}