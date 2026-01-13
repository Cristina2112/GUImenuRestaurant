@echo off
REM Script to compile and run RestaurantGUI with JavaFX

set JAVAFX_SDK=C:\javafx-sdk-23.0.1
set MODULE_PATH=--module-path %JAVAFX_SDK%\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics

REM First, check if JavaFX SDK exists, if not download it
if not exist %JAVAFX_SDK% (
    echo Downloading JavaFX SDK...
    powershell -Command "Invoke-WebRequest -Uri 'https://gluonhq.com/download/javafx/javafx-sdk-23.0.1-windows.zip' -OutFile 'javafx-sdk.zip'; Expand-Archive -Path 'javafx-sdk.zip' -DestinationPath 'C:\'; Remove-Item 'javafx-sdk.zip'"
)

REM Compile the project
echo Compiling project...
javac %MODULE_PATH% -d target\classes -sourcepath src\main\java src\main\java\org\example\*.java

REM Run the application
echo Running GUI...
java %MODULE_PATH% -cp target\classes org.example.view.RestaurantGUI

pause

