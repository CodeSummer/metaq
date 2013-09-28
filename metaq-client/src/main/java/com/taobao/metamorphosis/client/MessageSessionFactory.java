package com.taobao.metamorphosis.client;

import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.storage.OffsetStorage;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.client.producer.PartitionSelector;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ��Ϣ�Ự������meta�ͻ��˵����ӿ�,�Ƽ�һ��Ӧ��ֻʹ��һ��MessageSessionFactory
 * 
 * @author boyan
 * @Date 2011-4-27
 * 
 */
public interface MessageSessionFactory extends Shutdownable {

    /**
     * �رչ���
     * 
     * @throws MetaClientException
     */
    @Override
    public void shutdown() throws MetaClientException;


    /**
     * ������Ϣ������
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @return
     */
    public MessageProducer createProducer(PartitionSelector partitionSelector);


    /**
     * ������Ϣ�����ߣ�Ĭ��ʹ����ѯ����ѡ����
     * 
     * @return
     */
    public MessageProducer createProducer();


    /**
     * ������Ϣ�����ߣ�Ĭ��ʹ����ѯ����ѡ�������������Ѿ�����������ʹ�ã����ų���δ��ĳ���汾ɾ����
     * 
     * @param ordered
     *            �Ƿ�����trueΪ���������������Ϣ���շ���˳�򱣴���MQ server
     * @return
     */
    @Deprecated
    public MessageProducer createProducer(boolean ordered);


    /**
     * ������Ϣ������,�������Ѿ�����������ʹ�ã����ų���δ��ĳ���汾ɾ����
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @param ordered
     *            �Ƿ�����trueΪ���������������Ϣ���շ���˳�򱣴���MQ server
     * @return
     */
    @Deprecated
    public MessageProducer createProducer(PartitionSelector partitionSelector, boolean ordered);


    /**
     * ������Ϣ�����ߣ�Ĭ�Ͻ�offset�洢��zk
     * 
     * @param consumerConfig
     *            ����������
     * @return
     */
    public MessageConsumer createConsumer(ConsumerConfig consumerConfig);


    /**
     * ������Ϣ�����ߣ�ʹ��ָ����offset�洢��
     * 
     * @param consumerConfig
     *            ����������
     * @param offsetStorage
     *            offset�洢��
     * @return
     */
    public MessageConsumer createConsumer(ConsumerConfig consumerConfig, OffsetStorage offsetStorage);

}