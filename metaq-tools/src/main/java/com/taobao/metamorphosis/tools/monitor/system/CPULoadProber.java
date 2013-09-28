package com.taobao.metamorphosis.tools.monitor.system;

import com.taobao.metamorphosis.tools.monitor.core.CoreManager;
import com.taobao.metamorphosis.tools.monitor.core.MsgSender;
import com.taobao.metamorphosis.tools.utils.CPULoadUtil;
import com.taobao.metamorphosis.tools.utils.MonitorResult;


/**
 * 
 * @author �޻�
 * @since 2011-9-28 ����11:19:05
 */

public class CPULoadProber extends SystemProber {

    public CPULoadProber(CoreManager coreManager) {
        super(coreManager);
    }


    @Override
    protected void processResult(MonitorResult monitorResult) {
        if (monitorResult.getValue().intValue() > this.getMonitorConfig().getCpuLoadThreshold()) {
            this.alert(monitorResult.getIp() + " load �Ѿ����� " + monitorResult.getValue());
        }
    }


    @Override
    protected MonitorResult getMonitorResult(final MsgSender sender) throws Exception {
        return CPULoadUtil.getCpuLoad(sender.getHost(), CPULoadProber.this.getMonitorConfig().getLoginUser(),
            CPULoadProber.this.getMonitorConfig().getLoginPassword());
    }

}
