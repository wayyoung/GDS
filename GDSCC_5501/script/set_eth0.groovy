cc.setPrompt("]##")
cc.clearResponse()
cc.send("kill -9 \$(ps | grep udhcpc | awk '{print \$1}')")
cc.send("\n")
cc.cmd("")
cc.cmd("ifconfig eth0 10.10.10.11")
cc.cmd("mount -t cifs //10.10.10.3/IPCamera/vendor /system/vendor -o username=Administrator,password=0okmnji9,sec=ntlm,iocharset=utf8")