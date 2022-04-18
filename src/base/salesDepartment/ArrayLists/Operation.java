package base.salesDepartment.ArrayLists;

//Programmer Name : Kang Jia Yong TP060575
//Program Name: Operation.java
//Description: Operation Class
//First Written on: 1 April 2022
//Edited on: 18 April 2022

public class Operation implements Comparable<Operation> {
    private String operationCode;
    private String operationName;
    private float operationCost;
    private int operationTime;

    public Operation(String operationCode, String operationName, float operationCost) {
        this.operationCode = operationCode;
        this.operationName = operationName;
        this.operationCost = operationCost;
    }
    public Operation(String operationCode, String operationName, float operationCost, int operationTime) {
        this.operationCode = operationCode;
        this.operationName = operationName;
        this.operationCost = operationCost;
        this.operationTime = operationTime;
    }

    public String getOperationCode() { return operationCode; }

    public String getOperationName() {
        return operationName;
    }

    public float getOperationCost() {
        return operationCost;
    }

    public int getOperationTime() { return operationTime; }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setOperationCost(float operationCost) {
        this.operationCost = operationCost;
    }

    public void setOperationTime(int operationTime) {
        this.operationTime = operationTime;
    }

    @Override
    public int compareTo(Operation o) {
        //int code1 = Integer.parseInt(this.operationCode.substring(2,5));
        return this.operationCode.compareTo(o.operationCode);
    }
}
