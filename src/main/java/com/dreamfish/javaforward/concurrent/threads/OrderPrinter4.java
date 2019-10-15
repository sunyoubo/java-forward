package com.dreamfish.javaforward.concurrent.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通过全局标识实现
 */
public class OrderPrinter4 {

    private int max;
    private AtomicInteger status = new AtomicInteger(1);
    private AtomicBoolean isOdd = new AtomicBoolean(true);

    public OrderPrinter4(int max) {
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
                while (status.get() < max) {
                    if (isOdd.get()) { // 打印奇数
                        System.out.println(name + ": " + status.getAndIncrement());
                        isOdd.set(false);
                    }
                }
            } else {
                while (status.get() < max) {
                    if (!isOdd.get()) { // 打印偶数
                        System.out.println(name + ": " + status.getAndIncrement());
                        isOdd.set(true);
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
