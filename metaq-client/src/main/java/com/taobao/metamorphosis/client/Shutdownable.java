package com.taobao.metamorphosis.client;

import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * �ɹرշ���ӿ�
 * 
 * @author boyan
 * @Date 2011-6-2
 * 
 */
public interface Shutdownable {
    public void shutdown() throws MetaClientException;
}
