package com.taobao.metamorphosis.client.consumer;

import java.util.concurrent.Executor;

import com.taobao.metamorphosis.Message;


/**
 * �첽��Ϣ������
 * 
 * @author boyan
 * @Date 2011-4-23
 * 
 */
public interface MessageListener {
    /**
     * ���յ���Ϣ�б�ֻ��messages��Ϊ�ղ��Ҳ�Ϊnull������»ᴥ���˷���
     * 
     * @param messages
     *            TODO ƴд����Ӧ���ǵ�������ʱ����ʹ��
     */
    public void recieveMessages(Message message);


    /**
     * ������Ϣ���̳߳�
     * 
     * @return
     */
    public Executor getExecutor();
}
