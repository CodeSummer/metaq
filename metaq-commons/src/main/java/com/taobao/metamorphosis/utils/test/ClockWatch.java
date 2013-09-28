package com.taobao.metamorphosis.utils.test;

/**
 * 
 * ���ڲ��Ե�ʱ�������
 * 
 * @author boyan
 * 
 * @since 1.0, 2010-1-11 ����03:09:20
 */

public final class ClockWatch implements Runnable {
    private long startTime;
    private long stopTime;


    @Override
    public synchronized void run() {
        if (this.startTime == -1) {
            this.startTime = System.nanoTime();
        }
        else {
            this.stopTime = System.nanoTime();
        }

    }


    public synchronized void start() {
        this.startTime = -1;
    }


    public synchronized long getDurationInNano() {
        return this.stopTime - this.startTime;
    }


    public synchronized long getDurationInMillis() {
        return (this.stopTime - this.startTime) / 1000000;
    }
}
