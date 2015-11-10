LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= C:/Users/app61/AppData/Local/Android/OpenCV-3.0.0-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := edu_rit_margikarpets_beepbrake_MainActivity.c
LOCAL_LDLIBS += -llog
LOCAL_MODULE := OpenCV

include $(BUILD_SHARED_LIBRARY)
