package com.taobao.metamorphosis.server.network;

import com.taobao.gecko.core.command.ResponseCommand;

/**
 * Put��Ϣ�Ļص��ӿ�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-11-29
 * 
 */
public interface PutCallback {

    public void putComplete(ResponseCommand resp);
}
