SET GDSCC_HOME=%~dp0
cd /d %GDSCC_HOME%

set GDS_HOME=%GDSCC_HOME%..


if exist "%GDS_HOME%\lib\GDS.jar" (
    FC /B "%GDS_HOME%\lib\GDS.jar" "%GDS_HOME%\lib\GDS_RUNTIME.jar" > NUL
    if ERRORLEVEL 1 (
        copy /Y "%GDS_HOME%\lib\GDS_RUNTIME.jar" "%GDS_HOME%\lib\GDS.jar"

    ) else (
        echo NO NEED TO UPGRADE
    )

) else (
    echo  "NO GDS.jar"
    copy /Y %GDS_HOME%\lib\GDS_RUNTIME.jar %GDS_HOME%\lib\GDS.jar
)

set GDSCC_CP=
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\commons-io-2.5.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\commons-net-3.5.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\JTattoo-1.6.11.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\slf4j-api-1.7.13.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\groovy-all-2.4.7.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\jssc-2.8.0.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\GDS.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\log4j\log4j-slf4j-impl-2.5.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\log4j\log4j-api-2.5.jar
set GDSCC_CP=%GDSCC_CP%;%GDS_HOME%\lib\log4j\log4j-core-2.5.jar

start javaw -Xverify:none -Xmx128M -Dsun.java2d.d3d=false -Djava.library.path=%GDS_HOME%\lib -Dlog4j.configurationFile=log4j2.xml -Dgdscc.configurationFile=GDSCC_5502.windows.properties -classpath %GDSCC_CP%  gds.GDSCC %1