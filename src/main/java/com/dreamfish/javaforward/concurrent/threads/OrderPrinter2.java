package com.dreamfish.javaforward.concurrent.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * synchronized锁+Object的wait和notify实现
 */
public class OrderPrinter2 {

    private int max;
    private AtomicInteger status = new AtomicInteger(1); //相比volatile保证原子性
    Object odd = new Object(); // 奇数对象
    Object even = new Object(); // 偶数对象

    public OrderPrinter2(int max) {
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
                    if (status.get() % 2 == 0) {
                        synchronized (odd) { // 奇数线程进入等待
                            try {
                                odd.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println(name + ": " + status.getAndIncrement());
                        synchronized (even) { // 通知偶数打印线程
                            even.notify();
                        }
                    }
                }
            } else {
                while (status.get() <= max) {
                    if (status.get() % 2 != 0) {
                        synchronized (even) { // 偶数线程进入等待
                            try {
                                even.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println(name + ": " + status.getAndIncrement());
                        synchronized (odd) { // 通知奇数打印线程
                            odd.notify();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        OrderPrinter2 orderPrinter = new OrderPrinter2(100);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(orderPrinter.new Printer("奇数线程", true));
        executorService.submit(orderPrinter.new Printer("偶数线程", false));
        executorService.shutdown();
    }
}
