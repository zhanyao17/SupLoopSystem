package base.stockControlDepartment.ArrayList;

public class OverallstocktableModel {
    String materialID;
    String materialName;
    String remainingQuantity;
    String warehouseLabel;
    String purchaseID;
    String orderID;

    public OverallstocktableModel(String materialID, String materialName, String remainingQuantity, String warehouseLabel, String purchaseID, String orderID) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.remainingQuantity = remainingQuantity;
        this.warehouseLabel = warehouseLabel;
        this.purchaseID = purchaseID;
        this.orderID = orderID;
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

    public String getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getWarehouseLabel() {
        return warehouseLabel;
    }

    public void setWarehouseLabel(String warehouseLabel) {
        this.warehouseLabel = warehouseLabel;
    }

    public String getPurchaseID() {
        return purchaseID;
    }

    public void setPurchaseID(String purchaseID) {
        this.purchaseID = purchaseID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}

