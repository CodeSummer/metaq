package com.taobao.metamorphosis.client.producer;

import com.taobao.metamorphosis.cluster.Partition;


/**
 * ��Ϣ���ͽ������
 * 
 * @author boyan
 * @Date 2011-4-27
 * 
 */
public class SendResult {
    private final boolean success;
    private final Partition partition;
    private final String errorMessage;
    private final long offset;


    public SendResult(boolean success, Partition partition, long offset, String errorMessage) {
        super();
        this.success = success;
        this.partition = partition;
        this.offset = offset;
        this.errorMessage = errorMessage;
    }


    /**
     * ����Ϣ���ͳɹ�����Ϣ�ڷ����д���offset���������ʧ�ܣ�����-1
     * 
     * @return
     */
    public long getOffset() {
        return this.offset;
    }


    /**
     * ��Ϣ�Ƿ��ͳɹ�
     * 
     * @return trueΪ�ɹ�
     */
    public boolean isSuccess() {
        return this.success;
    }


    /**
     * ��Ϣ����������ķ���
     * 
     * @return ��Ϣ����������ķ������������ʧ����Ϊnull
     */
    public Partition getPartition() {
        return this.partition;
    }


    /**
     * ��Ϣ���ͽ���ĸ�����Ϣ���������ʧ�ܿ��ܰ���������Ϣ
     * 
     * @return ��Ϣ���ͽ���ĸ�����Ϣ���������ʧ�ܿ��ܰ���������Ϣ
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
