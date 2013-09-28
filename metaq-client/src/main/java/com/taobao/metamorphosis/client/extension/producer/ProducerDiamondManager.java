package com.taobao.metamorphosis.client.extension.producer;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.utils.DiamondUtils;


/**
 * �����diamondȡ�÷�����������Ϣ�����˳����Ϣ�ķ���Ԥ���ã�
 * 
 * @author �޻�
 * @since 2011-8-17 ����3:32:58
 */

public class ProducerDiamondManager {

    private static Log log = LogFactory.getLog(ProducerDiamondManager.class);

    private final DiamondManager partitionsDiamondManager;

    private final Map<String/* topic */, List<Partition>> partitionsMap =
            new ConcurrentHashMap<String, List<Partition>>();


    public ProducerDiamondManager(MetaClientConfig metaClientConfig) {
        this.partitionsDiamondManager =
                new DefaultDiamondManager(metaClientConfig.getDiamondPartitionsGroup(),
                    metaClientConfig.getDiamondPartitionsDataId(), new ManagerListener() {

                        @Override
                        public Executor getExecutor() {
                            return null;
                        }


                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            final Properties properties = new Properties();
                            try {
                                properties.load(new StringReader(configInfo));
                                DiamondUtils.getPartitions(properties, ProducerDiamondManager.this.partitionsMap);
                            }
                            catch (Exception e) {
                                log.error("��diamond����zk����ʧ��", e);
                            }
                        }

                    });
        DiamondUtils.getPartitions(this.partitionsDiamondManager, 10000, this.partitionsMap);
    }


    // for test
    ProducerDiamondManager(DiamondManager partitionsDiamondManager) {
        this.partitionsDiamondManager = partitionsDiamondManager;
        DiamondUtils.getPartitions(this.partitionsDiamondManager, 10000, this.partitionsMap);
    }


    public Map<String, List<Partition>> getPartitions() {
        return Collections.unmodifiableMap(this.partitionsMap);
    }
}
