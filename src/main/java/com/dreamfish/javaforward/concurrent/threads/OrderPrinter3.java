package com.dreamfish.javaforward.concurrent.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock+Condition实现
 */
public class OrderPrinter3 {

    private int max;
    private AtomicInteger status = new AtomicInteger(1);
    private ReentrantLock lock = new ReentrantLock();
    private Condition odd = lock.newCondition();
    private Condition even = lock.newCondition();

    public OrderPrinter3(int max) {
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
                    lock.lock();
                    try {
                        if (status.get() % 2 == 0) {
                            odd.await();
                        }
                        if (status.get() < max) { //打印奇数
                            System.out.println(name + ": " + status.getAndIncrement());
                        }
                        even.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            } else {
                while (status.get() < max) {
                    lock.lock();
                    try {
                        if (status.get() % 2 != 0) {
                            even.await();
                        }
                        if (status.get() < max) { //打印偶数
                            System.out.println(name + ": " + status.getAndIncrement());
                        }
                        odd.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
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
