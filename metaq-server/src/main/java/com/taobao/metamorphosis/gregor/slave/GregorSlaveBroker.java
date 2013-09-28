package com.taobao.metamorphosis.gregor.slave;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.taobao.metamorphosis.AbstractBrokerPlugin;
import com.taobao.metamorphosis.network.SyncCommand;
import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.utils.NamedThreadFactory;


/**
 * Slave broker�������ĸ����߶�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-12-14
 * 
 */
public class GregorSlaveBroker extends AbstractBrokerPlugin {
    OrderedThreadPoolExecutor orderedPutExecutor;


    @Override
    public void start() {

    }


    @Override
    public void stop() {
        if (this.orderedPutExecutor != null) {
            this.orderedPutExecutor.shutdown();
        }

    }


    @Override
    public void init(final MetaMorphosisBroker metaMorphosisBroker, final Properties props) {
        final MetaConfig metaConfig = metaMorphosisBroker.getMetaConfig();
        // slave��ע�ᵽzk,ǿ��
        metaMorphosisBroker.getBrokerZooKeeper().getZkConfig().zkEnable = false;
        this.orderedPutExecutor =
                new OrderedThreadPoolExecutor(metaConfig.getPutProcessThreadCount(),
                    metaConfig.getPutProcessThreadCount(), 60, TimeUnit.SECONDS, new NamedThreadFactory("putProcessor"));
        final GregorCommandProcessor processor =
                new GregorCommandProcessor(metaMorphosisBroker.getStoreManager(),
                    metaMorphosisBroker.getExecutorsManager(), metaMorphosisBroker.getStatsManager(),
                    metaMorphosisBroker.getRemotingServer(), metaMorphosisBroker.getMetaConfig(),
                    metaMorphosisBroker.getIdWorker(), metaMorphosisBroker.getBrokerZooKeeper(), 
                    metaMorphosisBroker.getMessageTypeManager());
        // ǿ������processor
        metaMorphosisBroker.setBrokerProcessor(processor);
        final SyncProcessor syncProcessor = new SyncProcessor(processor, this.orderedPutExecutor);
        // ע�����紦����
        metaMorphosisBroker.getRemotingServer().registerProcessor(SyncCommand.class, syncProcessor);
    }


    @Override
    public String name() {
        return "gregor";
    }

}
