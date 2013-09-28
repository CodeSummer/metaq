package com.taobao.metamorphosis.server.assembly;

/**
 * ��������MBean�ӿڣ��ṩһЩ��ѯ�͹����API
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-8-29
 * 
 */
public interface TransactionalCommandProcessorMBean {

    /**
     * �������д���prepare״̬��xa����
     * 
     * @return
     */
    public String[] getPreparedTransactions() throws Exception;


    /**
     * �������д���prepare״̬��xa������Ŀ
     * 
     * @return
     */
    public int getPreparedTransactionCount() throws Exception;


    /**
     * �˹��ύ����
     * 
     * @param txKey
     */
    public void commitTransactionHeuristically(String txKey, boolean onePhase) throws Exception;


    /**
     * �˹��ع�����
     * 
     * @param txKey
     */
    public void rollbackTransactionHeuristically(String txKey) throws Exception;


    /**
     * �˹�������񣬲��ύҲ���ع�����ɾ��
     * 
     * @param txKey
     * @throws Exception
     */
    public void completeTransactionHeuristically(String txKey) throws Exception;

}
