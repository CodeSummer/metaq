package com.taobao.metamorphosis.monitor;

import com.taobao.metamorphosis.utils.MetaMBeanServer;
import com.taobao.metaq.store.MetaStore;

/**
 * ͨ��jmxִ��broker�ϵĸ��ֲ���
 */
public class JmxManipulation implements JmxManipulationMBean {
	
	private final MetaStore metaStore;
	
	
	public JmxManipulation(MetaStore metaStore) {
		this.metaStore = metaStore;
		MetaMBeanServer.registMBean(this, null);
	}
	
	
	@Override
	public void triggerDeleteFiles() {
		this.metaStore.excuteDeleteFilesManualy();
	}
}
