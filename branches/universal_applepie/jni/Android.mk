LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := remoteroid
LOCAL_SRC_FILES := InputHandler.cpp \
				   Input.cpp \
				   suinput.cpp
				   
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

# Temporarly commenting out until cpp version of libfbuffer issue has fixed
# 
# LOCAL_MODULE := fbuffer
# LOCAL_SRC_FILES :=  FrameHandler.cpp \
# 					DisplayInfo.cpp 
					
# LOCAL_LDLIBS := -llog		
# LOCAL_CFLAGS := -DCONFIG_EMBEDDED\ -DUSE_IND_THREAD\

# include $(BUILD_SHARED_LIBRARY)

# include $(CLEAR_VARS)

# LOCAL_MODULE := fbufferu
# LOCAL_SRC_FILES := FrameHandlerU.cpp \
				   DisplayInfo.cpp

# LOCAL_LDLIBS := -llog
# LOCAL_CFLAGS := -DCONFIG_EMBEDDED\ -DUSE_IND_THREAD\
				
# include $(BUILD_SHARED_LIBRARY)