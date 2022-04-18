package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: RawMaterial.java
//Description: raw material class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class RawMaterial {
    private String materialID;
    private String materialName;
    private float materialQuantity;
    private float materialCost ;
    private String materialOperation;
    private String status;

    public RawMaterial(String materialID, String materialName, float materialCost, String status) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.materialCost = materialCost;
        this.status = status;
    }


    public RawMaterial(String materialID, String materialName, float materialQuantity, float materialCost) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.materialQuantity = materialQuantity;
        this.materialCost = materialCost;
    }

    public RawMaterial(String materialID, String materialName, float materialQuantity, float materialCost, String materialOperation) {
        this.materialID = materialID;
        this.materialName = materialName;
        this.materialQuantity = materialQuantity;
        this.materialCost = materialCost;
        this.materialOperation = materialOperation;
    }

    public String getMaterialID() {
        return materialID;
    }

    public String getMaterialName() {
        return materialName;
    }

    public float getMaterialQuantity() {
        return materialQuantity;
    }

    public float getMaterialCost() {
        return materialCost;
    }

    public String getMaterialOperation() {
        return materialOperation;
    }

    public String getStatus() {
        return status;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public void setMaterialQuantity(float materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public void setMaterialCost(float materialCost) {
        this.materialCost = materialCost;
    }

    public void setMaterialOperation(String materialOperation) {
        this.materialOperation = materialOperation;
    }
}
