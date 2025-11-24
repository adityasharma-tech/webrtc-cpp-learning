#include <jni.h>

#define WEBRTC_POSIX 1
#define WEBRTC_ANDROID 1
#define WEBRTC_ARCH_ARM64 1

#include "webrtc/include/rtc_base/checks.h"
#include "webrtc/include/rtc_base/ssl_adapter.h"

extern "C"
JNIEXPORT void JNICALL
Java_tech_adityasharma_prodwebrtc_ProductionService_nativeInit(JNIEnv *env, jobject thiz) {

}