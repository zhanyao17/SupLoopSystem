# Sup Loop System
![supLoopLogo](/src/logo/logoM.PNG)
------------------------------------------
# :mega: README.MD Breakdowns
## [1. Folder_Structure](#openfilefolder-folder-structure)
## [2. User_Interface](#bustsinsilhouette-user-interface)
## [3. Installations](#wrench-installations-guidances)


# :open_file_folder: Folder Structure

The workspace contains three main folders, where:
- `.vscode`: Folder contains .json configurations setting.
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
- Using bellow command line to restore backed up database. (**cat [file from mysqlBackup]**)
```
cat backup_suploop.sql | docker exec -i CONTAINER /usr/bin/mysql -u [mysql_username] --password[mysql_passwords]=root [database_name]
```

## Setting Up JavaFX library in VS Code
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
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sup_Loop_Database","newUser","zhanyao12345");// username & passwords
        return connection;}
    }
    ```
6. Save all the changes
