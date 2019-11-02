package io.github.hero821.example.javaagent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
    private Agent() {
        throw new UnsupportedOperationException();
    }

    /**
     * 以VM options的方式载入，在Java程序main方法之前执行
     * java -javaagent:agent.jar -jar main.jar
     */
    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                System.out.println("Agent.premain load class:" + className);
                try {
                    className = className.replace("/", ".");
                    if (className.equals("io.github.hero821.example.javaagent.Main")) {
                        CtClass cc = ClassPool.getDefault().get(className);
                        CtMethod cm = cc.getDeclaredMethod("sleep");
                        cm.insertBefore("$1 = 1;");
                        classfileBuffer = cc.toBytecode();
                        cc.debugWriteFile("../tmp");
                        cc.detach();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return classfileBuffer;
            }
        }, true);
    }

    /**
     * 以Attach的方式载入，在Java程序启动之后执行
     * VirtualMachine jvm = VirtualMachine.attach(jvmPid);
     * jvm.loadAgent(agentFile.getAbsolutePath());
     * jvm.detach();
     */
    public static void agentmain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                System.out.println("Agent.agentmain load class:" + className);
                return classfileBuffer;
            }
        }, true);
    }
}
