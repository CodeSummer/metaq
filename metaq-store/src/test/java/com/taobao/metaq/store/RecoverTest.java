/**
 * $Id: RecoverTest.java 2 2013-01-05 08:09:27Z shijia $
 */
package com.taobao.metaq.store;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.metaq.commons.MetaMessage;
import com.taobao.metaq.commons.MetaMessageAnnotation;
import com.taobao.metaq.commons.MetaMessageDecoder;
import com.taobao.metaq.commons.MetaMessageWrapper;


public class RecoverTest {
    // ���и���
    private static int QUEUE_TOTAL = 10;
    // �����ĸ�����
    private static AtomicInteger QueueId = new AtomicInteger(0);
    // ����������ַ
    private static SocketAddress BornHost;
    // �洢������ַ
    private static SocketAddress StoreHost;
    // ��Ϣ��
    private static byte[] MessageBody;

    private static final String StoreMessage = "Once, there was a chance for me!aaaaaaaaaaaaaaaaaaaaaaaa";


    public static MetaMessageWrapper buildMessage() {
        MetaMessage msg = new MetaMessage("TOPIC_A", "MSG_TYPE_A", MessageBody);
        msg.setAttribute("");

        MetaMessageAnnotation msgant = new MetaMessageAnnotation();
        msgant.setQueueId(Math.abs(QueueId.getAndIncrement()) % QUEUE_TOTAL);
        msgant.setSysFlag(4);
        msgant.setBornTimestamp(System.currentTimeMillis());
        msgant.setStoreHost(StoreHost);
        msgant.setBornHost(BornHost);

        return new MetaMessageWrapper(msg, msgant);
    }


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        StoreHost = new InetSocketAddress(InetAddress.getLocalHost(), 8123);
        BornHost = new InetSocketAddress(InetAddress.getByName("10.232.102.184"), 0);
        PropertyConfigurator.configure("log4j.properties");
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private MetaStore metaStoreWrite1;
    private MetaStore metaStoreWrite2;
    private MetaStore metaStoreRead;


    private void destroy() {
        if (metaStoreWrite1 != null) {
            // �رմ洢����
            metaStoreWrite1.shutdown();
            // ɾ���ļ�
            metaStoreWrite1.destroy();
        }

        if (metaStoreWrite2 != null) {
            // �رմ洢����
            metaStoreWrite2.shutdown();
            // ɾ���ļ�
            metaStoreWrite2.destroy();
        }

        if (metaStoreRead != null) {
            // �رմ洢����
            metaStoreRead.shutdown();
            // ɾ���ļ�
            metaStoreRead.destroy();
        }
    }


    public void writeMessage(boolean normal, boolean first) throws Exception {
        System.out.println("================================================================");
        long totalMsgs = 1000;
        QUEUE_TOTAL = 3;

        // ������Ϣ��
        MessageBody = StoreMessage.getBytes();

        MetaStoreConfig metaStoreConfig = new MetaStoreConfig();
        // ÿ������ӳ���ļ�
        metaStoreConfig.setMapedFileSizePhysic(1024 * 32);
        // ÿ���߼�ӳ���ļ�
        metaStoreConfig.setMapedFileSizeLogics(1024);

        MetaStore metaStore = new DefaultMetaStore(metaStoreConfig);
        if (first) {
            this.metaStoreWrite1 = metaStore;
        }
        else {
            this.metaStoreWrite2 = metaStore;
        }

        // ��һ����load��������
        boolean loadResult = metaStore.load();
        assertTrue(loadResult);

        // �ڶ�������������
        metaStore.start();

        // ������������Ϣ
        for (long i = 0; i < totalMsgs; i++) {
            MetaMessageWrapper wrapper = buildMessage();
            PutMessageResult result =
                    metaStore.putMessage(wrapper.getMetaMessage(), wrapper.getMetaMessageAnnotation());

            System.out.println(i + "\t" + result.getAppendMessageResult().getMsgId());
        }

        if (normal) {
            // �رմ洢����
            metaStore.shutdown();
        }

        System.out.println("========================writeMessage OK========================================");
    }


