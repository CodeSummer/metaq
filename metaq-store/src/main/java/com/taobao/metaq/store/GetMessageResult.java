/**
 * $Id: GetMessageResult.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * ����Ϣ���
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class GetMessageResult {
    // ö�ٱ�����ȡ��Ϣ���
    private GetMessageStatus status;
    // �������˺󣬷�����һ�ο�ʼ��Offset
    private long nextBeginOffset;
    // �߼������е���СOffset
    private long minOffset;
    // �߼������е����Offset
    private long maxOffset;
    // �����������Ϣ����
    private final List<SelectMapedBufferResult> messageMapedList = new ArrayList<SelectMapedBufferResult>(100);
    // ������Consumer������Ϣ
    private final List<ByteBuffer> messageBufferList = new ArrayList<ByteBuffer>(100);
    // ByteBuffer ���ֽ���
    private int bufferTotalSize = 0;


    public GetMessageResult() {
    }


    public GetMessageStatus getStatus() {
        return status;
    }


    public void setStatus(GetMessageStatus status) {
        this.status = status;
    }


    public long getNextBeginOffset() {
        return nextBeginOffset;
    }


    public void setNextBeginOffset(long nextBeginOffset) {
        this.nextBeginOffset = nextBeginOffset;
    }


    public long getMinOffset() {
        return minOffset;
    }


    public void setMinOffset(long minOffset) {
        this.minOffset = minOffset;
    }


    public long getMaxOffset() {
        return maxOffset;
    }


    public void setMaxOffset(long maxOffset) {
        this.maxOffset = maxOffset;
    }


    public List<SelectMapedBufferResult> getMessageMapedList() {
        return messageMapedList;
    }


    public List<ByteBuffer> getMessageBufferList() {
        return messageBufferList;
    }


    public void addMessage(final SelectMapedBufferResult mapedBuffer) {
        this.messageMapedList.add(mapedBuffer);
        this.messageBufferList.add(mapedBuffer.getByteBuffer());
        this.bufferTotalSize += mapedBuffer.getSize();
    }


    public void release() {
        for (SelectMapedBufferResult select : this.messageMapedList) {
            select.release();
        }
    }


    public int getBufferTotalSize() {
        return bufferTotalSize;
    }


    public int getMessageCount() {
        return this.messageMapedList.size();
    }


    public void setBufferTotalSize(int bufferTotalSize) {
        this.bufferTotalSize = bufferTotalSize;
    }
}
