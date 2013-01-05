package com.taobao.metamorphosis.client.producer;

import javax.transaction.xa.XAResource;

import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ֧��XA�������Ϣ������
 * 
 * @author boyan
 * 
 */
public interface XAMessageProducer extends MessageProducer {
    /**
     * ����һ��XAResource���������������ʹ�øö���������XAMessageProducer���뵽һ���ֲ�ʽ�����С�
     * 
     * @return
     */
    public XAResource getXAResource() throws MetaClientException;
}