    private void veryReadMessage(int queueId, long queueOffset, List<ByteBuffer> byteBuffers) {
        for (ByteBuffer byteBuffer : byteBuffers) {
            MetaMessageWrapper msg = MetaMessageDecoder.decode(byteBuffer);
            System.out.println("request queueId " + queueId + ", request queueOffset " + queueOffset
                    + " msg queue offset " + msg.getMetaMessageAnnotation().getQueueOffset());

            assertTrue(msg.getMetaMessageAnnotation().getQueueOffset() == queueOffset);

            queueOffset++;
        }
    }


    public void readMessage(final long msgCnt) throws Exception {
        System.out.println("================================================================");
        QUEUE_TOTAL = 3;

        // ������Ϣ��
        MessageBody = StoreMessage.getBytes();

        MetaStoreConfig metaStoreConfig = new MetaStoreConfig();
        // ÿ������ӳ���ļ�
        metaStoreConfig.setMapedFileSizePhysic(1024 * 32);
        // ÿ���߼�ӳ���ļ�
        metaStoreConfig.setMapedFileSizeLogics(1024);

        metaStoreRead = new DefaultMetaStore(metaStoreConfig);
        // ��һ����load��������
        boolean loadResult = metaStoreRead.load();
        assertTrue(loadResult);

        // �ڶ�������������
        metaStoreRead.start();

        // ������������Ϣ
        long readCnt = 0;
        for (int queueId = 0; queueId < QUEUE_TOTAL; queueId++) {
            for (long offset = 0;;) {
                GetMessageResult result = metaStoreRead.getMessage("TOPIC_A", queueId, offset, 1024 * 1024, null);
                if (result.getStatus() == GetMessageStatus.FOUND) {
                    System.out.println(queueId + "\t" + result.getMessageCount());
                    this.veryReadMessage(queueId, offset, result.getMessageBufferList());
                    offset += result.getMessageCount();
                    readCnt += result.getMessageCount();
                    result.release();
                }
                else {
                    break;
                }
            }
        }

        System.out.println("readCnt = " + readCnt);
        assertTrue(readCnt == msgCnt);

        System.out.println("========================readMessage OK========================================");
    }


    /**
     * �����رպ������ָ���Ϣ����֤�Ƿ�����Ϣ��ʧ
     */
    @Test
    public void test_recover_normally() throws Exception {
        this.writeMessage(true, true);
        Thread.sleep(1000 * 3);
        this.readMessage(1000);
        this.destroy();
    }


    /**
     * �����رպ������ָ���Ϣ�����ٴ�д����Ϣ����֤�Ƿ�����Ϣ��ʧ
     */
    @Test
    public void test_recover_normally_write() throws Exception {
        this.writeMessage(true, true);
        Thread.sleep(1000 * 3);
        this.writeMessage(true, false);
        Thread.sleep(1000 * 3);
        this.readMessage(2000);
        this.destroy();
    }


    /**
     * �쳣�رպ������ָ���Ϣ����֤�Ƿ�����Ϣ��ʧ
     */
    @Test
    public void test_recover_abnormally() throws Exception {
        this.writeMessage(false, true);
        Thread.sleep(1000 * 3);
        this.readMessage(1000);
        this.destroy();
    }


    /**
     * �쳣�رպ������ָ���Ϣ�����ٴ�д����Ϣ����֤�Ƿ�����Ϣ��ʧ
     */
    @Test
    public void test_recover_abnormally_write() throws Exception {
        this.writeMessage(false, true);
        Thread.sleep(1000 * 3);
        this.writeMessage(false, false);
        Thread.sleep(1000 * 3);
        this.readMessage(2000);
        this.destroy();
    }
}
