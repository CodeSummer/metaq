package com.taobao.metamorphosis.tools.monitor.core;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author �޻�
 * @since 2011-5-27 ����03:21:25
 */

abstract public class AbstractProber implements Prober {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private volatile AtomicBoolean isProbeStarted = new AtomicBoolean(false);

    protected final CoreManager coreManager;


    public AbstractProber(CoreManager coreManager) {
        this.coreManager = coreManager;
    }


    public void prob() throws InterruptedException {
        // ���ⱻ����ö��
        if (this.isProbeStarted.get() == false) {
            this.doProb();
            this.isProbeStarted.set(true);
        }
        else {
            this.logger.info("�Ѿ�������,��������");
        }
    }


    public void stopProb() {
        if (this.isProbeStarted.compareAndSet(true, false)) {
            this.doStopProb();
            this.logger.info("ֹͣ̽��.");
        }
        else {
            this.logger.info("û������,����ֹͣ");
        }
    }


    protected static void cancelFutures(List<ScheduledFuture<?>> futures) {
        if (futures == null) {
            return;
        }
        for (ScheduledFuture<?> future : futures) {
            if (future != null) {
                future.cancel(true);
            }
        }
    }


    protected abstract void doStopProb();


    protected abstract void doProb() throws InterruptedException;


    public MsgSender[] getSenders() {
        return this.coreManager.getSenders();
    }


    public MsgReceiver[] getReveicers() {
        return this.coreManager.getReveicers();
    }


    public MonitorConfig getMonitorConfig() {
        return this.coreManager.getMonitorConfig();
    }


    public ScheduledExecutorService getProberExecutor() {
        return this.coreManager.getProberExecutor();
    }


    public Log getLogger() {
        return this.logger;
    }
}
