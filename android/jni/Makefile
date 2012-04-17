JAVA_CLASSES_DIR = bin/classes
JAVA_NATIVES_DIR = $(JAVA_CLASSES_DIR)/org/secmem/remoteroid/natives
JNI_HEADER_DIR = jni/include
SOURCES = InputHandler.cpp

all: header lib

header : ../$(JAVA_NATIVES_DIR)/InputHandler.class
	cd ../$(JAVA_CLASSES_DIR); \
	javah org.secmem.remoteroid.natives.InputHandler; \
	mv org_secmem_remoteroid_natives_InputHandler.h InputHandler.h; \
	mv InputHandler.h ../../jni/include/InputHandler.h
	
lib : $(SOURCES)
	ndk-build
	
clean :
	rm -rf ../libs/armeabi/libremoteroid.so; \
	rm -rf ../obj/local/armeabi/libremoteroid.so; \
	rm -rf ../obj/local/armeabi/libstdc++.a; \
	rm -rf ../obj/local/armeabi/objs/remoteroid/InputHandler.o; \
	rm -rf ../obj/local/armeabi/objs/remoteroid/InputHandler.o.d