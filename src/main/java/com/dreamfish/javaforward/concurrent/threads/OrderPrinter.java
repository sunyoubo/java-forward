package com.dreamfish.javaforward.concurrent.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * synchronized锁+AtomicInteger
 */
public class OrderPrinter {

    private int max;
    private AtomicInteger status = new AtomicInteger(1); //相比volatile保证原子性

    public OrderPrinter(int max) {
        this.max = max;
    }

    class Printer implements Runnable {

        private String name; // 线程名
        private Boolean oddNumberThread; // 是否为奇数线程

        public Printer(String name, Boolean oddNumberThread) {
            this.name = name;
            this.oddNumberThread = oddNumberThread;
        }

        @Override
        public void run() {
            if (oddNumberThread) {
                while (status.get() <= max) {
                    synchronized (OrderPrinter.class) {
                        if (status.get() <= max && status.get() % 2 !=0) {
                            System.out.println(name + ": " + status.getAndIncrement());
                        }
                    }
                }
            } else {
                while (status.get() <= max) {
                    synchronized (OrderPrinter.class) {
                        if (status.get() <= max && status.get() % 2 ==0) {
                            System.out.println(name + ": " + status.getAndIncrement());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        OrderPrinter orderPrinter = new OrderPrinter(100);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(orderPrinter.new Printer("奇数线程", true));
        executorService.submit(orderPrinter.new Printer("偶数线程", false));
        executorService.shutdown();
    }
}
