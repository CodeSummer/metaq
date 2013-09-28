package com.taobao.metamorphosis.server.network;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.gecko.service.Connection;
import com.taobao.gecko.service.ConnectionLifeCycleListener;
import com.taobao.metamorphosis.server.transaction.Transaction;


/**
 * ���ӶϿ���ʱ��Ӧ�ûع������������еı�������
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-8-26
 * 
 */
public class LocalTransactionRollbackListener implements ConnectionLifeCycleListener {

    static final Log log = LogFactory.getLog(LocalTransactionRollbackListener.class);


    @Override
    public void onConnectionCreated(final Connection conn) {

    }


    @Override
    public void onConnectionReady(final Connection conn) {

    }


    /**
     * ���ӶϿ���ʱ��ع����б�������
     */
    @Override
    public void onConnectionClosed(final Connection conn) {
        final Set<String> keySet = conn.attributeKeySet();
        try {
            for (final String key : keySet) {
                final Object obj = conn.getAttribute(key);
                if (obj instanceof SessionContext) {
                    final SessionContext ctx = (SessionContext) obj;
                    for (final Transaction tx : ctx.getTransactions().values()) {
                        try {
                            tx.rollback();
                        }
                        catch (final Exception e) {
                            log.error("���ӶϿ����ع������������", e);
                        }
                    }
                }

            }
        }
        catch (final Throwable t) {
            log.error("���ӶϿ����ع������������", t);
        }

    }
}
