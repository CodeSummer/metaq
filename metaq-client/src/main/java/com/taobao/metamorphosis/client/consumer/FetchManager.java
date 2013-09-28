package com.taobao.metamorphosis.client.consumer;

/**
 * Fetch����������ӿ�
 * 
 * @author boyan
 * @Date 2011-5-4
 * 
 */
public interface FetchManager {
    /**
     * ֹͣfetch
     * 
     * @throws InterruptedException
     */
    public void stopFetchRunner() throws InterruptedException;


    /**
     * ����״̬������״̬������ò�start
     */
    public void resetFetchState();


    /**
     * ����������
     */
    public void startFetchRunner();


    /**
     * ���fetch����
     * 
     * @param request
     */
    public void addFetchRequest(FetchRequest request);


    /**
     * �Ƿ�ر�
     * 
     * @return
     */
    public boolean isShutdown();
}
