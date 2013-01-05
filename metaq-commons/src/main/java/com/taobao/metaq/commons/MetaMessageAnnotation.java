/**
 * $Id: MetaMessageAnnotation.java 2 2013-01-05 08:09:27Z shijia $
 */
package com.taobao.metaq.commons;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;


/**
 * ��Ϣ�ĸ������ԣ��ڷ����������˶���<br>
 * Producer ----> Broker ----> Consumer<br>
 */
public class MetaMessageAnnotation {
    // ����ID
    private int queueId;
    // �洢��¼��С
    private int storeSize;
    // ����ƫ����
    private long queueOffset;
    // ��Ϣ��־λ����Meta�������û��������ã�
    private int sysFlag;
    // ��Ϣ�ڿͻ��˴���ʱ���
    private long bornTimestamp;
    // ��Ϣ��������
    private SocketAddress bornHost;
    // ��Ϣ�ڷ������洢ʱ���
    private long storeTimestamp;
    // ��Ϣ�洢���ĸ�������
    private SocketAddress storeHost;
    // ��ϢID
    private String msgId;
    // ��Ϣ��Ӧ������Offset
    private long physicOffset;
    // ��Ϣ��CRC
    private int bodyCRC;
    // ��Ϣ����ID
    private long requestId;


    public MetaMessageAnnotation() {
    }


    public MetaMessageAnnotation(int queueId, long bornTimestamp, SocketAddress bornHost, long storeTimestamp,
            SocketAddress storeHost, String msgId) {
        this.queueId = queueId;
        this.bornTimestamp = bornTimestamp;
        this.bornHost = bornHost;
        this.storeTimestamp = storeTimestamp;
        this.storeHost = storeHost;
        this.msgId = msgId;
    }


    /**
     * SocketAddress ----> ByteBuffer ת����8���ֽ�
     */
    public static ByteBuffer SocketAddress2ByteBuffer(SocketAddress socketAddress) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        byteBuffer.put(inetSocketAddress.getAddress().getAddress());
        byteBuffer.putInt(inetSocketAddress.getPort());
        byteBuffer.flip();
        return byteBuffer;
    }


    /**
     * ��ȡbornHost�ֽ���ʽ��8���ֽ� HOST + PORT
     */
    public ByteBuffer getBornHostBytes() {
        return SocketAddress2ByteBuffer(this.bornHost);
    }


    /**
     * ��ȡstorehost�ֽ���ʽ��8���ֽ� HOST + PORT
     */
    public ByteBuffer getStoreHostBytes() {
        return SocketAddress2ByteBuffer(this.storeHost);
    }


    public int getQueueId() {
        return queueId;
    }


    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }


    public long getBornTimestamp() {
        return bornTimestamp;
    }


    public void setBornTimestamp(long bornTimestamp) {
        this.bornTimestamp = bornTimestamp;
    }


    public SocketAddress getBornHost() {
        return bornHost;
    }


    public String getBornHostString() {
        if (this.bornHost != null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) this.bornHost;
            return inetSocketAddress.getAddress().getHostAddress();
        }

        return null;
    }


    public void setBornHost(SocketAddress bornHost) {
        this.bornHost = bornHost;
    }


    public long getStoreTimestamp() {
        return storeTimestamp;
    }


    public void setStoreTimestamp(long storeTimestamp) {
        this.storeTimestamp = storeTimestamp;
    }


    public SocketAddress getStoreHost() {
        return storeHost;
    }


    public void setStoreHost(SocketAddress storeHost) {
        this.storeHost = storeHost;
    }


    public String getMsgId() {
        return msgId;
    }


    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    public int getSysFlag() {
        return sysFlag;
    }


    public void setSysFlag(int sysFlag) {
        this.sysFlag = sysFlag;
    }


    public int getBodyCRC() {
        return bodyCRC;
    }


    public void setBodyCRC(int bodyCRC) {
        this.bodyCRC = bodyCRC;
    }


    public long getQueueOffset() {
        return queueOffset;
    }


    public void setQueueOffset(long queueOffset) {
        this.queueOffset = queueOffset;
    }


    public long getPhysicOffset() {
        return physicOffset;
    }


    public void setPhysicOffset(long physicOffset) {
        this.physicOffset = physicOffset;
    }


    public long getRequestId() {
        return requestId;
    }


    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }


    public int getStoreSize() {
        return storeSize;
    }


    public void setStoreSize(int storeSize) {
        this.storeSize = storeSize;
    }
}
