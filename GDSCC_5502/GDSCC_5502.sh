#!/bin/bash

export GDSCC_HOME=$(dirname $0)
cd ${GDSCC_HOME}
GDSCC_HOME=$(pwd)

if [ "${GDS_HOME}" == "" ]; then
    GDS_HOME=${GDSCC_HOME}/..
    cd ${GDS_HOME}
    export GDS_HOME=$(pwd)
    cd ${GDSCC_HOME}
fi
if [ ! -f "${GDS_HOME}/runtime/lib/GDS.jar" ]; then
    cp -f ${GDS_HOME}/runtime/lib/GDS_RUNTIME.jar ${GDS_HOME}/runtime/lib/GDS.jar
fi
export GDSCC_CP=${GDS_HOME}/lib/commons-configuration-1.10.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/commons-logging-1.2.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/commons-io-2.4.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/commons-lang-2.6.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/commons-net-3.4.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/jd2xx.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/JTattoo-1.6.11.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/slf4j-api-1.7.13.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/log4j-slf4j-impl-2.5.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/log4j-api-2.5.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/log4j-core-2.5.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/lib/SerialIO.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/runtime/lib/GDS.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/runtime/lib/groovy-2.4.5.jar
export GDSCC_CP=${GDSCC_CP}:${GDS_HOME}/runtime/lib/groovy-jsr223-2.4.5.jar

echo PWD=$(pwd)
#echo  java -Xmx128M -Dlog4j.configurationFile=log4j2.xml -Dgdscc.configurationFile=GDSCC_5501.windows.properties -classpath ${GDSCC_CP}  gds.GDSCC
java -Xmx128M -DSERIAL_PORT_LIST=/dev/ttyCOM01:/dev/ttyCOM02:/dev/ttyCOM03:/dev/ttyCOM04 -Dlog4j.configurationFile=log4j2.xml -Dgdscc.configurationFile=GDSCC_5502.linux.properties -classpath ${GDSCC_CP}  gds.GDSCC $1 &
