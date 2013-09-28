/**
 * $Id: MetaStore.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.util.Set;

import com.taobao.metaq.commons.MetaMessage;
import com.taobao.metaq.commons.MetaMessageAnnotation;
import com.taobao.metaq.commons.MetaMessageWrapper;


/**
 * �洢��ӿ�
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public interface MetaStore {
    public static final String MetaStoreLogName = "MetaStore";


    /**
     * ����ʱ����������
     */
    public boolean load();


    /**
     * ��������
     */
    public void start() throws Exception;


    /**
     * �رշ���
     */
    public void shutdown();


    /**
     * ɾ�������ļ�����Ԫ���Ի�ʹ��
     */
    public void destroy();


    /**
     * �洢��Ϣ
     */
    public PutMessageResult putMessage(final MetaMessage msg, final MetaMessageAnnotation msgant);


    /**
     * ��ȡ��Ϣ�����typesΪnull����������
     */
    public GetMessageResult getMessage(final String topic, final int queueId, final long offset,
            final int maxSize, final Set<Integer> types);


    /**
     * ��ȡָ���������Offset ������в����ڣ�����-1
     */
    public long getMaxOffsetInQuque(final String topic, final int queueId);


    /**
     * ��ȡָ��������СOffset ������в����ڣ�����-1
     */
    public long getMinOffsetInQuque(final String topic, final int queueId);


    /**
     * ������Ϣʱ���ȡĳ�������ж�Ӧ��offset 1�����ָ��ʱ�䣨����֮ǰ֮���ж�Ӧ����Ϣ�����ȡ�����ʱ�������offset������ѡ��֮ǰ��
     * 2�����ָ��ʱ���޶�Ӧ��Ϣ���򷵻�0
     */
    public long getOffsetInQueueByTime(final String topic, final int queueId, final long timestamp);


    /**
     * ͨ���������Offset����ѯ��Ϣ�� ������������򷵻�null
     */
    public MetaMessageWrapper lookMessageByOffset(final long phyOffset);


    /**
     * ��ȡ����ʱͳ������
     */
    public String getRunningDataInfo();


    /**
     * ��ȡ����������offset
     */
    public long getMaxPhyOffset();


    /**
     * ��ȡ�������������Ϣʱ��
     */
    public long getEarliestMessageTime(final String topic, final int queueId);


    /**
     * ��ȡ�����е���Ϣ����
     */
    public long getMessageTotalInQueue(final String topic, final int queueId);


    /**
     * ���ݸ���ʹ�ã���ȡ�����������
     */
    public SelectMapedBufferResult getPhyQueueData(final long offset);


    /**
     * ���ݸ���ʹ�ã����������׷�����ݣ����ַ��������߼�����
     */
    public boolean appendToPhyQueue(final long startOffset, final byte[] data);


    /**
     * �ֶ�����ɾ���ļ�
     */
    public void excuteDeleteFilesManualy();
}
