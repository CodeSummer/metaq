/**
 * $Id: MetaQueueLogistic.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * �߼����ж��Ƕ������ұ�����16��������<br>
 * �߼������ɺ�̨�̴߳���ˢ��<br>
 * �洢��Ԫ=Offset(8Byte)+Size(4Byte)+MessageType(4Byte)
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class MetaQueueLogistic {
    private static final Logger log = Logger.getLogger(MetaStore.MetaStoreLogName);
    // �洢��Ԫ��С
    public static final int StoreUnitSize = 16;
    // �洢�������
    private final DefaultMetaStore defaultMetaStore;
    // �洢��Ϣ�����Ķ���
    private final MapedFileQueue mapedFileQueue;
    // Topic
    private final String topic;
    // queueId
    private final int queueId;
    // ���һ����Ϣ��Ӧ������Offset
    private long maxPhysicOffset = -1;
    // �߼����е���СOffset��ɾ�������ļ�ʱ�������������СOffset
    // ʵ��ʹ����Ҫ���� StoreUnitSize
    private volatile long minLogicOffset = 0;
    // д����ʱ�õ���ByteBuffer
    private final ByteBuffer byteBufferIndex;


    public MetaQueueLogistic(DefaultMetaStore defaultMetaStore, String topic, int queueId) {
        this.defaultMetaStore = defaultMetaStore;
        this.topic = topic;
        this.queueId = queueId;

        String queueDir = defaultMetaStore.getMetaStoreConfig().getStorePathLogics()//
                + File.separator + topic//
                + File.separator + queueId;//

        this.mapedFileQueue =
                new MapedFileQueue(queueDir, defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics(),
                    defaultMetaStore.getAllocateMapedFileService());

        this.byteBufferIndex = ByteBuffer.allocate(StoreUnitSize);
    }


    public boolean load() {
        boolean result = this.mapedFileQueue.load();
        log.info("load logics queue " + this.topic + "-" + this.queueId + " " + (result ? "OK" : "Failed"));
        return result;
    }


    public void recover() {
        final List<MapedFile> mapedFiles = this.mapedFileQueue.getMapedFiles();
        if (!mapedFiles.isEmpty()) {
            // �ӵ����������ļ���ʼ�ָ�
            int index = mapedFiles.size() - 3;
            if (index < 0)
                index = 0;

            int mapedFileSizeLogics = this.defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics();
            MapedFile mapedFile = mapedFiles.get(index);
            ByteBuffer byteBuffer = mapedFile.sliceByteBuffer();
            long processOffset = mapedFile.getFileFromOffset();
            long mapedFileOffset = 0;
            while (true) {
                for (int i = 0; i < mapedFileSizeLogics; i += StoreUnitSize) {
                    long offset = byteBuffer.getLong();
                    int size = byteBuffer.getInt();
                    int type = byteBuffer.getInt();

                    // ˵����ǰ�洢��Ԫ��Ч
                    // TODO �����ж���Ч�Ƿ����
                    if (offset >= 0 && size > 0) {
                        mapedFileOffset = i + StoreUnitSize;
                        this.maxPhysicOffset = offset;
                    }
                    else {
                        log.info("recover current logics file over,  " + mapedFile.getFileName() + " " + offset
                                + " " + size + " " + type);
                        break;
                    }
                }

                // �ߵ��ļ�ĩβ���л�����һ���ļ�
                if (mapedFileOffset == mapedFileSizeLogics) {
                    index++;
                    if (index >= mapedFiles.size()) {
                        // ��ǰ������֧�����ܷ���
                        log.info("recover last logics file over, last maped file " + mapedFile.getFileName());
                        break;
                    }
                    else {
                        mapedFile = mapedFiles.get(index);
                        byteBuffer = mapedFile.sliceByteBuffer();
                        processOffset = mapedFile.getFileFromOffset();
                        mapedFileOffset = 0;
                        log.info("recover next logics file, " + mapedFile.getFileName());
                    }
                }
                else {
                    log.info("recover current logics queue over " + mapedFile.getFileName() + " "
                            + (processOffset + mapedFileOffset));
                    break;
                }
            }

            processOffset += mapedFileOffset;
            this.mapedFileQueue.truncateDirtyFiles(processOffset);
        }
    }


    public long getMaxOffsetInQuque() {
        return this.mapedFileQueue.getMaxOffset() / StoreUnitSize;
    }


    public long getMinOffsetInQuque() {
        return this.minLogicOffset / StoreUnitSize;
    }


    /**
     * ���ֲ��Ҳ�����Ϣ����ʱ����ӽ�timestamp�߼����е�offset
     */
    public long getOffsetInQueueByTime(final long timestamp) {
        MapedFile mapedFile = this.mapedFileQueue.getMapedFileByTime(timestamp);
        if (mapedFile != null) {
            long offset = 0;
            // low:��һ��������Ϣ����ʼλ��
            // minLogicOffset������ֵ���
            // minLogicOffset-mapedFile.getFileFromOffset()λ�ÿ�ʼ������Чֵ
            int low = minLogicOffset>mapedFile.getFileFromOffset() ? 
            		(int)(minLogicOffset-mapedFile.getFileFromOffset()) : 0 ;
            		
            // high:���һ��������Ϣ����ʼλ��
            int high = 0;
            int midOffset = -1, targetOffset = -1, leftOffset = -1, rightOffset = -1;
            long leftIndexValue = -1L, rightIndexValue = -1L;

            // ȡ����mapedFile�������е�ӳ��ռ�(û��ӳ��Ŀռ䲢���᷵��,���᷵���ļ��ն�)
            SelectMapedBufferResult sbr = mapedFile.selectMapedBuffer(0);
            if (null != sbr) {
                ByteBuffer byteBuffer = sbr.getByteBuffer();
                high = byteBuffer.limit() - StoreUnitSize;
                try {
                    while (high >= low) {
                        midOffset = (low + high) / (2 * StoreUnitSize) * StoreUnitSize;
                        byteBuffer.position(midOffset);
                        long phyOffset = byteBuffer.getLong();
                        int size = byteBuffer.getInt();

                        // �Ƚ�ʱ��, �۰�
                        long storeTime =
                                this.defaultMetaStore.getMetaQueuePhysical().pickupStoretimestamp(phyOffset, size);
                        if (storeTime < 0) {
                            // û�д������ļ��ҵ���Ϣ����ʱֱ�ӷ���0
                            return 0;
                        }
                        else if (storeTime == timestamp) {
                            targetOffset = midOffset;
                            break;
                        }
                        else if (storeTime > timestamp) {
                            high = midOffset - StoreUnitSize;
                            rightOffset = midOffset;
                            rightIndexValue = storeTime;
                        }
                        else {
                            low = midOffset + StoreUnitSize;
                            leftOffset = midOffset;
                            leftIndexValue = storeTime;
                        }
                    }

                    if (targetOffset != -1) {
                        // ��ѯ��ʱ����������Ϣ������¼д���ʱ��
                        offset = targetOffset;
                    }
                    else {
                        if (leftIndexValue == -1) {
                            // timestamp ʱ��С�ڸ�MapedFile�е�һ����¼��¼��ʱ��
                            offset = rightOffset;
                        }
                        else if (rightIndexValue == -1) {
                            // timestamp ʱ����ڸ�MapedFile�����һ����¼��¼��ʱ��
                            offset = leftOffset;
                        }
                        else {
                            // ȡ��ӽ�timestamp��offset
                            offset =
                                    Math.abs(timestamp - leftIndexValue) > Math.abs(timestamp - rightIndexValue) ? rightOffset
                                            : leftOffset;
                        }
                    }

                    return (mapedFile.getFileFromOffset() + offset) / StoreUnitSize;
                }
                finally {
                    sbr.release();
                }
            }
        }

        // ӳ���ļ������Ϊ������ʱ����0
        return 0;
    }


    /**
     * ��������Offsetɾ����Ч�߼��ļ�
     */
    public void truncateDirtyLogicFiles(long phyOffet) {
        // �߼�����ÿ���ļ���С
        int logicFileSize = this.defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics();

        // �ȸı��߼����д洢������Offset
        this.maxPhysicOffset = phyOffet - 1;

        while (true) {
            MapedFile mapedFile = this.mapedFileQueue.getLastMapedFile2();
            if (mapedFile != null) {
                ByteBuffer byteBuffer = mapedFile.sliceByteBuffer();
                // �Ƚ�Offset���
                mapedFile.setWrotePostion(0);
                mapedFile.setCommittedPosition(0);

                for (int i = 0; i < logicFileSize; i += StoreUnitSize) {
                    long offset = byteBuffer.getLong();
                    int size = byteBuffer.getInt();
                    byteBuffer.getInt();

                    // �߼��ļ���ʼ��Ԫ
                    if (0 == i) {
                        if (offset >= phyOffet) {
                            this.mapedFileQueue.deleteLastMapedFile();
                            break;
                        }
                        else {
                            int pos = i + StoreUnitSize;
                            mapedFile.setWrotePostion(pos);
                            mapedFile.setCommittedPosition(pos);
                            this.maxPhysicOffset = offset;
                        }
                    }
                    // �߼��ļ��м䵥Ԫ
                    else {
                        // ˵����ǰ�洢��Ԫ��Ч
                        if (offset >= 0 && size > 0) {
                            // ����߼����д洢���������offset��������������offset���򷵻�
                            if (offset >= phyOffet) {
                                return;
                            }

                            int pos = i + StoreUnitSize;
                            mapedFile.setWrotePostion(pos);
                            mapedFile.setCommittedPosition(pos);
                            this.maxPhysicOffset = offset;

                            // ������һ��MapedFileɨ���꣬�򷵻�
                            if (pos == logicFileSize) {
                                return;
                            }
                        }
                        else {
                            return;
                        }
                    }
                }
            }
            else {
                break;
            }
        }
    }


    /**
     * �������һ����Ϣ��Ӧ������е�Next Offset
     */
    public long getLastOffset() {
        // �������Offset
        long lastOffset = -1;
        // �߼�����ÿ���ļ���С
        int logicFileSize = this.defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics();

        MapedFile mapedFile = this.mapedFileQueue.getLastMapedFile2();
        if (mapedFile != null) {
            ByteBuffer byteBuffer = mapedFile.sliceByteBuffer();

            // �Ƚ�Offset���
            mapedFile.setWrotePostion(0);
            mapedFile.setCommittedPosition(0);

            for (int i = 0; i < logicFileSize; i += StoreUnitSize) {
                long offset = byteBuffer.getLong();
                int size = byteBuffer.getInt();
                byteBuffer.getInt();

                // ˵����ǰ�洢��Ԫ��Ч
                if (offset >= 0 && size > 0) {
                    lastOffset = offset + size;
                    int pos = i + StoreUnitSize;
                    mapedFile.setWrotePostion(pos);
                    mapedFile.setCommittedPosition(pos);
                    this.maxPhysicOffset = offset;
                }
                else {
                    break;
                }
            }
        }

        return lastOffset;
    }


    public boolean commit(final int flushLeastPages) {
        return this.mapedFileQueue.commit(flushLeastPages);
    }


    public int deleteExpiredFile(long offset) {
        int cnt = this.mapedFileQueue.deleteExpiredFileByOffset(offset);
        // �����Ƿ�ɾ���ļ�������Ҫ��������Сֵ����Ϊ�п��������ļ�ɾ���ˣ�
        // �����߼��ļ�һ��Ҳɾ������
        this.correctMinOffset(offset);
        return cnt;
    }


    /**
     * �߼����е���СOffsetҪ�ȴ����������СphyMinOffset��
     */
    public void correctMinOffset(long phyMinOffset) {
        MapedFile mapedFile = this.mapedFileQueue.getFirstMapedFileOnLock();
        if (mapedFile != null) {
            SelectMapedBufferResult result = mapedFile.selectMapedBuffer(0);
            if (result != null) {
                try {
                    // ����Ϣ����
                    for (int i = 0; i < result.getSize(); i += MetaQueueLogistic.StoreUnitSize) {
                        long offsetPy = result.getByteBuffer().getLong();
                        result.getByteBuffer().getInt();
                        result.getByteBuffer().getInt();

                        if (offsetPy >= phyMinOffset) {
                            this.minLogicOffset = result.getMapedFile().getFileFromOffset() + i;
                            log.info("compute logics min offset: " + this.getMinOffsetInQuque() + ", topic: "
                                    + this.topic + ", queueId: " + this.queueId);
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    result.release();
                }
            }
        }
    }


    /**
     * �洢һ��16�ֽڵ���Ϣ��putIndexֻ��һ���̵߳��ã����Բ���Ҫ����
     * 
     * @param offset
     *            ��Ϣ��Ӧ���������offset
     * @param size
     *            ��Ϣ����������洢�Ĵ�С
     * @param msgType
     *            ��Ϣ����
     * @return �Ƿ�ɹ�
     */
    public boolean putIndex(final long offset, final int size, final int msgType, final long logicOffset) {
        // �����ݻָ�ʱ���ߵ��������
        if (offset <= this.maxPhysicOffset) {
            return true;
        }

        this.byteBufferIndex.flip();
        this.byteBufferIndex.limit(StoreUnitSize);
        this.byteBufferIndex.putLong(offset);
        this.byteBufferIndex.putInt(size);
        this.byteBufferIndex.putInt(msgType);

        final long realLogicOffset = logicOffset * StoreUnitSize;

        MapedFile mapedFile = this.mapedFileQueue.getLastMapedFile(realLogicOffset);
        if (mapedFile != null) {
            // ����MapedFile�߼���������˳��
            if (mapedFile.isFirstCreateInQueue() && logicOffset != 0 && mapedFile.getWrotePostion() == 0) {
                this.minLogicOffset = realLogicOffset;
                this.fillPreBlank(mapedFile, realLogicOffset);
                log.info("fill pre blank space " + mapedFile.getFileName() + " " + realLogicOffset + " "
                        + mapedFile.getWrotePostion());
            }

            if (realLogicOffset != (mapedFile.getWrotePostion() + mapedFile.getFileFromOffset())) {
                log.warn("logic queue order maybe wrong " + realLogicOffset + " "
                        + (mapedFile.getWrotePostion() + mapedFile.getFileFromOffset()));
            }

            // ��¼����������offset
            this.maxPhysicOffset = offset;
            return mapedFile.appendMessage(this.byteBufferIndex.array());
        }

        return false;
    }


    private void fillPreBlank(final MapedFile mapedFile, final long untilWhere) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(StoreUnitSize);
        byteBuffer.putLong(0);
        byteBuffer.putInt(Integer.MAX_VALUE);
        byteBuffer.putInt(0);

        int until = (int) (untilWhere % this.mapedFileQueue.getMapedFileSize());
        for (int i = 0; i < until; i += StoreUnitSize) {
            mapedFile.appendMessage(byteBuffer.array());
        }
    }


    /**
     * ����Index Buffer
     * 
     * @param startIndex
     *            ��ʼƫ��������
     */
    public SelectMapedBufferResult getIndexBuffer(final long startIndex) {
        int mapedFileSize = this.defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics();
        long offset = startIndex * StoreUnitSize;
        MapedFile mapedFile = this.mapedFileQueue.findMapedFileByOffset(offset);
        if (mapedFile != null) {
            SelectMapedBufferResult result = mapedFile.selectMapedBuffer((int) (offset % mapedFileSize));
            return result;
        }

        return null;
    }


    public long rollNextFile(final long index) {
        int mapedFileSize = this.defaultMetaStore.getMetaStoreConfig().getMapedFileSizeLogics();
        int totalUnitsInFile = mapedFileSize / StoreUnitSize;
        return (index + totalUnitsInFile - index % totalUnitsInFile);
    }


    public String getTopic() {
        return topic;
    }


    public int getQueueId() {
        return queueId;
    }


    public long getMaxPhysicOffset() {
        return maxPhysicOffset;
    }


    public void setMaxPhysicOffset(long maxPhysicOffset) {
        this.maxPhysicOffset = maxPhysicOffset;
    }


    public void destroy() {
        this.maxPhysicOffset = -1;
        this.minLogicOffset = 0;
        this.mapedFileQueue.destroy();
    }


    public long getMinLogicOffset() {
        return minLogicOffset;
    }


    public void setMinLogicOffset(long minLogicOffset) {
        this.minLogicOffset = minLogicOffset;
    }


    /**
     * ��ȡ��ǰ�����е���Ϣ����
     */
    public long getMessageTotalInQueue() {
        return this.getMaxOffsetInQuque() - this.getMinOffsetInQuque();
    }
}
