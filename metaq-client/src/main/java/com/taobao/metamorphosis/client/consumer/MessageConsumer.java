package com.taobao.metamorphosis.client.consumer;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.taobao.metamorphosis.client.Shutdownable;
import com.taobao.metamorphosis.client.consumer.storage.OffsetStorage;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ��Ϣ�����ߣ��̰߳�ȫ���Ƽ�����
 * 
 * @author boyan
 * @Date 2011-4-21
 * 
 */
public interface MessageConsumer extends Shutdownable {

    /**
     * ��ȡָ��topic�ͷ����������Ϣ��Ĭ�ϳ�ʱ10��
     * 
     * @param topic
     * @param partition
     * @return ��Ϣ������������Ϊnull
     */
    @Deprecated
    public MessageIterator get(String topic, Partition partition, long offset, int maxSize)
            throws MetaClientException, InterruptedException;


    /**
     * ��ȡָ��topic�ͷ����������Ϣ����ָ��ʱ����û�з������׳��쳣
     * 
     * @param topic
     * @param partition
     * @param timeout
     * @param timeUnit
     * @return ��Ϣ������������Ϊnull
     * @throws TimeoutException
     */
    public MessageIterator get(String topic, Partition partition, long offset, int maxSize, long timeout,
            TimeUnit timeUnit) throws MetaClientException, InterruptedException;


    public DequeueResult dequeue(String topic, Partition partition, long offset, int maxSize)
            throws MetaClientException, InterruptedException;


    public DequeueResult dequeue(String topic, Partition partition, long offset, int maxSize, long timeout,
            TimeUnit timeUnit) throws MetaClientException, InterruptedException;


    /**
     * ����ָ������Ϣ������MessageListener��������Ϣ�ﵽ��ʱ������֪ͨMessageListener����ע�⣬
     * ���ô˷���������ʹ���Ĺ�ϵ������Ч�� ֻ���ڵ���complete���������Ч���˷���������ʽ����
     * 
     * @param topic
     *            ���ĵ�topic
     * @param maxSize
     *            ����ÿ�ν��յ�������ݴ�С,���ܳ���1M
     * @param messageListener
     *            ��Ϣ������
     */
    public MessageConsumer subscribe(String topic, int maxSize, MessageListener messageListener)
            throws MetaClientException;


    /**
     * ����ָ������Ϣ������MessageListener��������Ϣ�ﵽ��ʱ������֪ͨMessageListener����ע�⣬
     * ���ô˷���������ʹ���Ĺ�ϵ������Ч�� ֻ���ڵ���complete���������Ч���˷���������ʽ����
     * 
     * @param topic
     * @param maxSize
     * @param messageListener
     * @param features
     * @return
     * @throws MetaClientException
     */
    public MessageConsumer subscribe(String topic, int maxSize, MessageListener messageListener,
            String[] messageTypes) throws MetaClientException;


    /**
     * ����������Ϣ,��ע�⣬���ô˷���������ʹ���Ĺ�ϵ������Ч��ֻ���ڵ���complete���������Ч��
     * 
     * @param subscriptions
     */
    public void setSubscriptions(Collection<Subscription> subscriptions) throws MetaClientException;


    /**
     * ʹ���Ѿ����ĵ�topic��Ч,�˷������ܵ���һ��,�ٴε�����Ч�����׳��쳣
     */
    public void completeSubscribe() throws MetaClientException;


    /**
     * ���ش�������ʹ�õ�offset�洢�����ɹ��������������
     * 
     * @return
     */
    public OffsetStorage getOffsetStorage();


    /**
     * ֹͣ������
     */
    public void shutdown() throws MetaClientException;


    /**
     * ��������������
     * 
     * @return
     */
    public ConsumerConfig getConsumerConfig();

}