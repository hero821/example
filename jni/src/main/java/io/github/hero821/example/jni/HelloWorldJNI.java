package io.github.hero821.example.jni;

// g++ -c -I %JAVA_HOME%\include -I %JAVA_HOME%\include\win32 io_github_hero821_example_jni_HelloWorldJNI.cpp -o io_github_hero821_example_jni_HelloWorldJNI.o
// g++ -shared -o HelloWorldJNI.dll io_github_hero821_example_jni_HelloWorldJNI.o -Wl,--add-stdcall-alias
// 复制 HelloWorldJNI.dll 到 classes
// java -cp . io.github.hero821.example.jni.HelloWorldJNI

public class HelloWorldJNI {
    static {
        System.loadLibrary("HelloWorldJNI");
    }

    private native void sayHello();

    public static void main(String[] args) {
        new HelloWorldJNI().sayHello();
    }
}
