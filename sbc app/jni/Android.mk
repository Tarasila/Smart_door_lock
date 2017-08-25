LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := code_c
LOCAL_SRC_FILES := code.c
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)