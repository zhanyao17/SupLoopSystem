package base.systemManager.arrayList;

public class employeeDetailsArray
{
    private String employeeId;
    private String employeeName;
    private String employeeContactNumber;
    private String departmentId;
    private String departmentName;

    public employeeDetailsArray(String employeeId, String employeeName, String employeeContactNumber, String departmentId, String departmentName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeContactNumber = employeeContactNumber;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeContactNumber() {
        return employeeContactNumber;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEmployeeContactNumber(String employeeContactNumber) {
        this.employeeContactNumber = employeeContactNumber;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}


