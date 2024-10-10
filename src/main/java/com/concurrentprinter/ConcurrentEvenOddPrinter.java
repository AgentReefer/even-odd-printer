package com.concurrentprinter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentEvenOddPrinter {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition evenTurn = lock.newCondition();
    private final Condition oddTurn = lock.newCondition();
    private boolean isOddTurn = true;
    private final int maxNumber;

    public ConcurrentEvenOddPrinter(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public void printEven() {
        for (int i = 2; i <= maxNumber; i += 2) {
            lock.lock();
            try {
                while (isOddTurn) {
                    evenTurn.await();
                }
                System.out.println(Thread.currentThread().getName() + ": " + i);
                isOddTurn = true;
                oddTurn.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printOdd() {
        for (int i = 1; i <= maxNumber; i += 2) {
            lock.lock();
            try {
                while (!isOddTurn) {
                    oddTurn.await();
                }
                System.out.println(Thread.currentThread().getName() + ": " + i);
                isOddTurn = false;
                evenTurn.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}