package dev.thatsmybaby.factory;

import java.util.concurrent.*;

public final class Test {

    public static void main(String[] args) {
        System.out.println("Test");

        ScheduledThreadPoolExecutor service = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        System.out.println("A");

        for (int i = 0; i < service.getPoolSize(); i++) {
            System.out.println("Repeating poool");
            service.scheduleAtFixedRate(() -> {
                System.out.println("Thread " + Thread.currentThread().getName());
                System.out.println("Thread Pool size " + service.getPoolSize());
                System.out.println("Thread Pool Active " + service.getActiveCount());
            }, 2, 5, TimeUnit.SECONDS);
        }
    }
}