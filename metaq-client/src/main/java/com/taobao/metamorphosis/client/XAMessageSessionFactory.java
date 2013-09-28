package com.taobao.metamorphosis.client;

import com.taobao.metamorphosis.client.producer.PartitionSelector;
import com.taobao.metamorphosis.client.producer.XAMessageProducer;


/**
 * ���ڴ���XA��Ϣ�Ự�Ĺ���
 * 
 * @author boyan
 * 
 */
public interface XAMessageSessionFactory extends MessageSessionFactory {

    /**
     * ����XA��Ϣ������
     * 
     * @param partitionSelector
     *            ����ѡ����
     * @return
     */
    public XAMessageProducer createXAProducer(PartitionSelector partitionSelector);


    /**
     * ����XA��Ϣ�����ߣ�Ĭ��ʹ����ѯ����ѡ����
     * 
     * @return
     */
    public XAMessageProducer createXAProducer();

}
