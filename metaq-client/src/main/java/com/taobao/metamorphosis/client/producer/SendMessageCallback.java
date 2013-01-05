package com.taobao.metamorphosis.client.producer;



/**
 * ������Ϣ�Ļص�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-12-14
 * 
 */
public interface SendMessageCallback {

    /**
     * ����Ϣ���ͷ��غ�ص�����֪���ͽ��
     * 
     * @param result
     *            ���ͽ��
     */
    public void onMessageSent(SendResult result);


    /**
     * �������쳣��ʱ��ص�������
     * 
     * @param e
     */
    public void onException(Throwable e);

}
