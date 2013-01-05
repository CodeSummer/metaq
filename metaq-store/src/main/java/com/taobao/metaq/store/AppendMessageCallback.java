/**
 * $Id: AppendMessageCallback.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.nio.ByteBuffer;

/**
 * д����Ϣ�ص�
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public interface AppendMessageCallback {

    /**
     * ���л���Ϣ��д��MapedByteBuffer
     * 
     * @param byteBuffer
     *            Ҫд���target
     * @param maxBlank
     *            Ҫд���target���հ���
     * @param msg
     *            Ҫд���message
     * @return д������ֽ�
     */
    public AppendMessageResult doAppend(final long fileFromOffset, final ByteBuffer byteBuffer,
            final int maxBlank, final Object msg);
}
