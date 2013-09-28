package com.taobao.metamorphosis.client.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.gecko.service.exception.NotifyRemotingException;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.MessageAccessor;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.exception.InvalidMessageException;
import com.taobao.metamorphosis.exception.MetaClientException;
import com.taobao.metamorphosis.utils.MetaStatLog;
import com.taobao.metamorphosis.utils.StatConstants;


/**
 * ��Ϣץȡ��������ʵ��
 * 
 * @author boyan(boyan@taobao.com)
 * @date 2011-9-13
 * 
 */
public class SimpleFetchManager implements FetchManager {

    private volatile boolean shutdown = false;

    private Thread[] fetchRunners;

    private int fetchRequestCount;

    private FetchRequestQueue requestQueue;

    private final ConsumerConfig consumerConfig;

    private final InnerConsumer consumer;


    public SimpleFetchManager(final ConsumerConfig consumerConfig, final InnerConsumer consumer) {
        super();
        this.consumerConfig = consumerConfig;
        this.consumer = consumer;
    }


    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }


    @Override
    public void stopFetchRunner() throws InterruptedException {
        this.shutdown = true;
        // �ж���������
        if (this.fetchRunners != null) {
            for (final Thread thread : this.fetchRunners) {
                if (thread != null) {
                    thread.interrupt();
                    try {
                        thread.join(5000);
                    }
                    catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }
        }
        // �ȴ������������
        if (this.requestQueue != null) {
            this.requestQueue.shutdown();

            while (this.requestQueue.size() != this.fetchRequestCount) {
                Thread.sleep(50);
            }
        }

    }


    @Override
    public void resetFetchState() {
        this.requestQueue = new FetchRequestQueue();
        this.fetchRunners = new Thread[this.consumerConfig.getFetchRunnerCount()];
        for (int i = 0; i < this.fetchRunners.length; i++) {
            this.fetchRunners[i] = new Thread(new FetchRequestRunner());
            this.fetchRunners[i].setName(this.consumerConfig.getGroup() + "Fetch-Runner-" + i);
        }

    }


    @Override
    public void startFetchRunner() {
        // ����������Ŀ����ֹͣ��ʱ��Ҫ���
        this.fetchRequestCount = this.requestQueue.size();
        this.shutdown = false;
        for (final Thread thread : this.fetchRunners) {
            thread.start();
        }

    }


    @Override
    public void addFetchRequest(final FetchRequest request) {
        this.requestQueue.offer(request);

    }


    FetchRequest takeFetchRequest() throws InterruptedException {
        return this.requestQueue.take();
    }

    static final Log log = LogFactory.getLog(SimpleFetchManager.class);

    class FetchRequestRunner implements Runnable {

        private static final int DELAY_NPARTS = 10;


        @Override
        public void run() {
            while (!SimpleFetchManager.this.shutdown) {
                try {
                    final FetchRequest request = SimpleFetchManager.this.requestQueue.take();
                    if (request != null) {
                        this.executeRequest(request);
                    }
                }
                catch (final InterruptedException e) {
                    // take��Ӧ�жϣ�����
                }

            }
        }


        void executeRequest(final FetchRequest request) {
            try {
                final FetchResult fetchResult = SimpleFetchManager.this.consumer.fetchAll(request, -1, null);
                if (fetchResult != null) {
                    // 2.0
                    if (fetchResult.isNewMetaServer()) {
                        List<Message> msgList = fetchResult.getMessageList();
                        final ListIterator<Message> iterator = msgList.listIterator();
                        final MessageListener listener =
                                SimpleFetchManager.this.consumer.getMessageListener(request.getTopic());
                        this.notifyListener20(request, iterator, listener);
                    }
                    // 1.4
                    else {
                        final MessageIterator iterator = fetchResult.getMessageIterator();
                        final MessageListener listener =
                                SimpleFetchManager.this.consumer.getMessageListener(request.getTopic());
                        this.notifyListener(request, iterator, listener);
                    }
                }
                else {
                    this.updateDelay(request);
                    SimpleFetchManager.this.addFetchRequest(request);
                }
            }
            catch (final MetaClientException e) {
                this.updateDelay(request);
                this.LogAddRequest(request, e);
            }
            catch (final InterruptedException e) {
                // ��Ȼ��Ҫ������У�������ֹͣ�ź�
                SimpleFetchManager.this.addFetchRequest(request);
            }
            catch (final Throwable e) {
                this.updateDelay(request);
                this.LogAddRequest(request, e);
            }
        }

        private long lastLogNoConnectionTime;


        private void LogAddRequest(final FetchRequest request, final Throwable e) {
            if (e instanceof MetaClientException && e.getCause() instanceof NotifyRemotingException
                    && e.getMessage().contains("�޿�������")) {
                // ���1���Ӵ�ӡһ��
                final long now = System.currentTimeMillis();
                if (this.lastLogNoConnectionTime <= 0 || (now - this.lastLogNoConnectionTime) > 60000) {
                    log.error("��ȡ��Ϣʧ��,topic=" + request.getTopic() + ",partition=" + request.getPartition(), e);
                    this.lastLogNoConnectionTime = now;
                }
            }
            else {
                log.error("��ȡ��Ϣʧ��,topic=" + request.getTopic() + ",partition=" + request.getPartition(), e);
            }
            SimpleFetchManager.this.addFetchRequest(request);
        }


        private void getOffsetAddRequest(final FetchRequest request, final InvalidMessageException e) {
            try {
                final long newOffset = SimpleFetchManager.this.consumer.offset(request);
                request.resetRetries();
                request.setOffset(newOffset, request.getLastMessageId(), request.getPartitionObject().isAutoAck());
            }
            catch (final MetaClientException ex) {
                log.error("��ѯoffsetʧ��,topic=" + request.getTopic() + ",partition=" + request.getPartition(), e);
            }
            finally {
                SimpleFetchManager.this.addFetchRequest(request);
            }
        }


        private void notifyListener(final FetchRequest request, final MessageIterator it,
                final MessageListener listener) {
            if (listener != null) {
                if (listener.getExecutor() != null) {
                    try {
                        listener.getExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                FetchRequestRunner.this.receiveMessages(request, it, listener);
                            }
                        });
                    }
                    catch (final RejectedExecutionException e) {
                        log.error("MessageListener�̳߳ط�æ���޷�������Ϣ,topic=" + request.getTopic() + ",partition="
                                + request.getPartition(), e);
                        SimpleFetchManager.this.addFetchRequest(request);
                    }

                }
                else {
                    this.receiveMessages(request, it, listener);
                }
            }
        }


        private void notifyListener20(final FetchRequest request, final ListIterator<Message> it,
                final MessageListener listener) {
            if (listener != null) {
                if (listener.getExecutor() != null) {
                    try {
                        listener.getExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                FetchRequestRunner.this.receiveMessages20(request, it, listener);
                            }
                        });
                    }
                    catch (final RejectedExecutionException e) {
                        log.error("MessageListener�̳߳ط�æ���޷�������Ϣ,topic=" + request.getTopic() + ",partition="
                                + request.getPartition(), e);
                        SimpleFetchManager.this.addFetchRequest(request);
                    }

                }
                else {
                    this.receiveMessages20(request, it, listener);
                }
            }
        }


        /**
         * ������Ϣ���������̣�<br>
         * <ul>
         * <li>1.�ж��Ƿ�����Ϣ���Դ������û����Ϣ���������ݵ������Դ��������ж��Ƿ���Ҫ����maxSize</li>
         * <li>2.�ж���Ϣ�Ƿ����Զ�Σ���������趨����������������Ϣ���������ߡ���������Ϣ�����ڱ������Ի��߽���notify��Ͷ</li>
         * <li>3.������Ϣ�������̣������Ƿ��Զ�ack��������д���:
         * <ul>
         * <li>(1)�����Ϣ���Զ�ack��������ѷ����쳣�����޸�offset���ӳ����ѵȴ�����</li>
         * <li>(2)�����Ϣ���Զ�ack�������������������offset</li>
         * <li>(3)�����Ϣ���Զ�ack���������������ack����offset�޸�Ϊtmp offset��������tmp offset</li>
         * <li>(4)�����Ϣ���Զ�ack���������������rollback��������offset������tmp offset</li>
         * <li>(5)�����Ϣ���Զ�ack���������������ackҲ��rollback��������offset������tmp offset</li>
         * </ul>
         * </li>
         * </ul>
         * 
         * @param request
         * @param it
         * @param listener
         */
        private void receiveMessages(final FetchRequest request, final MessageIterator it,
                final MessageListener listener) {
            if (it != null && it.hasNext()) {
                if (this.processWhenRetryTooMany(request, it)) {
                    return;
                }
                final Partition partition = request.getPartitionObject();
                this.processReceiveMessage(request, it, listener, partition);
            }
            else {

                // ���Զ���޷���������ȡ�����ݣ�������Ҫ����maxSize
                if (SimpleFetchManager.this.isRetryTooManyForIncrease(request) && it != null
                        && it.getDataLength() > 0) {
                    request.increaseMaxSize();
                    log.warn("���棬��" + request.getRetries() + "���޷���ȡtopic=" + request.getTopic() + ",partition="
                            + request.getPartition() + "����Ϣ������maxSize=" + request.getMaxSize() + " Bytes");
                }

                // һ��Ҫ�ж�it�Ƿ�Ϊnull,����������������βʱ(����null)Ҳ������Retries����,�ᵼ���Ժ���������Ϣʱ����recover
                if (it != null) {
                    request.incrementRetriesAndGet();
                }

                this.updateDelay(request);
                SimpleFetchManager.this.addFetchRequest(request);
            }
        }


        private void receiveMessages20(final FetchRequest request, final ListIterator<Message> it,
                final MessageListener listener) {
            if (it != null && it.hasNext()) {
                if (this.processWhenRetryTooMany20(request, it)) {
                    return;
                }
                final Partition partition = request.getPartitionObject();

                if (listener instanceof MessageListListener) {
                    this.processReceiveMessageList20(request, it, (MessageListListener) listener, partition);
                }
                else {
                    this.processReceiveMessage20(request, it, listener, partition);
                }
            }
            else {

                // һ��Ҫ�ж�it�Ƿ�Ϊnull,����������������βʱ(����null)Ҳ������Retries����,�ᵼ���Ժ���������Ϣʱ����recover
                if (it != null) {
                    request.incrementRetriesAndGet();
                }

                this.updateDelay(request);
                SimpleFetchManager.this.addFetchRequest(request);
            }
        }


        /**
         * �����Ƿ���Ҫ���������Ĵ���
         * 
         * @param request
         * @param it
         * @param listener
         * @param partition
         * @return
         */
        private void processReceiveMessage(final FetchRequest request, final MessageIterator it,
                final MessageListener listener, final Partition partition) {
            int count = 0;
            while (it.hasNext()) {
                final int prevOffset = it.getOffset();
                partition.setOffset(request.getOffset());
                try {
                    final Message msg = it.next();

                    MessageAccessor.setPartition(msg, partition);
                    listener.recieveMessages(msg);

                    long newOffset = request.getOffset() + it.getOffset() - prevOffset;

                    if (partition.isAutoAck()) {
                        request.setOffset(newOffset, msg.getId(), true);
                        count++;
                    }
                    else {
                        // �������ύ����Offset�洢��ZK
                        if (partition.isAcked()) {
                            partition.reset();

                            // �����Խ�����Ϣ���
                            if (request.getTmpOffset() > 0) {
                                newOffset = newOffset + (request.getTmpOffset() - request.getOffset());
                            }

                            request.setOffset(newOffset, msg.getId(), true);
                            count++;
                        }
                        // �����߻ع������������ʼʱǰ����Offset
                        else if (partition.isRollback()) {
                            partition.reset();
                            request.rollbackOffset();
                            break;
                        }
                        // �����ύҲ���ǻع�����Offset�洢���ڴ���ʱ����
                        else {
                            request.setOffset(newOffset, msg.getId(), false);
                            count++;
                        }
                    }
                }
                catch (final InvalidMessageException e) {
                    MetaStatLog.addStat(null, StatConstants.INVALID_MSG_STAT, request.getTopic());
                    // ��Ϣ��Ƿ�����ȡ��Чoffset�����·����ѯ
                    this.getOffsetAddRequest(request, e);
                    return;
                }
                catch (final Throwable e) {
                    // ��ָ���Ƶ���һ����Ϣ
                    it.setOffset(prevOffset);
                    log.error(
                        "MessageListener������Ϣ�쳣,topic=" + request.getTopic() + ",partition="
                                + request.getPartition(), e);
                    // ����ѭ����������Ϣ�쳣������Ϊֹ
                    break;
                }
            }

            // ���offset��Ȼû��ǰ�����������Դ���
            if (it.getOffset() == 0) {
                request.incrementRetriesAndGet();
            }
            else {
                request.resetRetries();
            }

            this.addRequst(request);

            MetaStatLog.addStatValue2(null, StatConstants.GET_MSG_COUNT_STAT, request.getTopic(), count);
        }


        private void processReceiveMessage20(final FetchRequest request, final ListIterator<Message> it,
                final MessageListener listener, final Partition partition) {
            int count = 0;
            while (it.hasNext()) {
                partition.setOffset(request.getOffset());
                try {
                    final Message msg = it.next();

                    MessageAccessor.setPartition(msg, partition);
                    listener.recieveMessages(msg);

                    if (partition.isAutoAck()) {
                        request.setOffset(msg.getOffset() + 1, msg.getId(), true);
                        count++;
                    }
                    else {
                        // �������ύ����Offset�洢��ZK
                        if (partition.isAcked()) {
                            partition.reset();
                            request.setOffset(msg.getOffset() + 1, msg.getId(), true);
                            count++;
                        }
                        // �����߻ع������������ʼʱǰ����Offset
                        else if (partition.isRollback()) {
                            partition.reset();
                            request.rollbackOffset();
                            break;
                        }
                        // �����ύҲ���ǻع�����Offset�洢���ڴ���ʱ����
                        else {
                            request.setOffset(msg.getOffset() + 1, msg.getId(), false);
                            count++;
                        }
                    }
                }
                catch (final Throwable e) {
                    // ��ָ���Ƶ���һ����Ϣ
                    it.previous();
                    log.error(
                        "MessageListener������Ϣ�쳣,topic=" + request.getTopic() + ",partition="
                                + request.getPartition(), e);
                    // ����ѭ����������Ϣ�쳣������Ϊֹ
                    break;
                }
            }

            // ���offset��Ȼû��ǰ�����������Դ���
            if (count == 0) {
                request.incrementRetriesAndGet();
            }
            else {
                request.resetRetries();
            }

            this.addRequst(request);

            MetaStatLog.addStatValue2(null, StatConstants.GET_MSG_COUNT_STAT, request.getTopic(), count);
        }


        private void processReceiveMessageList20(final FetchRequest request, final ListIterator<Message> it,
                final MessageListListener listener, final Partition partition) {
            int count = 0;

            List<Message> msgs = new ArrayList<Message>();

            while (it.hasNext()) {
                try {
                    final Message msg = it.next();
                    if (msg != null) {
                        partition.setOffset(request.getOffset());
                        MessageAccessor.setPartition(msg, partition);
                        msgs.add(msg);
                    }
                }
                catch (final Throwable e) {
                    log.error(
                        "MessageListListener������Ϣ�쳣,topic=" + request.getTopic() + ",partition="
                                + request.getPartition(), e);
                    // ����ѭ����������Ϣ�쳣������Ϊֹ
                    break;
                }
            }

            if (!msgs.isEmpty()) {
                Message lastMessage = msgs.get(msgs.size() - 1);
                try {
                    listener.recieveMessageList(msgs);

                    if (partition.isAutoAck()) {
                        request.setOffset(lastMessage.getOffset() + 1, lastMessage.getId(), true);
                        count++;
                    }
                    else {
                        // �������ύ����Offset�洢��ZK
                        if (partition.isAcked()) {
                            partition.reset();
                            request.setOffset(lastMessage.getOffset() + 1, lastMessage.getId(), true);
                            count++;
                        }
                        // �����߻ع������������ʼʱǰ����Offset
                        else if (partition.isRollback()) {
                            partition.reset();
                            request.rollbackOffset();
                        }
                        // �����ύҲ���ǻع�����Offset�洢���ڴ���ʱ����
                        else {
                            request.setOffset(lastMessage.getOffset() + 1, lastMessage.getId(), false);
                            count++;
                        }
                    }
                }
                catch (Throwable e) {
                    log.error("recieveMessageList throw exception.", e);
                }
            }

            // ���offset��Ȼû��ǰ�����������Դ���
            if (count == 0) {
                request.incrementRetriesAndGet();
            }
            else {
                request.resetRetries();
            }

            this.addRequst(request);

            MetaStatLog.addStatValue2(null, StatConstants.GET_MSG_COUNT_STAT, request.getTopic(), count);
        }


        private boolean processWhenRetryTooMany(final FetchRequest request, final MessageIterator it) {
            if (SimpleFetchManager.this.isRetryTooMany(request)) {
                try {
                    final Message couldNotProecssMsg = it.next();
                    MessageAccessor.setPartition(couldNotProecssMsg, request.getPartitionObject());
                    MetaStatLog.addStat(null, StatConstants.SKIP_MSG_COUNT, couldNotProecssMsg.getTopic());
                    SimpleFetchManager.this.consumer.appendCouldNotProcessMessage(couldNotProecssMsg);
                }
                catch (final InvalidMessageException e) {
                    MetaStatLog.addStat(null, StatConstants.INVALID_MSG_STAT, request.getTopic());
                    // ��Ϣ��Ƿ�����ȡ��Чoffset�����·����ѯ
                    this.getOffsetAddRequest(request, e);
                    return true;
                }
                catch (final Throwable t) {
                    this.LogAddRequest(request, t);
                    return true;
                }

                request.resetRetries();
                // �����������ܴ������Ϣ
                request.setOffset(request.getOffset() + it.getOffset(), it.getPrevMessage().getId(), true);
                // ǿ�������ӳ�Ϊ0
                request.setDelay(0);
                SimpleFetchManager.this.addFetchRequest(request);
                return true;
            }
            else {
                return false;
            }
        }


        private boolean processWhenRetryTooMany20(final FetchRequest request, final MessageIterator it) {
            if (SimpleFetchManager.this.isRetryTooMany(request)) {
                try {
                    final Message couldNotProecssMsg = it.next();
                    MessageAccessor.setPartition(couldNotProecssMsg, request.getPartitionObject());
                    MetaStatLog.addStat(null, StatConstants.SKIP_MSG_COUNT, couldNotProecssMsg.getTopic());
                    SimpleFetchManager.this.consumer.appendCouldNotProcessMessage(couldNotProecssMsg);
                }
                catch (final InvalidMessageException e) {
                    MetaStatLog.addStat(null, StatConstants.INVALID_MSG_STAT, request.getTopic());
                    // ��Ϣ��Ƿ�����ȡ��Чoffset�����·����ѯ
                    this.getOffsetAddRequest(request, e);
                    return true;
                }
                catch (final Throwable t) {
                    this.LogAddRequest(request, t);
                    return true;
                }

                request.resetRetries();
                // �����������ܴ������Ϣ
                request.setOffset(request.getOffset() + it.getOffset(), it.getPrevMessage().getId(), true);
                // ǿ�������ӳ�Ϊ0
                request.setDelay(0);
                SimpleFetchManager.this.addFetchRequest(request);
                return true;
            }
            else {
                return false;
            }
        }


        private boolean processWhenRetryTooMany20(final FetchRequest request, final ListIterator<Message> it) {
            if (SimpleFetchManager.this.isRetryTooMany(request)) {
                long id = 100;
                try {
                    final Message couldNotProecssMsg = it.next();
                    id = couldNotProecssMsg.getId();
                    MessageAccessor.setPartition(couldNotProecssMsg, request.getPartitionObject());
                    MetaStatLog.addStat(null, StatConstants.SKIP_MSG_COUNT, couldNotProecssMsg.getTopic());
                    SimpleFetchManager.this.consumer.appendCouldNotProcessMessage(couldNotProecssMsg);
                }
                catch (final Throwable t) {
                    this.LogAddRequest(request, t);
                    return true;
                }

                request.resetRetries();
                // �����������ܴ������Ϣ
                request.setOffset(request.getOffset() + 1, id, true);
                // ǿ�������ӳ�Ϊ0
                request.setDelay(0);
                SimpleFetchManager.this.addFetchRequest(request);
                return true;
            }
            else {
                return false;
            }
        }


        private void ackRequest(final FetchRequest request, final MessageIterator it, final boolean ack) {
            request.setOffset(request.getOffset() + it.getOffset(), it.getPrevMessage() != null ? it
                .getPrevMessage().getId() : -1, ack);
            this.addRequst(request);
        }


        private void addRequst(final FetchRequest request) {
            final long delay = this.getRetryDelay(request);
            request.setDelay(delay);
            SimpleFetchManager.this.addFetchRequest(request);
        }


        private long getRetryDelay(final FetchRequest request) {
            final long maxDelayFetchTimeInMills = SimpleFetchManager.this.getMaxDelayFetchTimeInMills();
            final long nPartsDelayTime = maxDelayFetchTimeInMills / DELAY_NPARTS;
            // �ӳ�ʱ��Ϊ������ӳ�ʱ��/10*���Դ���
            long delay = nPartsDelayTime * request.getRetries();
            if (delay > maxDelayFetchTimeInMills) {
                delay = maxDelayFetchTimeInMills;
            }
            return delay;
        }


        // ��ʱ��ѯ
        private void updateDelay(final FetchRequest request) {
            final long delay = this.getNextDelay(request);
            request.setDelay(delay);
        }


        private long getNextDelay(final FetchRequest request) {
            final long maxDelayFetchTimeInMills = SimpleFetchManager.this.getMaxDelayFetchTimeInMills();
            // ÿ��1/10����,���MaxDelayFetchTimeInMills
            final long nPartsDelayTime = maxDelayFetchTimeInMills / DELAY_NPARTS;
            long delay = request.getDelay() + nPartsDelayTime;
            if (delay > maxDelayFetchTimeInMills) {
                delay = maxDelayFetchTimeInMills;
            }
            return delay;
        }

    }


    boolean isRetryTooMany(final FetchRequest request) {
        return request.getRetries() > this.consumerConfig.getMaxFetchRetries();
    }


    boolean isRetryTooManyForIncrease(final FetchRequest request) {
        return request.getRetries() > this.consumerConfig.getMaxIncreaseFetchDataRetries();
    }


    long getMaxDelayFetchTimeInMills() {
        return this.consumerConfig.getMaxDelayFetchTimeInMills();
    }

}
