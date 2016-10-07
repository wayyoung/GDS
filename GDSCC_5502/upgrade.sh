#!/bin/bash

if [ -f "../runtime/lib/GDS.jar" ]; then

    DIFF=$(diff ../runtime/lib/GDS.jar ../runtime/lib/GDS_RUNTIME.jar)
    if [ "$DIFF" != "" ]; then
        ./GDSCC_5501.sh EXIT
        echo "WAIT 15 sec"
        sleep 15
        rm -f "../runtime/lib/GDS.jar"
        sleep 3
        cp -f ../runtime/lib/GDS_RUNTIME.jar ../runtime/lib/GDS.jar
        sleep 3
        ./GDSCC_5501.sh
    else
    	echo "NO NEED TO UPGRADE"
    fi
else
    echo  "NO GDS.jar"
    cp -f ../runtime/lib/GDS_RUNTIME.jar ../runtime/lib/GDS.jar
    sleep 5
    ./GDSCC_5501.sh
fi


