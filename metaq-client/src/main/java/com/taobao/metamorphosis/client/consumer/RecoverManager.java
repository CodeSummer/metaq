package com.taobao.metamorphosis.client.consumer;

import java.io.IOException;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.client.Shutdownable;


/**
 * ���Ѷ˵�Recover������
 * 
 * @author �޻�
 * @since 2011-10-31 ����3:40:04
 */

public interface RecoverManager extends Shutdownable {
    /**
     * �Ƿ��Ѿ�����
     * 
     * @return
     */
    public boolean isStarted();


    /**
     * ����recover
     * 
     * @param metaClientConfig
     */
    public void start(MetaClientConfig metaClientConfig);


    /**
     * ����һ����Ϣ
     * 
     * @param group
     * @param message
     * @throws IOException
     */
    public void append(String group, Message message) throws IOException;
}
