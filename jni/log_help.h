//
// Created by liaoyao on 11/2/17.
//

#ifndef TEST_LOG_HELP_H
#define TEST_LOG_HELP_H

#include <android/log.h>


#define debug_on 1

#ifdef debug_on

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // ����LOGD����
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // ����LOGI����
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // ����LOGW����
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // ����LOGE����
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // ����LOGF����

#endif


#endif //TEST_LOG_HELP_H
