package com.taobao.metamorphosis.gregor.slave;

import com.taobao.metamorphosis.network.SyncCommand;
import com.taobao.metamorphosis.server.network.PutCallback;
import com.taobao.metamorphosis.server.network.SessionContext;


/**
 * ͬ�������ӿ�
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-12-14
 * 
 */
public interface SyncCommandProcessor {
    /**
     * ����ͬ������
     * 
     * @param request
     * @param sessionContext
     * @param cb
     */
    public void processSyncCommand(final SyncCommand request, final SessionContext sessionContext, final PutCallback cb);
}
