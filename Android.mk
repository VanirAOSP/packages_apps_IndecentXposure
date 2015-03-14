LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SDK_VERSION := current
LOCAL_PACKAGE_NAME := IndecentXposure
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_OVERRIDES_PACKAGES := IndecentXposure
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 android-support-v7-recyclerview
LOCAL_SRC_FILES := $(call all-java-files-under, src debug/src)
res_dir := res debug/res ../../../prebuilts/sdk/current/support/v7/appcompat/res
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dir))
LOCAL_SDK_VERSION := current
LOCAL_PACKAGE_NAME := IndecentXposureDebug
include $(BUILD_PACKAGE)
