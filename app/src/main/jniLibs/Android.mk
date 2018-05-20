LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := input-prebuilt
LOCAL_SRC_FILES := libinput-event.so
include $(PREBUILT_SHARED_LIBRARY)
