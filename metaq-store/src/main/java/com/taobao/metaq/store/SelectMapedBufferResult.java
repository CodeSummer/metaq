/**
 * $Id: SelectMapedBufferResult.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.nio.ByteBuffer;


/**
 * ��ѯMapedFile������һ���ڴ�����
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class SelectMapedBufferResult {
    // �Ӷ������ĸ�����Offset��ʼ
    private final long startOffset;
    // position��0��ʼ
    private final ByteBuffer byteBuffer;
    // ��Ч���ݴ�С
    private int size;
    // �����ͷ��ڴ�
    private MapedFile mapedFile;


    public SelectMapedBufferResult(long startOffset, ByteBuffer byteBuffer, int size, MapedFile mapedFile) {
        this.startOffset = startOffset;
        this.byteBuffer = byteBuffer;
        this.size = size;
        this.mapedFile = mapedFile;
    }


    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }


    public int getSize() {
        return size;
    }


    public MapedFile getMapedFile() {
        return mapedFile;
    }


    /**
     * �˷���ֻ�ܱ�����һ�Σ��ظ�������Ч
     */
    public synchronized void release() {
        if (this.mapedFile != null) {
            this.mapedFile.release();
            this.mapedFile = null;
        }
    }


    @Override
    protected void finalize() {
        if (this.mapedFile != null) {
            this.release();
        }
    }


    public long getStartOffset() {
        return startOffset;
    }
}
