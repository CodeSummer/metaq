/**
 * $Id: MetaStatsService.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.taobao.metaq.commons.ServiceThread;

/**
 * �洢��ͳ�Ʒ���
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class MetaStatsService extends ServiceThread {
    static class CallSnapshot {
        public final long timestamp;
        public final long callTimesTotal;


        public CallSnapshot(long timestamp, long callTimesTotal) {
            this.timestamp = timestamp;
            this.callTimesTotal = callTimesTotal;
        }


        public static double getTPS(final CallSnapshot begin, final CallSnapshot end) {
            long total = end.callTimesTotal - begin.callTimesTotal;
            Long time = end.timestamp - begin.timestamp;

            double tps = total / time.doubleValue();

            return tps * 1000;
        }
    }

    private static final Logger log = Logger.getLogger(MetaStore.MetaStoreLogName);
    // ����ʱ��
    private long metaStoreBootTimestamp = System.currentTimeMillis();
    // putMessage��д��������Ϣ��ʱ������������ʱ�䣨��λ���룩
    private volatile long putMessageEntireTimeMax = 0;
    // getMessage����ȡһ����Ϣ��ʱ������������ʱ�䣨��λ���룩
    private volatile long getMessageEntireTimeMax = 0;

    // for putMessageEntireTimeMax
    private ReentrantLock lockPut = new ReentrantLock();
    // for getMessageEntireTimeMax
    private ReentrantLock lockGet = new ReentrantLock();

    // putMessage��ʧ�ܴ���
    private final AtomicLong putMessageFailedTimes = new AtomicLong(0);
    // putMessage����������
    private final AtomicLong putMessageTimesTotal = new AtomicLong(0);
    // getMessage����������
    private final AtomicLong getMessageTimesTotalFound = new AtomicLong(0);
    private final AtomicLong getMessageTransferedMsgCount = new AtomicLong(0);
    private final AtomicLong getMessageTimesTotalMiss = new AtomicLong(0);
    // putMessage��Message Size Total
    private final AtomicLong putMessageSizeTotal = new AtomicLong(0);
    // putMessage����ʱ�ֲ�
    private final AtomicLong[] putMessageDistributeTime = new AtomicLong[7];
    // DispatchMessageService�����������ֵ
    private volatile long dispatchMaxBuffer = 0;

    // ����Ƶ�ʣ�1���Ӳ���һ��
    private static final int FrequencyOfSampling = 1000;
    // ��������¼����������֮ǰ��ɾ����
    private static final int MaxRecordsOfSampling = 60 * 10;
    // ��Բ����̼߳���
    private ReentrantLock lockSampling = new ReentrantLock();

    // put���10���Ӳ���
    private final LinkedList<CallSnapshot> putTimesList = new LinkedList<CallSnapshot>();
    // get���10���Ӳ���
    private final LinkedList<CallSnapshot> getTimesFoundList = new LinkedList<CallSnapshot>();
    private final LinkedList<CallSnapshot> getTimesMissList = new LinkedList<CallSnapshot>();
    private final LinkedList<CallSnapshot> transferedMsgCountList = new LinkedList<CallSnapshot>();

    // ��ӡTPS���ݼ��ʱ�䣬��λ�룬1����
    private static int PrintTPSInterval = 60 * 1;
    private long lastPrintTimestamp = System.currentTimeMillis();


    public MetaStatsService() {
        for (int i = 0; i < this.putMessageDistributeTime.length; i++) {
            putMessageDistributeTime[i] = new AtomicLong(0);
        }
    }


    public long getPutMessageEntireTimeMax() {
        return putMessageEntireTimeMax;
    }


    public void setPutMessageEntireTimeMax(long value) {
        // ΢��
        if (value <= 0) {
            this.putMessageDistributeTime[0].incrementAndGet();
        }
        // ������
        else if (value < 10) {
            this.putMessageDistributeTime[1].incrementAndGet();
        }
        // ��ʮ����
        else if (value < 100) {
            this.putMessageDistributeTime[2].incrementAndGet();
        }
        // ���ٺ��루500�������ڣ�
        else if (value < 500) {
            this.putMessageDistributeTime[3].incrementAndGet();
        }
        // ���ٺ��루500�������ϣ�
        else if (value < 1000) {
            this.putMessageDistributeTime[4].incrementAndGet();
        }
        // ����
        else if (value < 10000) {
            this.putMessageDistributeTime[5].incrementAndGet();
        }
        // �����10��
        else {
            this.putMessageDistributeTime[6].incrementAndGet();
        }

        if (value > this.putMessageEntireTimeMax) {
            this.lockPut.lock();
            this.putMessageEntireTimeMax =
                    value > this.putMessageEntireTimeMax ? value : this.putMessageEntireTimeMax;
            this.lockPut.unlock();
        }
    }


    public long getGetMessageEntireTimeMax() {
        return getMessageEntireTimeMax;
    }


    public void setGetMessageEntireTimeMax(long value) {
        if (value > this.getMessageEntireTimeMax) {
            this.lockGet.lock();
            this.getMessageEntireTimeMax =
                    value > this.getMessageEntireTimeMax ? value : this.getMessageEntireTimeMax;
            this.lockGet.unlock();
        }
    }


    public AtomicLong getPutMessageTimesTotal() {
        return putMessageTimesTotal;
    }


    public AtomicLong getPutMessageSizeTotal() {
        return putMessageSizeTotal;
    }


    public long getDispatchMaxBuffer() {
        return dispatchMaxBuffer;
    }


    public void setDispatchMaxBuffer(long value) {
        this.dispatchMaxBuffer = value > this.dispatchMaxBuffer ? value : this.dispatchMaxBuffer;
    }


    private String getPutMessageDistributeTimeStringInfo(Long total) {
        final StringBuilder sb = new StringBuilder(512);

        for (AtomicLong i : this.putMessageDistributeTime) {
            long value = i.get();
            double ratio = value / total.doubleValue();
            sb.append("\r\n\t\t");
            sb.append(value + "(" + (ratio * 100) + "%)");
        }

        return sb.toString();
    }


    //
    // private String getRuntime() {
    // long time = System.currentTimeMillis() - this.metaStoreBootTimestamp;
    // Long result = (time / 1000);
    // return result.toString();
    // }

    private String getFormatRuntime() {
        final long MILLISECOND = 1;
        final long SECOND = 1000 * MILLISECOND;
        final long MINUTE = 60 * SECOND;
        final long HOUR = 60 * MINUTE;
        final long DAY = 24 * HOUR;
        final MessageFormat TIME = new MessageFormat("[ {0} days, {1} hours, {2} minutes, {3} seconds ]");

        long time = System.currentTimeMillis() - this.metaStoreBootTimestamp;
        long days = time / DAY;
        long hours = (time % DAY) / HOUR;
        long minutes = (time % HOUR) / MINUTE;
        long seconds = (time % MINUTE) / SECOND;
        return TIME.format(new Long[] { days, hours, minutes, seconds });
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(1024);
        Long totalTimes = this.putMessageTimesTotal.get();
        if (0 == totalTimes) {
            totalTimes = 1L;
        }

        sb.append("\truntime: " + this.getFormatRuntime() + "\r\n");
        sb.append("\tputMessageEntireTimeMax: " + this.putMessageEntireTimeMax + "\r\n");
        sb.append("\tputMessageTimesTotal: " + totalTimes + "\r\n");
        sb.append("\tputMessageSizeTotal: " + this.putMessageSizeTotal.get() + "\r\n");
        sb.append("\tputMessageDistributeTime: " + this.getPutMessageDistributeTimeStringInfo(totalTimes) + "\r\n");
        sb.append("\tputMessageAverageSize: " + (this.putMessageSizeTotal.get() / totalTimes.doubleValue())
                + "\r\n");
        sb.append("\tdispatchMaxBuffer: " + this.dispatchMaxBuffer + "\r\n");
        sb.append("\tgetMessageEntireTimeMax: " + this.getMessageEntireTimeMax + "\r\n");
        sb.append("\tputTps: " + this.getPutTps() + "\r\n");
        sb.append("\tgetFoundTps: " + this.getGetFoundTps() + "\r\n");
        sb.append("\tgetMissTps: " + this.getGetMissTps() + "\r\n");
        sb.append("\tgetTotalTps: " + this.getGetTotalTps() + "\r\n");
        sb.append("\tgetTransferedTps: " + this.getGetTransferedTps() + "\r\n");
        return sb.toString();
    }


    private void sampling() {
        this.lockSampling.lock();

        this.putTimesList.add(new CallSnapshot(System.currentTimeMillis(), this.putMessageTimesTotal.get()));
        if (this.putTimesList.size() > (MaxRecordsOfSampling + 1)) {
            this.putTimesList.removeFirst();
        }

        this.getTimesFoundList.add(new CallSnapshot(System.currentTimeMillis(), this.getMessageTimesTotalFound
            .get()));
        if (this.getTimesFoundList.size() > (MaxRecordsOfSampling + 1)) {
            this.getTimesFoundList.removeFirst();
        }

        this.getTimesMissList
            .add(new CallSnapshot(System.currentTimeMillis(), this.getMessageTimesTotalMiss.get()));
        if (this.getTimesMissList.size() > (MaxRecordsOfSampling + 1)) {
            this.getTimesMissList.removeFirst();
        }

        this.transferedMsgCountList.add(new CallSnapshot(System.currentTimeMillis(),
            this.getMessageTransferedMsgCount.get()));
        if (this.transferedMsgCountList.size() > (MaxRecordsOfSampling + 1)) {
            this.transferedMsgCountList.removeFirst();
        }

        this.lockSampling.unlock();
    }


    private String getPutTps(int time) {
        String result = "";
        this.lockSampling.lock();
        CallSnapshot last = this.putTimesList.getLast();

        if (this.putTimesList.size() > time) {
            CallSnapshot lastBefore = this.putTimesList.get(this.putTimesList.size() - (time + 1));
            result += CallSnapshot.getTPS(lastBefore, last);
        }

        this.lockSampling.unlock();

        return result;
    }


    private String getPutTps() {
        StringBuilder sb = new StringBuilder();
        // 10����
        sb.append(this.getPutTps(10));
        sb.append(" ");

        // 1����
        sb.append(this.getPutTps(60));
        sb.append(" ");

        // 10����
        sb.append(this.getPutTps(600));

        return sb.toString();
    }


    private String getGetFoundTps(int time) {
        String result = "";
        this.lockSampling.lock();
        CallSnapshot last = this.getTimesFoundList.getLast();

        if (this.getTimesFoundList.size() > time) {
            CallSnapshot lastBefore = this.getTimesFoundList.get(this.getTimesFoundList.size() - (time + 1));
            result += CallSnapshot.getTPS(lastBefore, last);
        }

        this.lockSampling.unlock();

        return result;
    }


    private String getGetFoundTps() {
        StringBuilder sb = new StringBuilder();
        // 10����
        sb.append(this.getGetFoundTps(10));
        sb.append(" ");

        // 1����
        sb.append(this.getGetFoundTps(60));
        sb.append(" ");

        // 10����
        sb.append(this.getGetFoundTps(600));

        return sb.toString();
    }


    private String getGetMissTps(int time) {
        String result = "";
        this.lockSampling.lock();
        CallSnapshot last = this.getTimesMissList.getLast();

        if (this.getTimesMissList.size() > time) {
            CallSnapshot lastBefore = this.getTimesMissList.get(this.getTimesMissList.size() - (time + 1));
            result += CallSnapshot.getTPS(lastBefore, last);
        }

        this.lockSampling.unlock();

        return result;
    }


    private String getGetMissTps() {
        StringBuilder sb = new StringBuilder();
        // 10����
        sb.append(this.getGetMissTps(10));
        sb.append(" ");

        // 1����
        sb.append(this.getGetMissTps(60));
        sb.append(" ");

        // 10����
        sb.append(this.getGetMissTps(600));

        return sb.toString();
    }


    private String getGetTransferedTps(int time) {
        String result = "";
        this.lockSampling.lock();
        CallSnapshot last = this.transferedMsgCountList.getLast();

        if (this.transferedMsgCountList.size() > time) {
            CallSnapshot lastBefore =
                    this.transferedMsgCountList.get(this.transferedMsgCountList.size() - (time + 1));
            result += CallSnapshot.getTPS(lastBefore, last);
        }

        this.lockSampling.unlock();

        return result;
    }


    private String getGetTransferedTps() {
        StringBuilder sb = new StringBuilder();
        // 10����
        sb.append(this.getGetTransferedTps(10));
        sb.append(" ");

        // 1����
        sb.append(this.getGetTransferedTps(60));
        sb.append(" ");

        // 10����
        sb.append(this.getGetTransferedTps(600));

        return sb.toString();
    }


    private String getGetTotalTps(int time) {
        this.lockSampling.lock();
        double found = 0;
        double miss = 0;
        {
            CallSnapshot last = this.getTimesFoundList.getLast();

            if (this.getTimesFoundList.size() > time) {
                CallSnapshot lastBefore = this.getTimesFoundList.get(this.getTimesFoundList.size() - (time + 1));
                found = CallSnapshot.getTPS(lastBefore, last);
            }
        }
        {
            CallSnapshot last = this.getTimesMissList.getLast();

            if (this.getTimesMissList.size() > time) {
                CallSnapshot lastBefore = this.getTimesMissList.get(this.getTimesMissList.size() - (time + 1));
                miss = CallSnapshot.getTPS(lastBefore, last);
            }
        }

        this.lockSampling.unlock();

        return Double.toString(found + miss);
    }


    private String getGetTotalTps() {
        StringBuilder sb = new StringBuilder();
        // 10����
        sb.append(this.getGetTotalTps(10));
        sb.append(" ");

        // 1����
        sb.append(this.getGetTotalTps(60));
        sb.append(" ");

        // 10����
        sb.append(this.getGetTotalTps(600));

        return sb.toString();
    }


    /**
     * 1���Ӵ�ӡһ��TPS
     */
    private void printTps() {
        if (System.currentTimeMillis() > (this.lastPrintTimestamp + PrintTPSInterval * 1000)) {
            this.lastPrintTimestamp = System.currentTimeMillis();

            log.info("put_tps " + this.getPutTps(PrintTPSInterval));

            log.info("get_found_tps " + this.getGetFoundTps(PrintTPSInterval));

            log.info("get_miss_tps " + this.getGetMissTps(PrintTPSInterval));

            log.info("get_transfered_tps " + this.getGetTransferedTps(PrintTPSInterval));
        }
    }


    public void run() {
        log.info(this.getServiceName() + " service started");

        while (!this.isStoped()) {
            try {
                this.waitForRunning(FrequencyOfSampling);

                this.sampling();

                this.printTps();
            }
            catch (Exception e) {
                log.warn(this.getServiceName() + " service has exception. ", e);
            }
        }

        log.info(this.getServiceName() + " service end");
    }


    @Override
    public String getServiceName() {
        return MetaStatsService.class.getSimpleName();
    }


    public AtomicLong getGetMessageTimesTotalFound() {
        return getMessageTimesTotalFound;
    }


    public AtomicLong getGetMessageTimesTotalMiss() {
        return getMessageTimesTotalMiss;
    }


    public AtomicLong getGetMessageTransferedMsgCount() {
        return getMessageTransferedMsgCount;
    }


    public AtomicLong getPutMessageFailedTimes() {
        return putMessageFailedTimes;
    }
}
