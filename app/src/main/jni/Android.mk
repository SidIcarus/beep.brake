LOCAL_PATH  := $(call my-dir)
JNI_ROOT    := $(LOCAL_PATH)/../
MAIN_ROOT   := $(JNI_ROOT)/../
SRC_ROOT    := $(MAIN_ROOT)/../
APP_ROOT    := $(SRC_ROOT)/../
PROJECT_ROOT:= $(APP_ROOT)/../

include $(CLEAR_VARS)

#opencv
OPENCV_ROOT:= $(APP_ROOT)/opencv_java3
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCV_ROOT}/native/jni/OpenCV.mk

LOCAL_LDLIBS += -llog
LOCAL_MODULE := beepbrake
LOCAL_SRC_FILES := edu_rit_se_beepbrake_MainActivity.c
#LOCAL_SRC_FILES += new_src_file.c #add more source files like this

include $(BUILD_SHARED_LIBRARY)
