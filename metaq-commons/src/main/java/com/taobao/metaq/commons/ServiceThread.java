/**
 * $Id: ServiceThread.java 2 2013-01-05 08:09:27Z shijia $
 */
package com.taobao.metaq.commons;

/**
 * ��̨�����̻߳���
 */
public abstract class ServiceThread implements Runnable {
    // ִ���߳�
    protected final Thread thread;
    // �̻߳���ʱ�䣬Ĭ��60S
    private static final long JoinTime = 60 * 1000;
    // �Ƿ��Ѿ���Notify��
    protected volatile boolean hasNotified = false;
    // �߳��Ƿ��Ѿ�ֹͣ
    protected volatile boolean stoped = false;


    public ServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }


    public abstract String getServiceName();


    public void start() {
        this.thread.start();
    }


    public void shutdown() {
        this.stoped = true;
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }

        try {
            this.thread.join(this.getJointime());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void wakeup() {
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }
    }


    protected void waitForRunning(long interval) {
        synchronized (this) {
            if (this.hasNotified) {
                this.hasNotified = false;
                this.onWaitEnd();
                return;
            }

            try {
                this.wait(interval);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                this.hasNotified = false;
                this.onWaitEnd();
            }
        }
    }


    protected void onWaitEnd() {
    }


    public boolean isStoped() {
        return stoped;
    }


    public long getJointime() {
        return JoinTime;
    }
}
