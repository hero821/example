package io.github.hero821.example.jdk.process;

public class Main {
    private String file = "/opt/test.tar";
    private long startTime, endTime;
    //    private String[] commands = new String[]{"cmd.exe", "/c", "copy", file, file};
    private String[] commands = new String[]{"cp", file, file};

    public Main() throws Exception {
        copy_exec();
        copy_exec_waitFor();
    }

    private void copy_exec() throws Exception {
        commands[commands.length - 1] = file + "-copy";
        startTime = System.currentTimeMillis();
        Runtime.getRuntime().exec(commands);
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_exec_waitFor() throws Exception {
        commands[commands.length - 1] = file + "-copy";
        startTime = System.currentTimeMillis();
        Runtime.getRuntime().exec(commands).waitFor();
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}
