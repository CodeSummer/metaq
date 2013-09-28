package com.taobao.metamorphosis.server.utils;

/**
 * Meta config mbean
 * 
 * @author boyan
 * @Date 2011-7-14
 * 
 */
public interface MetaConfigMBean {
    /**
     * Reload topics configuration
     */
    public void reload();


    /** �رշ��� */
    public void closePartitions(String topic, int start, int end);


    /** ��һ��topic�����з��� */
    public void openPartitions(String topic);

}
