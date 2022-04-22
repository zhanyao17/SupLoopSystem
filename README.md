## Installations
1. Database installations.
    - `mysqlBackup` will be used to restore Database_suploop into your dockers 

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources included (base, controller, fxml)
    - `base` : Main function used (arrayFile + Defined Methods)
    - `controller` : Contains all the controller to handle each of used scene page in fxml (listed in fxml Sub-Folder)
    - `fxml` : Contains all the design code for all the scene page

- `logo`: the folder to store jpeg/png file for table picture (fxml)

Meanwhile, the compiled output files will be generated in the `bin` folder by default : Contains all class files.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## User Interface
- System Manager
- Sales Department
- Manufacture Department
- Stock Control Deparment
- Recycle Department


## First Time Set-up
### Docker installations 
**Guidances for install docker** -> [here](https://docs.docker.com/desktop/windows/install/).

### Database Setup
**Setting up MySQL Database in Docker** -> [here](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/docker-mysql-getting-started.html).

### Database Restorations
**Restore Database** -> Repalce backupFiles > `backup_suploop.sql`
```
cat [backupFiles] | docker exec -i CONTAINER /usr/bin/mysql -u [mysql_username] --password[mysql_passwords]=root [database_name]
```

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## To-do
- `changes_database` : Remind // Rechecking Database content (avoid crashing !!)
- `testing_bugs` : Test bugs