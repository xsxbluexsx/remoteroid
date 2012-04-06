LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_MODULE := remoteroid
LOCAL_SRC_FILES := InputHandler.cpp

include $(BUILD_SHARED_LIBRARY)