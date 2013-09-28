package com.taobao.metamorphosis.client.consumer;

import java.util.List;


/**
 * Consumer��balance����
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-11-29
 * 
 */
public interface LoadBalanceStrategy {

    enum Type {
        DEFAULT,
        CONSIST
    }


    /**
     * ����consumer id���Ҷ�Ӧ�ķ����б�
     * 
     * @param topic
     *            ����topic
     * @param consumerId
     *            consumerId
     * @param curConsumers
     *            ��ǰ���е�consumer�б�
     * @param curPartitions
     *            ��ǰ�ķ����б�
     * 
     * @return
     */
    public List<String> getPartitions(String topic, String consumerId, final List<String> curConsumers,
            final List<String> curPartitions);
}
