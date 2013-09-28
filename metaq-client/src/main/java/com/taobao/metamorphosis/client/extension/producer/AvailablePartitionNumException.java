package com.taobao.metamorphosis.client.extension.producer;

import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ��ʾĳtopic��ǰ���õķ�����������ȷ,�����������������һ�µ�
 * 
 * @author �޻�
 * @since 2011-8-2 ����02:49:27
 */

public class AvailablePartitionNumException extends MetaClientException {

    private static final long serialVersionUID = 8087499474643513774L;


    public AvailablePartitionNumException() {
        super();
    }


    public AvailablePartitionNumException(String message, Throwable cause) {
        super(message, cause);
    }


    public AvailablePartitionNumException(String message) {
        super(message);
    }


    public AvailablePartitionNumException(Throwable cause) {
        super(cause);
    }

}
