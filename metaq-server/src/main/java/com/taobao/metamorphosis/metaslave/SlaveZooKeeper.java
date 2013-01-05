package com.taobao.metamorphosis.metaslave;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

import com.taobao.metamorphosis.cluster.Broker;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;
import com.taobao.metamorphosis.utils.MetaZookeeper;


/**
 * �����zk����,�����master��zk�ϵ�ע��
 * 
 * @author �޻�
 * @since 2011-6-24 ����05:46:36
 */

class SlaveZooKeeper {
    private final static Logger log = Logger.getLogger(PullMessageController.SlaveLogName);

    private final MetaMorphosisBroker broker;
    private final PullMessageController pullMessageController;
    private final MasterBrokerIdListener masterBrokerIdListener;


    public SlaveZooKeeper(final MetaMorphosisBroker broker, final PullMessageController pullMessageController) {
        this.broker = broker;
        this.pullMessageController = pullMessageController;
        this.masterBrokerIdListener = new MasterBrokerIdListener();
    }


    public void start() {
        // ����zk��Ϣ�仯
        this.getZkClient().subscribeDataChanges(
            this.getMetaZookeeper().brokerIdsPathOf(this.broker.getMetaConfig().getBrokerId(), -1),
            this.masterBrokerIdListener);
    }


    public String getMasterServerUrl() {
        final Broker masterBroker =
                this.getMetaZookeeper().getMasterBrokerById(this.broker.getMetaConfig().getBrokerId());
        return masterBroker != null ? masterBroker.getZKString() : null;
    }


    public Map<String, List<Partition>> getPartitionsForTopicsFromMaster() {
        return this.getMetaZookeeper().getPartitionsForTopicsFromMaster(this.getMasterTopics(),
            this.broker.getMetaConfig().getBrokerId());
    }


    private Set<String> getMasterTopics() {
        return this.getMetaZookeeper().getTopicsByBrokerIdFromMaster(this.broker.getMetaConfig().getBrokerId());
    }


    private ZkClient getZkClient() {
        return this.broker.getBrokerZooKeeper().getZkClient();
    }


    private MetaZookeeper getMetaZookeeper() {
        return this.broker.getBrokerZooKeeper().getMetaZookeeper();
    }

    private final class MasterBrokerIdListener implements IZkDataListener {

        @Override
        public void handleDataChange(final String dataPath, final Object data) throws Exception {
            // ����slave��������master������ʱ
            log.info("SlaveZooKeeper data changed in zk,path=" + dataPath);
            int zkSyncTimeMs;
            try {
                zkSyncTimeMs = broker.getMetaConfig().getZkConfig().zkSyncTimeMs;
            }
            catch (Exception e) {
                zkSyncTimeMs = 5000;
                // ignore
            }
            // �ȴ�zk����ͬ���������������
            Thread.sleep(zkSyncTimeMs);
            String masterUrl = SlaveZooKeeper.this.getMasterServerUrl();
            if (masterUrl != null) {
                SlaveZooKeeper.this.pullMessageController.setMasterServerUrl(masterUrl);
                SlaveZooKeeper.this.pullMessageController.setMasterFixed(true);
                log.info("SlaveZooKeeper got new master server url OK, " + masterUrl);
            }
            else {
                log.error("SlaveZooKeeper got new master server url Failed");
            }
        }


        @Override
        public void handleDataDeleted(final String dataPath) throws Exception {
            log.info("SlaveZooKeeper data deleted in zk,path=" + dataPath);
        }
    }

}
