package ru.ifmo.rain.usov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;
//java -cp . -p . -m info.kgeorgiy.java.advanced.crawler hard ru.ifmo.rain.usov.crawler.WebCrawler
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private final ConcurrentMap<String, WebPage> Processed;
    private final int perHost;
    private final static int DEFAULT = 1;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
        Processed = new ConcurrentHashMap<>();
    }

    private final Set<String> SuccessfulDownload = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<String, IOException> FailedDownload = new ConcurrentHashMap<>();
    private final Set<String> Links = ConcurrentHashMap.newKeySet();
    private final Phaser phaser = new Phaser(1);
    private final ConcurrentLinkedQueue<String> LinksInProcess = new ConcurrentLinkedQueue<>();

    private class WebPage {
        private final Queue<Runnable> waitingTasks;
        private int isWorking;

        WebPage() {
            waitingTasks = new ArrayDeque<>();
            isWorking = 0;
        }

        synchronized private void launch() {
            if (isWorking < perHost) {
                Runnable Task = waitingTasks.poll();
                if (Task != null) {
                    isWorking++;
                    downloaders.submit(() -> {
                        Task.run();
                        isWorking--;
                        launch();
                    });
                }
            }
        }

        synchronized void addTask(Runnable task) {
            waitingTasks.add(task);
            launch();
        }
    }

    private static int argument(int index, String[] cmd) {
        return index >= cmd.length ? DEFAULT : Integer.parseInt(cmd[index]);
    }

    private void getLinks(final Document doc, final Phaser phaser) {
        phaser.register();
        extractors.submit(() -> {
            try {
                List<String> links = doc.extractLinks();
                LinksInProcess.addAll(links);
            } catch (IOException ignored) {
            } finally {
                phaser.arrive();
            }
        });
    }

    void mainProcess(final String link, final int depth, final Phaser phaser) {
        String host;
        try {
            host = URLUtils.getHost(link);
        } catch (MalformedURLException e) {
            FailedDownload.put(link, e);
            return;
        }

        WebPage Loader = Processed.computeIfAbsent(host, lambda -> new WebPage());
        phaser.register();
        Loader.addTask(() -> {
            try {
                Document doc = downloader.download(link);
                SuccessfulDownload.add(link);
                if (depth > 1) {
                    getLinks(doc, phaser);
                }
            } catch (IOException e) {
                FailedDownload.put(link, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    public static void main(String[] args) {
        if ((args != null) & (args.length > 0) & (args.length < 6)) {
            try {
                String URL = args[0];
                int depth = argument(1, args);
                int downloaders = argument(2, args);
                int extractors = argument(3, args);
                int perHost = argument(4, args);
                try (Crawler crawler = new WebCrawler(new CachingDownloader(),
                        downloaders, extractors, perHost)) {
                    crawler.download(URL, depth);
                } catch (IOException e) {
                    System.out.println("Crawler failed");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Failure in CMD");
            }
        } else {
            System.out.println("Incorrect number of args");
        }
    }

    @Override
    public Result download(String url, int depth) {
        LinksInProcess.add(url);
        phaser.register();
        for (int counter = 0; counter < depth; counter++) {
            final int currentDepth = depth - counter;
            final Phaser finalphaser = new Phaser(1);
            LinksInProcess.stream().filter(Links::add).forEach
                    (href -> mainProcess(href, currentDepth, finalphaser));
            finalphaser.arriveAndAwaitAdvance();
        }
        phaser.arrive();
        phaser.arriveAndAwaitAdvance();
        return new Result(new ArrayList<>(SuccessfulDownload), FailedDownload);
    }

    @Override
    public void close() {
        extractors.shutdown();
        downloaders.shutdown();
        try {
            extractors.awaitTermination(5, TimeUnit.SECONDS);
            downloaders.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Interuppted: %s" + e.getMessage());
        }
    }
}