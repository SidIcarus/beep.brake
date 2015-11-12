LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := OpenCV
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Users\app61\Git\Github\beep.brake\app\src\main\jni\edu_rit_margikarpets_beepbrake_MainActivity.c \

LOCAL_C_INCLUDES += C:\Users\app61\Git\Github\beep.brake\app\src\main\jni
LOCAL_C_INCLUDES += C:\Users\app61\Git\Github\beep.brake\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
