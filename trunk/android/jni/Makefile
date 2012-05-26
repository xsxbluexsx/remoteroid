JAVA_CLASSES_DIR = bin/classes
JAVA_NATIVES_DIR = $(JAVA_CLASSES_DIR)/org/secmem/remoteroid/natives
JNI_HEADER_DIR = jni/include
SOURCES = InputHandler.cpp FrameHandler.cpp

all: header lib

header : ../$(JAVA_NATIVES_DIR)/InputHandler.class ../$(JAVA_NATIVES_DIR)/FrameHandler.class
	cd ../$(JAVA_CLASSES_DIR); \
	javah org.secmem.remoteroid.natives.InputHandler; \
	javah org.secmem.remoteroid.natives.FrameHandler; \
	mv org_secmem_remoteroid_natives_InputHandler.h InputHandler.h; \
	mv org_secmem_remoteroid_natives_FrameHandler.h FrameHandler.h; \
	mv InputHandler.h ../../jni/include/InputHandler.h; \
	mv FrameHandler.h ../../jni/include/FrameHandler.h
	
lib : $(SOURCES)
	ndk-build APP_ABI="armeabi x86"
	
clean :
	rm -rf ../libs/armeabi/libremoteroid.so; \
	rm -rf ../obj/local/armeabi/libremoteroid.so; \
	rm -rf ../obj/local/armeabi/libstdc++.a; \
	rm -rf ../obj/local/armeabi/objs/remoteroid/InputHandler.o; \
	rm -rf ../obj/local/armeabi/objs/remoteroid/InputHandler.o.d