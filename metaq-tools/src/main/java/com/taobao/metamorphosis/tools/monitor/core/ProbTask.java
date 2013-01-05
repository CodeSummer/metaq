package com.taobao.metamorphosis.tools.monitor.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ̽���߳�ר��task,ȷ�������׳��쳣,����scheduleֹͣ����̽��
 * 
 * @author �޻�
 * @since 2011-5-30 ����01:56:49
 */

public abstract class ProbTask implements Runnable {
    protected Log log = LogFactory.getLog(this.getClass());


    public void run() {
        try {
            this.doExecute();
        }
        catch (InterruptedException e) {
            this.log.warn("̽���߳̽��յ��ж��ź�.");
            Thread.currentThread().interrupt();
        }
        catch (Throwable e) {
            // ����������쳣,����ScheduledExecutorService����ִ�к���̽������
            this.handleExceptionInner(e);
        }
    }


    private void handleExceptionInner(Throwable e) {
        try {
            this.handleException(e);
        }
        catch (Throwable e2) {
            // ignore
        }
    }


    abstract protected void doExecute() throws Exception;


    abstract protected void handleException(Throwable e);

}
