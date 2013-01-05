package com.taobao.metamorphosis;

import java.util.Properties;

import com.taobao.metamorphosis.server.assembly.MetaMorphosisBroker;


/**
 * @author �޻�
 * @since 2011-6-9 ����01:29:19
 */

interface BrokerPlugin {

    public void start();


    public void stop();


    public void init(MetaMorphosisBroker metaMorphosisBroker, Properties props);


    public String name();

}
