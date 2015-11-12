#include <jni.h>
#include "edu_rit_margikarpets_beepbrake_MainActivity.h"

// 
JNIEXPORT jstring JNICALL Java_edu_rit_margikarpets_beepbrake_MainActivity_changeText
        (JNIEnv *env, jobject object) {
  return (*env)->NewStringUTF(env, "Hello From New York City, Its Saturday Night Live");
}