<p align="center" width="100%">
    <img src="/src/logo/logoM.PNG" width="700">
</p>


------------------------------------------
# Breakdowns
## [1. Background](#rocket-background)
## [2. Folder_Structure](#open_file_folder-folder-structure)
## [3. User_Interface](#busts_in_silhouette-user-interface)
## [4. Installations](#wrench-installations-guidances)


------------------------------------------
# :rocket: Background
<p align="justify"> 
    Supply Chain System is become more and more crutial and important now a days especially for manufacture factory. Growing interest in supply chain systems has emphasised the importance of implementing proper ways to enable effective management of their complexity, immensity and breadth of coverages. Hence, the aim of this project is to develop an Integrated Supply Chain System (Sup Loop System)for battery manufacturing factory, for additional reverse logistic concept will also been used in this project to handle sustainable activities such as (Recycle, Reuse & Returned) in battery manufacturing.
</p>

## Reverse Logistic // Reverse Supply Chain (RSC)?
<p align='justify'>
    The reverse supply chain is the backward movement of goods (such as defective products) from vendors back up the supply chain. It is the opposite of the typical supply chain, where items go from manufacturer to retailer to consumer. 
</p>

[Click here for more information](https://en.wikipedia.org/wiki/Reverse_logistics)

## The Combinations of FSC & RSC
<p align="left" width="100%">
    <img src="https://img.c3controls.com/image/upload/c_fill,dpr_auto,f_auto,fl_lossy,g_west,h_500,q_auto,w_auto/v1/content/c3controls-Forward-Reverse-Logistics" width="700">
</p>

------------------------------------------
#  :open_file_folder: Folder Structure

The workspace contains three main folders, where:
- `.vscode`: .json files (For configuring).
- `mysqlBackup`: Folder contains database backup files.

- `src`: Folder to maintain sources included (base, controller, fxml).
    - `base` : Main function used (arrayFile + Defined Methods).
    - `controller` : Contains all the controller to handle each of used scene page in fxml (listed in fxml Sub-Folder).
    - `fxml` : Contains all the design code for all the scene page.
    - `icon`: Storing the icon used in the system.
    - `JDBC_Connectors`: Contains mysql connections.
    - `logo`: Storing the logo of the system.

   
------------------------------------------
# :busts_in_silhouette: User Interface
- System Manager
- Sales Department
- Production Manager
- Manufacture Department
- Stock Control Deparment
- Recycle Department


------------------------------------------
#  :wrench: Installations Guidances
> This Section will included how to install dockers, setting up MySQL Database in docker, Setting up JavaFX library in VS code and Integrating JDBC with java.
## Docker installations 
- Guidances for install docker -> [here](https://docs.docker.com/desktop/windows/install/).

## Database Setup
- Setting up MySQL Database in Docker -> [here](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/docker-mysql-getting-started.html).

## Database Restorations
- Using bellow command line (in terminal) to restore backed up database. (**cat [file from mysqlBackup]**)


```
# restoring backup files
cat backup_suploop.sql | docker exec -i CONTAINER /usr/bin/mysql -u [mysql_username] --password[mysql_passwords]=root [database_name]
```

## Setting Up JavaFX library & JDBC in VS Code
> Steps by Steps guidances....
1. Download JavaFX Library -> [here](https://gluonhq.com/products/javafx/).

2. Download JDBC Drivers (MySQL) -> [here](https://dev.mysql.com/downloads/connector/j/).

3. Upload References library to your current projects (** .jar files **)
    - Upload downloaded library into your project Refereced Library `/SupLoopSystem/Referenced Libraries/[Upload Here]`
4. Configuring VM Machinces Environment to support **JavaFX** 
    - Adding new configure setting "vmArgs" in `SupLoopSystem/.vscode/launch.json` with beloow json code
    
    ```json
    "vmArgs": "--module-path [JavaFX_Path] --add-modules javafx.controls,javafx.fxml"
    ``` 
        
5. Changing Username & Login Passwords on **JDBC**
    - Replace the **Username** & **Passwords** in `SupLoopSystem/src/JDBC_Connectors/DBConnectors.java` (Base on your mysql_username, mysql_passwords)
    
    ``` java
    public class DBConnectors{  // switch user 
    public Connection getConnection() throws SQLException
    {
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sup_Loop_Database","[mysql_username]","[mysql_passwords]");
        return connection;}
    }
    ```    
6. Save all the changes
