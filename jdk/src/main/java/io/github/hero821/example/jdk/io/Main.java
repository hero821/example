package io.github.hero821.example.jdk.io;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Main {
    private String file = "/opt/test.tar";
    private long startTime, endTime;

    public Main() throws Exception {
        copy_FileInputStream_FileOutputStream();
        copy_BufferedInputStream_BufferedOutputStream();
        copy_FileReader_FileWriter();
        copy_BufferedReader_BufferedWriter();
        copy_FileChannel();
        copy_FileChannel_ByteBuffer();
        copy_CommonsIO();
        copy_NIO_Files();
    }

    private void copy_FileInputStream_FileOutputStream() throws Exception {
        startTime = System.currentTimeMillis();
        InputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = new FileOutputStream(file + "-copy");
        byte[] bytes = new byte[1024];
        while (inputStream.read(bytes) != -1) {
            outputStream.write(bytes);
        }
        inputStream.close();
        outputStream.close();
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_BufferedInputStream_BufferedOutputStream() throws Exception {
        startTime = System.currentTimeMillis();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file + "-copy"));
        byte[] bytes = new byte[1024];
        while (inputStream.read(bytes) != -1) {
            outputStream.write(bytes);
        }
        inputStream.close();
        outputStream.close();
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_FileReader_FileWriter() throws Exception {
        // 只能读写字符流
        FileReader reader = new FileReader(file);
        FileWriter writer = new FileWriter(file + "-copy");
        char[] chars = new char[1024];
        while (reader.read(chars) != -1) {
            writer.write(chars);
        }
        reader.close();
        writer.close();
    }

    private void copy_BufferedReader_BufferedWriter() throws Exception {
        // 只能读写字符流
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file + "-copy"));
        char[] chars = new char[1024];
        while (reader.read(chars) != -1) {
            writer.write(chars);
        }
        reader.close();
        writer.close();
    }

    private void copy_FileChannel() throws Exception {
        startTime = System.currentTimeMillis();
        FileChannel srcChannel = new FileInputStream(file).getChannel();
        FileChannel desChannel = new FileOutputStream(file + "-copy").getChannel();
        srcChannel.transferTo(0, srcChannel.size(), desChannel);
        srcChannel.close();
        desChannel.close();
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_FileChannel_ByteBuffer() throws Exception {
        startTime = System.currentTimeMillis();
        FileChannel srcChannel = new FileInputStream(file).getChannel();
        FileChannel desChannel = new FileOutputStream(file + "-copy").getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        while (srcChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            desChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        srcChannel.close();
        desChannel.close();
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_CommonsIO() throws Exception {
        startTime = System.currentTimeMillis();
        File srcFile = new File(file);
        File desFile = new File(file + "-copy");
        FileUtils.copyFile(srcFile, desFile);
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    private void copy_NIO_Files() throws Exception {
        startTime = System.currentTimeMillis();
        File srcFile = new File(file);
        File desFile = new File(file + "-copy");
        Files.copy(srcFile.toPath(), desFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        endTime = System.currentTimeMillis();
        System.out.println("耗时:" + (endTime - startTime) + "ms");
    }

    public static void main(String[] args) throws Exception {
        new Main();
    }
}
