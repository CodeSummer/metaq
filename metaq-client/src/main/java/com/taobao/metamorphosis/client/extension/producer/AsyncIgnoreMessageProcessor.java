package com.taobao.metamorphosis.client.extension.producer;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.client.extension.producer.AsyncMessageProducer.IgnoreMessageProcessor;
import com.taobao.metamorphosis.client.extension.producer.MessageRecoverManager.MessageRecoverer;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * 
 * @author �޻�
 * @since 2011-10-27 ����11:43:35
 */

class AsyncIgnoreMessageProcessor implements IgnoreMessageProcessor {

    private static final Log log = LogFactory.getLog(AsyncIgnoreMessageProcessor.class);

    private static final String STORAGE_PATH = System.getProperty("meta.async.storage.path",
        System.getProperty("user.home") + File.separator + ".meta_async_storage");

    /**
     * ���ش��̻������Ϣ��������
     */
    private final int messageCountLimit = 500000;

    private MessageRecoverManager storageManager;


    AsyncIgnoreMessageProcessor(MetaClientConfig metaClientConfig, MessageRecoverer recoverer) {
        this.storageManager = new LocalMessageStorageManager(metaClientConfig, STORAGE_PATH, recoverer);
    }


    /**
     * ��Ϣ���뱾�ش��̲����ڻָ�
     */
    @Override
    public boolean handle(Message message) throws Exception {
        Partition partition = message.getPartition();
        partition = (partition != null ? partition : Partition.RandomPartiton);
        int count = this.storageManager.getMessageCount(message.getTopic(), partition);
        if (count < this.messageCountLimit) {
            this.storageManager.append(message, partition);
            return true;
        }
        else {
            log.info("local storage is full,ignore message");
            return false;
        }
    }


    // for test
    void setStorageManager(MessageRecoverManager storageManager) {
        this.storageManager = storageManager;
    }
}
