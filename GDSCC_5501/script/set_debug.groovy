cc.setPrompt("]##")
cc.cmd("export VALGRIND_LIB=/vendor/valgrind/lib/valgrind")

cc.cmd("export LD_LIBRARY_PATH=/vendor/debug/system/lib:\$LD_LIBRARY_PATH")
cc.cmd("export OMX_LIB_PATH=/vendor/debug/system/lib")

