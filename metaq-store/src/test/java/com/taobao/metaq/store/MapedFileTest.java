/**
 * $Id: MapedFileTest.java 2 2013-01-05 08:09:27Z shijia $
 */
package com.taobao.metaq.store;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class MapedFileTest {

    private static final String StoreMessage = "Once, there was a chance for me!";


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }


    @Test
    public void test_write_read() {
        try {
            MapedFile mapedFile = new MapedFile("./unit_test_store/MapedFileTest/000", 1024 * 64);
            boolean result = mapedFile.appendMessage(StoreMessage.getBytes());
            assertTrue(result);
            System.out.println("write OK");

            SelectMapedBufferResult selectMapedBufferResult = mapedFile.selectMapedBuffer(0);
            byte[] data = new byte[StoreMessage.length()];
            selectMapedBufferResult.getByteBuffer().get(data);
            String readString = new String(data);

            System.out.println("Read: " + readString);
            assertTrue(readString.equals(StoreMessage));

            // ��ֹBuffer��д
            mapedFile.shutdown(1000);

            // mapedFile���󲻿���
            assertTrue(!mapedFile.isAvailable());

            // �ͷŶ�����Buffer
            selectMapedBufferResult.release();

            // �ڴ������ͷŵ�
            assertTrue(mapedFile.isCleanupOver());

            // �ļ�ɾ���ɹ�
            assertTrue(mapedFile.destroy(1000));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * ��ǰ�����������ڶ�mmap�������󣬻ᵼ��JVM CRASHED
     */
    @Ignore
    public void test_jvm_crashed() {
        try {
            MapedFile mapedFile = new MapedFile("./unit_test_store/MapedFileTest/10086", 1024 * 64);
            boolean result = mapedFile.appendMessage(StoreMessage.getBytes());
            assertTrue(result);
            System.out.println("write OK");

            SelectMapedBufferResult selectMapedBufferResult = mapedFile.selectMapedBuffer(0);
            selectMapedBufferResult.release();
            mapedFile.shutdown(1000);

            byte[] data = new byte[StoreMessage.length()];
            selectMapedBufferResult.getByteBuffer().get(data);
            String readString = new String(data);
            System.out.println(readString);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
