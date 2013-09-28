package com.taobao.metamorphosis.client.producer;

import java.util.List;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ����ѡ����
 * 
 * @author boyan
 * @Date 2011-4-26
 * 
 */
public interface PartitionSelector {

    /**
     * ����topic��message��partitions�б���ѡ�����
     * 
     * @param topic
     *            topic
     * @param partitions
     *            �����б�
     * @param message
     *            ��Ϣ
     * @return
     * @throws MetaClientException
     *             �˷����׳����κ��쳣��Ӧ����װΪMetaClientException
     */
    public Partition getPartition(String topic, List<Partition> partitions, Message message) throws MetaClientException;
}
