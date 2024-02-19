#include <jni.h>
#include <iostream>
#include "io_github_hero821_example_jni_HelloWorldJNI.h"

JNIEXPORT void JNICALL Java_io_github_hero821_example_jni_HelloWorldJNI_sayHello
  (JNIEnv* env, jobject thisObject) {
    std::cout << "Hello World from C++" << std::endl;
}
