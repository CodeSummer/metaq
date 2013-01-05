package com.taobao.metamorphosis;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import com.taobao.metamorphosis.gregor.master.SamsaMasterBroker;
import com.taobao.metamorphosis.gregor.slave.GregorSlaveBroker;
import com.taobao.metamorphosis.http.MetamorphosisOnJettyBroker;
import com.taobao.metamorphosis.metaslave.MetamorphosisSlaveBroker;
import com.taobao.metamorphosis.notifyslave.SlaveBroker;
import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;
import com.taobao.metamorphosis.server.exception.MetamorphosisServerStartupException;
import com.taobao.metamorphosis.timetunnel.TimetunnelBroker;


/**
 * @author �޻�
 * @since 2011-6-9 ����01:36:39
 */

public class BrokerPlugins extends AbstractBrokerPlugin {

    /**
     * ��ע���plugins
     */
    private final Map<String/* plugin name */, BrokerPlugin> plugins = new LinkedHashMap<String, BrokerPlugin>();

    /**
     * ��Ҫ������plugins
     */
    private final Map<String/* plugin name */, Properties> pluginsInfo = new HashMap<String, Properties>();

    private final AtomicBoolean isInited = new AtomicBoolean(false);


    public BrokerPlugins(final Map<String, Properties> pluginsInfo, final MetaMorphosisBroker broker) {
        // ͬ�����ƣ� Master
        this.register(SamsaMasterBroker.class);

        // ͬ�����ƣ� Slave
        this.register(GregorSlaveBroker.class);
        
        // Meta�첽����: Slave��Master�첽������
        this.register(MetamorphosisSlaveBroker.class);

        // ����һ��TT�ͻ��ˣ���TT�����ݴ洢��Meta����
        this.register(TimetunnelBroker.class);

        // ����һ��Notify�ͻ���, ��Notify�����ݴ洢��Meta����
        this.register(SlaveBroker.class);

        // ��META����ӿڱ�¶��HTTP�ӿ���ʽ����Ӧ��HTTP�ͻ���������ʵ����kongming��ɣ�
        this.register(MetamorphosisOnJettyBroker.class);
		
		this.broker = broker;

        if (pluginsInfo != null) {
            this.pluginsInfo.putAll(pluginsInfo);
        }

        this.checkPluginsInfo(this.plugins, this.pluginsInfo);
    }


    private void checkPluginsInfo(final Map<String, BrokerPlugin> plugins, final Map<String, Properties> pluginsInfo) {
        if (pluginsInfo == null || pluginsInfo.isEmpty()) {
            this.log.info("no broker plugin");
            return;
        }

        // ��Ϊ�첽����Slave����ʱ���⴦������������plugin
        if (pluginsInfo.containsKey(MetamorphosisSlaveBroker.NAME)) {
            log.info("start as meta slaver,unstart other plugins");
            final Properties slaveProperties = pluginsInfo.get(MetamorphosisSlaveBroker.NAME);
            pluginsInfo.clear();
            pluginsInfo.put(MetamorphosisSlaveBroker.NAME, slaveProperties);
        }

        for (final String name : pluginsInfo.keySet()) {
            this.log.info("cmd line require start plugin:" + name);
            if (plugins.get(name) == null) {
                throw new MetamorphosisServerStartupException("unknown broker plugin:" + name);
            }
        }
    }


    @Override
    public void init(final MetaMorphosisBroker broker, final Properties props) {
        if (this.isInited.compareAndSet(false, true)) {
            new InnerPluginsRunner() {
                @Override
                protected void doExecute(final BrokerPlugin plugin) {
                    BrokerPlugins.this.log.info("Start inited broker plugin:[" + plugin.name() + ":"
                            + plugin.getClass().getName() + "]");
                    plugin.init(broker, BrokerPlugins.this.pluginsInfo.get(plugin.name()));
                    BrokerPlugins.this.log.info("Inited broker plugin:[" + plugin.name() + ":"
                            + plugin.getClass().getName() + "]");
                }
            }.execute();
        }
    }


    void register(final Class<? extends BrokerPlugin> pluginClass) {
        try {
            final BrokerPlugin plugin = pluginClass.getConstructor(new Class[0]).newInstance();
            this.plugins.put(plugin.name(), plugin);
        }
        catch (final Exception e) {
            throw new MetamorphosisServerStartupException("Register broker plugin failed", e);
        }
    }


    @Override
    public String name() {
        return null;
    }


    @Override
    public void start() {
        if (!this.isInited.get()) {
            this.log.warn("Not inited yet");
            return;
        }

        new InnerPluginsRunner() {
            @Override
            protected void doExecute(final BrokerPlugin plugin) {
                plugin.start();
                BrokerPlugins.this.log.info("Started broker plugin:[" + plugin.name() + ":"
                        + plugin.getClass().getName() + "]");
            }
        }.execute();
    }


    @Override
    public void stop() {
        new InnerPluginsRunner() {
            @Override
            protected void doExecute(final BrokerPlugin plugin) {
                plugin.stop();
                BrokerPlugins.this.log.info("stoped broker plugin:[" + plugin.name() + ":"
                        + plugin.getClass().getName() + "]");
            }
        }.execute();
    }

    private abstract class InnerPluginsRunner {

        public void execute() {
            for (final BrokerPlugin plugin : BrokerPlugins.this.plugins.values()) {
                if (BrokerPlugins.this.pluginsInfo.containsKey(plugin.name())) {
                    this.doExecute(plugin);
                }
                else {
                    BrokerPlugins.this.log.info("unstarted plugin:" + plugin.name());
                }
            }
        }


        protected abstract void doExecute(BrokerPlugin plugin);
    }


    // for test
    Map<String, Properties> getPluginsInfo() {
        return this.pluginsInfo;
    }

}
