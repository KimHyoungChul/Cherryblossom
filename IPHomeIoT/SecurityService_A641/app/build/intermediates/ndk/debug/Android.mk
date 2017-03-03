LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := secsvc
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\Android.mk \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\global.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\main.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\SecurityService.dsp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\SecurityService.dsw \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\SecurityService.ncb \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\SecurityService.opt \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\cv_app\CvsService.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\dbus\DBusLocalManager.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\dbus\DBusRemoteManager.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\dbus\DBusSignalFunc.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\dx_app\SoapDbusSvcManager.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\env_c\envC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\env_c\envC.o \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\env_c\soap.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cdx.wsdl \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManager.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManagerC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManagerClient.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManagerClientLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManagerServer.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cdx_c\cmxDbusToXManagerServerLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmm.wsdl \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManager.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManagerC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManagerClient.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManagerClientLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManagerServer.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cmm_c\cmxMediaManagerServerLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcess.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcessC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcessClient.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcessClientLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcessServer.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cmxNewProcessServerLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cnp_c\cnp.wsdl \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsService.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsServiceC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsServiceClient.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsServiceClientLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsServiceServer.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\cmxCSsServiceServerLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_css_c\css.wsdl \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsService.nsmap \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsServiceC.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsServiceClient.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsServiceClientLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsServiceServer.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cmxCvsServiceServerLib.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_cvs_c\cvs.wsdl \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_lib\dom.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_lib\stdsoap2_ssl.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\header\soap_lib\threads.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\include\dbus\mm.xml \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\method\EventStaticIntMethod.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\mm_app\MediaService.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\mm_app\MediaServiceFunc.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\np_app\CnpService.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\np_app\CnpServiceFunc.c \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\object\ConfigObj.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\object\SktManager.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\object\SysManager.cpp \
	E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni\ss_app\SoapCSsSvcManager.c \

LOCAL_C_INCLUDES += E:\preDriveE\TestPrograms\SecurityService_A641\app\src\main\jni
LOCAL_C_INCLUDES += E:\preDriveE\TestPrograms\SecurityService_A641\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
