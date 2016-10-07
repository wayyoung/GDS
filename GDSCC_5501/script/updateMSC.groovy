cc.setPrompt("MStar #")
//cc.asyncSendLooping("\r\n","MStar #",30)
//cc.buttonClick(0)
//cc.asyncSendLoopingWait()
hwclock -w


hwclock -w


hwclock


cc.cmd("set serverip 172.17.1.73")
cc.cmd("set autoload 0")
cc.cmd("set ipaddr 172.17.1.86)
//cc.cmd("dhcp")
cc.cmd("saveenv")
cc.cmd("tftp 0x23000000 msc316-flash.bin;sf probe 0;sf erase 0x0 0x800000;sf write 0x23000000 0x0 0x780000")
cc.cmd("saveenv")
