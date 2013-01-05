package com.taobao.metamorphosis.client.extension;

import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;


/**
 * �㲥��Ϣ�Ự����,ʹ�����������Consumer��ͬһ�����ڵ�ÿ̨���������յ�ͬһ����Ϣ,
 * �Ƽ�һ��Ӧ��ֻʹ��һ��MessageSessionFactory
 * 
 * @author �޻�
 * @since 2011-6-13 ����02:49:27
 */

public interface BroadcastMessageSessionFactory extends MessageSessionFactory {

    /**
     * �����㲥��ʽ���յ���Ϣ�����ߣ�offset���洢�ڱ���
     * 
     * @param consumerConfig
     *            ����������
     * 
     * @return
     * */
    public MessageConsumer createBroadcastConsumer(ConsumerConfig consumerConfig);
}
