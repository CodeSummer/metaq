package com.taobao.metamorphosis.client.consumer;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Ignore;

import com.taobao.metamorphosis.client.consumer.ConsumerZooKeeper.ZKLoadRebalanceListener;
import com.taobao.metamorphosis.cluster.Broker;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * Э���������µĲ����� ��ȡConsumerZooKeeper��˽�е��ڲ�״̬
 * 
 * @author �޻�
 * @since 2011-6-29 ����06:09:33
 */
@Ignore("�����뵥Ԫ����")
public class ConsumerZooKeeperAccessor {

    public static ZKLoadRebalanceListener getBrokerConnectionListenerForTest(ConsumerZooKeeper consumerZooKeeper,
            FetchManager fetchManager) {
        return consumerZooKeeper.getBrokerConnectionListener(fetchManager);
    }


    public static Collection<TopicPartitionRegInfo> getTopicPartitionRegInfos(ConsumerZooKeeper consumerZooKeeper,
            FetchManager fetchManager) {
        return getBrokerConnectionListenerForTest(consumerZooKeeper, fetchManager).getTopicPartitionRegInfos();
    }


    public static ConcurrentHashMap<String/* topic */, ConcurrentHashMap<Partition, TopicPartitionRegInfo>> getTopicRegistry(
            ZKLoadRebalanceListener listener) {
        return listener.topicRegistry;
    }


    public static Set<Broker> getOldBrokerSet(ZKLoadRebalanceListener listener) {
        return listener.oldBrokerSet;
    }

}
