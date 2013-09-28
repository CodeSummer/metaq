package com.taobao.metamorphosis.client.extension.producer;

import java.io.IOException;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.Shutdownable;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * ��Ϣ�ݴ��recover�������ĳ���
 * 
 * @author �޻�
 * @since 2011-10-27 ����3:34:12
 */

public interface MessageRecoverManager extends Shutdownable {

    /**
     * ȫ���ָ�
     */
    public void recover();


    /**
     * �����ָ�һ������һ����������Ϣ
     * 
     * @param topic
     * @param partition
     * @param recoverer
     *            �ָ���������Ϣ�Ĵ�����
     * @return �Ƿ������ύ�˻ָ�����
     * */
    public boolean recover(final String topic, final Partition partition, final MessageRecoverer recoverer);


    /**
     * ������Ϣ
     * 
     * @param message
     * @param partition
     * @throws IOException
     */
    public void append(Message message, Partition partition) throws IOException;


    /**
     * ��Ϣ����
     * 
     * @param topic
     * @param partition
     * @return
     */
    public int getMessageCount(String topic, Partition partition);


    /**
     * ������λָ���Ϣ�Ĵ�����
     * 
     * @param recoverer
     */
    public void setMessageRecoverer(MessageRecoverer recoverer);

    /**
     * ָ����Ϣ���recover
     * 
     * @author wuhua
     * 
     */
    public static interface MessageRecoverer {
        /**
         * recover��������Ϣ��δ���
         * 
         * @param msg
         * @throws Exception
         */
        public void handle(Message msg) throws Exception;
    }
}
