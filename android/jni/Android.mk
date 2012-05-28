LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := remoteroid
LOCAL_SRC_FILES := InputHandler.cpp \
				   Input.cpp \
				   suinput.cpp
				   
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := fbuffer
LOCAL_SRC_FILES := fbuffer.c
LOCAL_CFLAGS := -DCONFIG_EMBEDDED\ -DUSE_IND_THREAD\

include $(BUILD_SHARED_LIBRARY)