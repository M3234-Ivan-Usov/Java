package ru.ifmo.rain.usov.concurrent;
//java -cp . -p . -m info.kgeorgiy.java.advanced.concurrent list ru.ifmo.rain.usov.parallel.IterativeParallelism
//java -cp . -p . -m info.kgeorgiy.java.advanced.mapper list ru.ifmo.rain.usov.parallel.IterativeParallelism

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {
    private ParallelMapper PM;
    public IterativeParallelism() {
        PM = null;
    }
    public IterativeParallelism(ParallelMapper PM) {
        this.PM = PM;
    }

    private <T> List<Stream<? extends T>> getDistribution(int quantity, List<? extends T> data) {
        int full = data.size() / quantity;
        int remain = data.size() % quantity;
        List<Stream<? extends T>> forOne = new ArrayList<>();
        int distributed = 0;
        for (int k = 0; k < quantity; k++) {
            int forCurrent = full + (k < remain ? 1 : 0);
            if (forCurrent != 0) {
                forOne.add(data.subList(distributed, distributed + forCurrent).stream());
            }
            distributed += forCurrent;
        }
        return forOne;
    }

    private <T, R> R action(int quantity, List<T> data, Function<Stream<? extends T>, ? extends R> process,
                            Function<Stream<? extends R>, ? extends R> result) throws InterruptedException {
        List<Stream<? extends T>> forOne = getDistribution(quantity, data);
        List<R> processed;
        if (PM != null) {
            processed = PM.map(process, forOne);
        }
        else {
            List<Thread> Gastarbeiter = new ArrayList<>(Collections.emptyList());
            processed = new ArrayList<>(Collections.nCopies(forOne.size(), null));
            for (int i = 0; i < forOne.size(); i++) {
                final int cur = i;
                Thread thread = new Thread(() -> processed.set(cur, process.apply(forOne.get(cur))));
                Gastarbeiter.add(thread);
                thread.start();
            }
            tryJoin(Gastarbeiter);
        }
        return result.apply(processed.stream());
    }

    void tryJoin(List<Thread> Gastarbeiter) throws InterruptedException {
        for (Thread cur : Gastarbeiter) {
            try {
                cur.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return action(threads, values, x -> x.max(comparator).get(), x -> x.max(comparator).get());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return action(threads, values, x -> x.min(comparator).get(), x -> x.min(comparator).get());
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return action(threads, values, x -> x.allMatch(predicate), x -> x.allMatch(Boolean::booleanValue));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return action(threads, values, x -> x.anyMatch(predicate), x -> x.anyMatch(Boolean::booleanValue));
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return action(threads, values, x -> x.map(Object::toString).collect(Collectors.joining()),
                x -> x.collect(Collectors.joining()));
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return action(threads, values, x -> x.filter(predicate).collect(Collectors.toList()),
                x -> x.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return action(threads, values, x -> x.map(f).collect(Collectors.toList()),
                x -> x.flatMap(List::stream).collect(Collectors.toList()));
    }
}
