package com.taobao.metamorphosis.client.extension;

import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.extension.producer.AsyncMessageProducer;
import com.taobao.metamorphosis.client.extension.producer.AsyncMessageProducer.IgnoreMessageProcessor;
import com.taobao.metamorphosis.client.producer.PartitionSelector;


/**
 * <pre>
 * ���ڴ����첽��������Ϣ�ĻỰ����. 
 * 
 * ʹ�ó���: 
 *      ���ڷ��Ϳɿ���Ҫ����ô��,��Ҫ����߷���Ч�ʺͽ��Ͷ�����Ӧ�õ�Ӱ�죬�������Ӧ�õ��ȶ���.
 *      ����,�ռ���־���û���Ϊ��Ϣ�ȳ���.
 * ע��:
 *      ������Ϣ�󷵻صĽ���в�����׼ȷ��messageId,partition,offset,��Щֵ����-1
 *      
 * @author �޻�
 * @since 2011-10-21 ����2:28:26
 * </pre>
 */

public interface AsyncMessageSessionFactory extends MessageSessionFactory {

    /**
     * �����첽�������Ϣ������
     * 
     * @return
     */
    public AsyncMessageProducer createAsyncProducer();


    /**
     * �����첽�������Ϣ������
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @return
     */
    public AsyncMessageProducer createAsyncProducer(final PartitionSelector partitionSelector);


    /**
     * �����첽�������Ϣ������
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @param slidingWindowSize
     *            ���Ʒ��������Ļ������ڴ�С,4k����ռ���ڵ�һ����λ,�ο�ֵ:���ڴ�СΪ20000�ȽϺ���. С��0����Ĭ��ֵ20000.
     *            ���ڿ���̫����ܵ���OOM����
     * @return
     */
    public AsyncMessageProducer createAsyncProducer(final PartitionSelector partitionSelector, int slidingWindowSize);


    /**
     * �����첽�������Ϣ������
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @param slidingWindowSize
     *            ���Ʒ��������Ļ������ڴ�С,4k����ռ���ڵ�һ����λ,�ο�ֵ:���ڴ�СΪ20000�ȽϺ���. С��0����Ĭ��ֵ20000.
     *            ���ڿ���̫����ܵ���OOM����
     * @param processor
     *            ���÷���ʧ�ܺͳ���������Ϣ�Ĵ�����,�û������Լ��ӹ���Щ��Ϣ��δ���
     * 
     * 
     * @return
     */
    public AsyncMessageProducer createAsyncProducer(final PartitionSelector partitionSelector,
            IgnoreMessageProcessor processor);
}
