@echo off
if exist "..\runtime\lib\GDS.jar" (
    FC /B "..\runtime\lib\GDS.jar" "..\runtime\lib\GDS_RUNTIME.jar" > NUL
    echo %ERRORLEVEL%
    IF %ERRORLEVEL% EQU 1 (
        call GDSCC_5501.bat EXIT
        timeout 15
        del "..\runtime\lib\GDS.jar"
        timeout 5
        copy /Y "..\runtime\lib\GDS_RUNTIME.jar" "..\runtime\lib\GDS.jar"
        timeout 5
        call GDSCC_5501.bat
    ) else (
        echo NO NEED TO UPGRADE
    )

) else (
    echo  "NO GDS.jar"
    copy /Y ..\runtime\lib\GDS_RUNTIME.jar ..\runtime\lib\GDS.jar
    timeout 5
    call GDSCC_5501.bat
)

