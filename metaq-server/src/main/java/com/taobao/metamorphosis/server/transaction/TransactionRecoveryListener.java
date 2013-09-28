package com.taobao.metamorphosis.server.transaction;

import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.transaction.XATransactionId;


/**
 * ����ָ�������
 * 
 * @author boyan
 * 
 */
public interface TransactionRecoveryListener {
    void recover(XATransactionId xid, PutCommand[] addedMessages);
}
