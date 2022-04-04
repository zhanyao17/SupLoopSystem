package base.stockControlDepartment.ArrayList;

public class KeepTrackTableModel {
    private String materialID;
    private String warehouseID;
    private String warehouseLabel;
    private String materialQuantity;
    private String actualQuantity;
    private String purchaseTotQuantity;
    private String orderID;

    public KeepTrackTableModel(String materialID, String warehouseID, String warehouseLabel, String materialQuantity, String actualQuantity, String purchaseTotQuantity, String orderID) {
        this.materialID = materialID;
        this.warehouseID = warehouseID;
        this.warehouseLabel = warehouseLabel;
        this.materialQuantity = materialQuantity;
        this.actualQuantity = actualQuantity;
        this.purchaseTotQuantity = purchaseTotQuantity;
        this.orderID = orderID;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
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

    public String getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(String materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public String getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(String actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getPurchaseTotQuantity() {
        return purchaseTotQuantity;
    }

    public void setPurchaseTotQuantity(String purchaseTotQuantity) {
        this.purchaseTotQuantity = purchaseTotQuantity;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
