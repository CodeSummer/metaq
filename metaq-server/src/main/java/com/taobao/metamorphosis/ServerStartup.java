package com.taobao.metamorphosis;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.metamorphosis.server.exception.MetamorphosisServerStartupException;
import com.taobao.metamorphosis.server.utils.MetaConfig;


/**
 * @author �޻�
 * @since 2011-6-9 ����03:28:28
 */

public class ServerStartup {
    static final Log log = LogFactory.getLog(ServerStartup.class);


    public static void main(final String[] args) throws Exception {
        final CommandLine line = StartupHelp.parseCmdLine(args, new PosixParser());
        final Map<String, Properties> pluginsInfo = getPluginsInfo(line);
        final EnhancedBroker broker = new EnhancedBroker(getMetaConfig(line), pluginsInfo);
        broker.start();
    }


    private static MetaConfig getMetaConfig(final CommandLine line) {
        // broker����ʱ���ص�ϵͳ���á�ZK���á�topic����
        final String serverOption = "f";
        String serverConfigFile = "../conf/server.ini";
        if (line.hasOption(serverOption)) {
            serverConfigFile = line.getOptionValue(serverOption);
            if (StringUtils.isBlank(serverConfigFile)) {
                throw new MetamorphosisServerStartupException("-f Blank file path");
            }
        }

        // broker����ʱ���ص�topic����
        final String topicOption = "t";
        String topicConfigFile = "../conf/topics.ini";
        if (line.hasOption(topicOption)) {
            topicConfigFile = line.getOptionValue(topicOption);
            if (StringUtils.isBlank(topicConfigFile)) {
                throw new MetamorphosisServerStartupException("-t Blank file path");
            }
        }

        final MetaConfig metaConfig = new MetaConfig();
        metaConfig.loadRootConfig(serverConfigFile);
        metaConfig.loadTopicConfig(topicConfigFile);
        metaConfig.verify();
        log.warn("META Broker ���ص������ļ���" + serverConfigFile + " " + topicConfigFile);
        log.warn("META Broker ���ò�����" + metaConfig);
        return metaConfig;
    }


    static Map<String, Properties> getPluginsInfo(final CommandLine line) {
        final Properties properties = line.getOptionProperties("F");
        final Map<String, Properties> pluginsInfo = new HashMap<String, Properties>();
        if (properties != null) {
            for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                pluginsInfo.put(String.valueOf(entry.getKey()), StartupHelp.getProps(String.valueOf(entry.getValue())));
            }
        }
        return pluginsInfo;

    }

}
