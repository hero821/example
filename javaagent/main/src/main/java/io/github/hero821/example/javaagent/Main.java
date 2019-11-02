package io.github.hero821.example.javaagent;

public class Main {
    public static void main(String[] args) {
        new Main().sleep(3);
    }

    public void sleep(int seconds) {
        System.out.println("Main.sleep 开始");
        try {
            System.out.println("Main.sleep 延迟" + seconds + "秒");
            Thread.sleep(1000L * seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Main.sleep 结束");
    }
}
