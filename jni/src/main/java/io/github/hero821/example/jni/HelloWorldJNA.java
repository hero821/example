package io.github.hero821.example.jni;

import com.sun.jna.Library;
import com.sun.jna.Native;

// java -cp .;E:\Software\.m2\repository\net\java\dev\jna\jna\5.14.0\jna-5.14.0.jar io.github.hero821.example.jni.HelloWorldJNA

public class HelloWorldJNA {
    public interface CLibrary extends Library {
        void Java_io_github_hero821_example_jni_HelloWorldJNI_sayHello();
    }

    public static void main(String[] args) {
        CLibrary library = Native.load("HelloWorldJNI", CLibrary.class);
        library.Java_io_github_hero821_example_jni_HelloWorldJNI_sayHello();
    }
}
