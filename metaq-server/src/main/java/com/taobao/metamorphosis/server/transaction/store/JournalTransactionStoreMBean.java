package com.taobao.metamorphosis.server.transaction.store;

/**
 * ��������MBean�ӿ�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-8-25
 * 
 */
public interface JournalTransactionStoreMBean {
    /**
     * ִ��checkpoint
     * 
     * @throws Exception
     */
    public void makeCheckpoint() throws Exception;


    /**
     * ���ص�ǰ��Ծ������
     * 
     * @return
     */
    public int getActiveTransactionCount();

}
