package com.taobao.metamorphosis.client.consumer.storage;

import java.util.Collection;

import com.taobao.metamorphosis.client.consumer.TopicPartitionRegInfo;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * Offset�洢���ӿ�
 * 
 * @author boyan
 * @Date 2011-4-28
 * 
 */
public interface OffsetStorage {
    /**
     * ����offset���洢
     * 
     * @param group
     *            ����������
     * @param infoList
     *            �����߶��ĵ���Ϣ������Ϣ�б�
     */
    public void commitOffset(String group, Collection<TopicPartitionRegInfo> infoList);


    /**
     * ����һ�������ߵĶ�����Ϣ����������ڷ���null
     * 
     * @param topic
     * @param group
     * @param partiton
     * @return
     */
    public TopicPartitionRegInfo load(String topic, String group, Partition partition);


    /**
     * �ͷ���Դ��meta�ͻ����ڹرյ�ʱ����������ô˷���
     */
    public void close();


    /**
     * ��ʼ��offset
     * 
     * @param topic
     * @param group
     * @param partition
     * @param offset
     */
    public void initOffset(String topic, String group, Partition partition, long offset);
}
