GDS / GDSCC
=====
GDS is a runtime context helping to automate embedded devices testing.

GDSCC is a java Swing serial port console based on gritty project with RMI enabled interface that can be used to communicate with target device.

GDSCC can connect to "Firmata enabled"* Arduino device to support extra functions:
    --  Button Simulation [ digital pin 2~5 (high=press low=release) ]

GDSCC can invoke groovy scripts via Ctrl + F1~F8.

* The Arduino device should be updated with the "StandardFirmata" example.
