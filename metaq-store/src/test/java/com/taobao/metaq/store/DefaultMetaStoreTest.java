/**
 * $Id: DefaultMetaStoreTest.java 2 2013-01-05 08:09:27Z shijia $
 */
package com.taobao.metaq.store;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.taobao.metaq.commons.MetaMessage;
import com.taobao.metaq.commons.MetaMessageAnnotation;
import com.taobao.metaq.commons.MetaMessageWrapper;


public class DefaultMetaStoreTest {
    // ���и���
    private static int QUEUE_TOTAL = 100;
    // �����ĸ�����
    private static AtomicInteger QueueId = new AtomicInteger(0);
    // ����������ַ
    private static SocketAddress BornHost;
    // �洢������ַ
    private static SocketAddress StoreHost;
    // ��Ϣ��
    private static byte[] MessageBody;

    private static final String StoreMessage = "Once, there was a chance for me!";


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


    @Test
    public void test_write_read() throws Exception {
        System.out.println("================================================================");
        long totalMsgs = 10000;
        QUEUE_TOTAL = 1;

        // ������Ϣ��
        MessageBody = StoreMessage.getBytes();

        MetaStoreConfig metaStoreConfig = new MetaStoreConfig();
        // ÿ������ӳ���ļ� 4K
        metaStoreConfig.setMapedFileSizePhysic(1024 * 8);

        MetaStore metaStoreMaster = new DefaultMetaStore(metaStoreConfig);
        // ��һ����load��������
        boolean load = metaStoreMaster.load();
        assertTrue(load);

        // �ڶ�������������
        metaStoreMaster.start();
        for (long i = 0; i < totalMsgs; i++) {
            MetaMessageWrapper wrapper = buildMessage();
            PutMessageResult result =
                    metaStoreMaster.putMessage(wrapper.getMetaMessage(), wrapper.getMetaMessageAnnotation());

            System.out.println(i + "\t" + result.getAppendMessageResult().getMsgId());
        }

        // ��ʼ���ļ�
        Set<Integer> types = new HashSet<Integer>();
        types.add("MSG_TYPE_A".hashCode());
        for (long i = 0; i < totalMsgs; i++) {
            try {
                GetMessageResult result = metaStoreMaster.getMessage("TOPIC_A", 0, i, 1024 * 1024, types);
                if (result == null) {
                    System.out.println("result == null " + i);
                }
                assertTrue(result != null);
                result.release();
                System.out.println("read " + i + " OK");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        // �رմ洢����
        metaStoreMaster.shutdown();

        // ɾ���ļ�
        metaStoreMaster.destroy();
        System.out.println("================================================================");
    }


    @Test
    public void test_group_commit() throws Exception {
        System.out.println("================================================================");
        long totalMsgs = 10000;
        QUEUE_TOTAL = 1;

        // ������Ϣ��
        MessageBody = StoreMessage.getBytes();

        MetaStoreConfig metaStoreConfig = new MetaStoreConfig();
        // ÿ������ӳ���ļ� 4K
        metaStoreConfig.setMapedFileSizePhysic(1024 * 8);

        // ����GroupCommit����
        metaStoreConfig.setGroupCommitEnable(true);

        MetaStore metaStoreMaster = new DefaultMetaStore(metaStoreConfig);
        // ��һ����load��������
        boolean load = metaStoreMaster.load();
        assertTrue(load);

        // �ڶ�������������
        metaStoreMaster.start();
        for (long i = 0; i < totalMsgs; i++) {
            MetaMessageWrapper wrapper = buildMessage();
            PutMessageResult result =
                    metaStoreMaster.putMessage(wrapper.getMetaMessage(), wrapper.getMetaMessageAnnotation());

            System.out.println(i + "\t" + result.getAppendMessageResult().getMsgId());
        }

        // ��ʼ���ļ�
        Set<Integer> types = new HashSet<Integer>();
        types.add("MSG_TYPE_A".hashCode());
        for (long i = 0; i < totalMsgs; i++) {
            try {
                GetMessageResult result = metaStoreMaster.getMessage("TOPIC_A", 0, i, 1024 * 1024, types);
                if (result == null) {
                    System.out.println("result == null " + i);
                }
                assertTrue(result != null);
                result.release();
                System.out.println("read " + i + " OK");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        // �رմ洢����
        metaStoreMaster.shutdown();

        // ɾ���ļ�
        metaStoreMaster.destroy();
        System.out.println("================================================================");
    }
}
