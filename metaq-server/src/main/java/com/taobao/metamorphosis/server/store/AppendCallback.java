package com.taobao.metamorphosis.server.store;

/**
 * Append�ص�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-11-29
 * 
 */
public interface AppendCallback {

    /**
     * ��append�ɹ���ص��˷���������д���location
     * 
     * @param location
     */
    public void appendComplete(Location location);
}
