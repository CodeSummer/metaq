package com.taobao.metamorphosis.client.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Ĭ�ϵĸ��ؾ�����ԣ�����ʹ�ø���������consumer֮��ƽ�����䣬consumer֮�����ķ�������಻����1
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-11-29
 * 
 */
public class DefaultLoadBalanceStrategy implements LoadBalanceStrategy {

    static final Log log = LogFactory.getLog(DefaultLoadBalanceStrategy.class);


    @Override
    public List<String> getPartitions(final String topic, final String consumerId, final List<String> curConsumers,
            final List<String> curPartitions) {
        // ÿ��������ƽ�����ص�partition��Ŀ
        final int nPartsPerConsumer = curPartitions.size() / curConsumers.size();
        // ���ص�����partition��consumer��Ŀ
        final int nConsumersWithExtraPart = curPartitions.size() % curConsumers.size();

        log.info("Consumer " + consumerId + " rebalancing the following partitions: " + curPartitions + " for topic "
                + topic + " with consumers: " + curConsumers);
        final int myConsumerPosition = curConsumers.indexOf(consumerId);
        if (myConsumerPosition < 0) {
            log.warn("No broker partions consumed by consumer " + consumerId + " for topic " + topic);
            return Collections.emptyList();
        }
        assert myConsumerPosition >= 0;
        // �������
        final int startPart =
                nPartsPerConsumer * myConsumerPosition + Math.min(myConsumerPosition, nConsumersWithExtraPart);
        final int nParts = nPartsPerConsumer + (myConsumerPosition + 1 > nConsumersWithExtraPart ? 0 : 1);

        if (nParts <= 0) {
            log.warn("No broker partions consumed by consumer " + consumerId + " for topic " + topic);
            return Collections.emptyList();
        }
        final List<String> rt = new ArrayList<String>();
        for (int i = startPart; i < startPart + nParts; i++) {
            final String partition = curPartitions.get(i);
            rt.add(partition);
        }
        return rt;
    }

}
