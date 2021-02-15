package io.github.hero821.example.jdk.http;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件下载：FileDownload
 * 单线程：SingleThread
 * 多线程：MultiThread
 */
public class FileDownload {
    @SuppressWarnings("DuplicatedCode")
    static class SingleThread {
        private static final int totalThreadNum = 1;
        private static final ExecutorService executor = Executors.newFixedThreadPool(totalThreadNum + 1);

        public void start(String url) throws Exception {
            Path path = Paths.get(getFileName(url));
            Files.deleteIfExists(path);
            Files.createFile(path);

            System.out.println("开始下载");
            long startTime = System.currentTimeMillis();

            long totalSize = getTotalSize(url);
            List<Future<Boolean>> futures = new ArrayList<>();
            futures.add(executor.submit(new DownloadThread(path, url)));
            futures.add(executor.submit(new LogThread(totalSize, totalThreadNum)));
            for (Future<Boolean> future : futures) {
                future.get();
            }
            executor.shutdown();

            long endTime = System.currentTimeMillis();
            System.out.println("完成下载");
            System.out.printf("耗时%s%n", (endTime - startTime) / 1000);
        }

        private static class DownloadThread implements Callable<Boolean> {
            private final Path path;
            private final String url;

            public DownloadThread(Path path, String url) {
                this.path = path;
                this.url = url;
            }

            @Override
            public Boolean call() throws Exception {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
                    throw new RuntimeException("状态码错误");
                }
                RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
                InputStream inputStream = connection.getInputStream();
                byte[] bytes = new byte[1024 * 1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    file.write(bytes, 0, len);
                    LogThread.addFinishedSize(len);
                }
                inputStream.close();
                file.close();
                connection.disconnect();
                LogThread.addFinishedThreadNum();
                System.out.printf("线程结束%n");
                return true;
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    static class MultiThread {
        private static final int totalThreadNum = 5;
        private static final ExecutorService executor = Executors.newFixedThreadPool(totalThreadNum + 1);

        public void start(String url) throws Exception {
            Path path = Paths.get(getFileName(url));
            Files.deleteIfExists(path);
            Files.createFile(path);

            System.out.println("开始下载");
            long startTime = System.currentTimeMillis();

            long totalSize = getTotalSize(url);
            long rangeSize = (long) Math.ceil((double) totalSize / totalThreadNum);
            List<Future<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < totalThreadNum; i++) {
                futures.add(executor.submit(new DownloadThread(i, path, url, i * rangeSize, Math.min((i + 1) * rangeSize, totalSize) - 1)));
            }
            futures.add(executor.submit(new LogThread(totalSize, totalThreadNum)));
            for (Future<Boolean> future : futures) {
                future.get();
            }
            executor.shutdown();

            long endTime = System.currentTimeMillis();
            System.out.println("完成下载");
            System.out.printf("耗时%s%n", (endTime - startTime) / 1000);
        }

        private static class DownloadThread implements Callable<Boolean> {
            private final int id;
            private final Path path;
            private final String url;
            private final long start;
            private final long end;

            public DownloadThread(int id, Path path, String url, long start, long end) {
                this.id = id;
                this.path = path;
                this.url = url;
                this.start = start;
                this.end = end;
            }

            @Override
            public Boolean call() throws Exception {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
                connection.connect();
                if (HttpURLConnection.HTTP_PARTIAL != connection.getResponseCode()) {
                    throw new RuntimeException("状态码错误");
                }
                RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
                file.seek(start);
                InputStream inputStream = connection.getInputStream();
                byte[] bytes = new byte[1024 * 1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    file.write(bytes, 0, len);
                    LogThread.addFinishedSize(len);
                }
                inputStream.close();
                file.close();
                connection.disconnect();
                LogThread.addFinishedThreadNum();
                System.out.printf("线程%s结束%n", id);
                return true;
            }
        }
    }

    private static class LogThread implements Callable<Boolean> {
        private final long totalSize;
        private static final AtomicLong finishedSize = new AtomicLong();
        private final int totalThreadNum;
        private static final AtomicInteger finishedThreadNum = new AtomicInteger();

        public LogThread(long totalSize, int totalThreadNum) {
            this.totalSize = totalSize;
            this.totalThreadNum = totalThreadNum;
        }

        public static void addFinishedSize(int size) {
            finishedSize.addAndGet(size);
        }

        public static void addFinishedThreadNum() {
            finishedThreadNum.addAndGet(1);
        }

        @Override
        public Boolean call() throws Exception {
            while (totalThreadNum != finishedThreadNum.get()) {
                System.out.printf("已完成：%s/%s（%s%%）%n", finishedSize.get(), totalSize, 100 * finishedSize.get() / totalSize);
                //noinspection BusyWait
                Thread.sleep(1000);
            }
            return true;
        }
    }

    private static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private static long getTotalSize(String url) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        if (!"bytes".equals(connection.getHeaderField("Accept-Ranges"))) {
            throw new RuntimeException("不支持分片");
        }
        long totalSize = connection.getContentLengthLong();
        connection.disconnect();
        return totalSize;
    }

    public static void main(String[] args) throws Exception {
        new SingleThread().start("https://download.jetbrains.com/idea/ideaIU-2020.3.2.win.zip");
        new MultiThread().start("https://download.jetbrains.com/idea/ideaIU-2020.3.2.win.zip");
    }
}
