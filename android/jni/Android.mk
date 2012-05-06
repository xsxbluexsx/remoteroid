LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := remoteroid
LOCAL_SRC_FILES := InputHandler.cpp \
				   FrameHandler.cpp \
				   Input.cpp \
				   suinput.cpp
				   
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)