package ru.ifmo.rain.usov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private List<Thread> arbeiters;
    private final Queue<Runnable> toDo;


    public ParallelMapperImpl(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("Negative number of arbeiters");
        }
        arbeiters = new ArrayList<>();
        toDo = new ArrayDeque<>();
        for (int i = 0; i < threads; i++) {
            arbeiters.add(new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Runnable job;
                        synchronized (toDo) {
                            while (toDo.isEmpty()) {
                                toDo.wait();
                            }
                            job = toDo.poll();
                            toDo.notifyAll();
                        }
                        job.run();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    Thread.currentThread().interrupt();
                }
            }));
            arbeiters.get(i).start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        Result<R> result = new Result<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            final int index = i;
            synchronized (toDo) {
                toDo.add(() -> result.add(index, f.apply(args.get(index))));
                toDo.notifyAll();
            }
        }
        return result.syncProcessed();
    }

    @Override
    public void close() {
        arbeiters.forEach(Thread::interrupt);
        for (Thread worker : arbeiters) {
            try {
                worker.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class Result<R> {
        private List<R> processed;
        private int sync;

        Result(int argsSize) {
            processed = new ArrayList<>(Collections.nCopies(argsSize, null));
            sync = 0;
        }

        synchronized void add(final int index, R value) {
            processed.set(index, value);
            sync++;
            notifyAll();
        }

        synchronized List<R> syncProcessed() throws InterruptedException {
            while (sync < processed.size()) {
                wait();
            }
            return processed;
        }
    }
}
