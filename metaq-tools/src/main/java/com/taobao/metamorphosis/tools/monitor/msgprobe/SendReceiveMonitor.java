package com.taobao.metamorphosis.tools.monitor.msgprobe;

import org.apache.log4j.Logger;

import com.taobao.metamorphosis.tools.monitor.alert.Alarm;
import com.taobao.metamorphosis.tools.monitor.core.CoreManager;
import com.taobao.metamorphosis.tools.monitor.core.MonitorConfig;


/**
 * @author �޻�
 */

@Deprecated
public class SendReceiveMonitor {
    private static Logger logger = Logger.getLogger(SendReceiveMonitor.class);


    public static void main(String[] args) {

        MsgProber prober = null;
        MonitorConfig monitorConfig = new MonitorConfig();
        try {
            monitorConfig.loadInis("monitor.properties");
            CoreManager coreManager = CoreManager.getInstance(monitorConfig, 1);
            prober = new MsgProber(coreManager);
            prober.init();
        }
        catch (Throwable e) {
            logger.error("fail to startup", e);
            System.exit(-1);
        }

        try {
            prober.prob();
        }
        catch (Throwable e) {
            logger.error("���ϵͳ������ֹ", e);
            Alarm.alert("���ϵͳ������ֹ", monitorConfig);
        }

    }

}
