package com.taobao.metamorphosis.client.consumer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * �������ṩ��consumer�ӿڣ������ṩ��Fetchʹ��
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-9-13
 * 
 */
public interface InnerConsumer {

    /**
     * ץȡ��Ϣ
     * 
     * @param fetchRequest
     * @param timeout
     * @param timeUnit
     * @return
     * @throws MetaClientException
     * @throws InterruptedException
     */
    MessageIterator fetch(final FetchRequest fetchRequest, long timeout, TimeUnit timeUnit)
            throws MetaClientException, InterruptedException;


    /**
     * ����1.X��2.X�汾
     */
    FetchResult fetchAll(final FetchRequest fetchRequest, long timeout, TimeUnit timeUnit)
            throws MetaClientException, InterruptedException;
    
    /**
     * ͬ������Ϣ
     */
    DequeueResult fetchSync(final FetchRequest fetchRequest, long timeout, TimeUnit timeUnit)
            throws MetaClientException, InterruptedException;


    /**
     * ����topic��Ӧ����Ϣ������
     * 
     * @param topic
     * @return
     */
    MessageListener getMessageListener(final String topic);


    /**
     * �����޷����ͻ������ѵ���Ϣ
     * 
     * @param message
     * @throws IOException
     */
    void appendCouldNotProcessMessage(final Message message) throws IOException;


    /**
     * ��ѯoffset
     * 
     * @param fetchRequest
     * @return
     * @throws MetaClientException
     */
    long offset(final FetchRequest fetchRequest) throws MetaClientException;

}
