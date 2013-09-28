package com.taobao.metamorphosis.client;

import org.I0Itec.zkclient.ZkClient;


/**
 * ZkClient���������
 * 
 * @author boyan
 * @Date 2011-4-26
 * 
 */
public interface ZkClientChangedListener {
    /**
     * ���µ�zkClient������ʱ��
     * 
     * @param newClient
     */
    public void onZkClientChangedBefore(ZkClient newClient);
    
    public void onZkClientChanged(ZkClient newClient);
}
