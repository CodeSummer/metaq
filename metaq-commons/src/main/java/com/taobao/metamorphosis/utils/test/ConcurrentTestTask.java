package com.taobao.metamorphosis.utils.test;

/**
 * 
 * 
 * ������������ӿ�
 * 
 * @author boyan
 * 
 * @since 1.0, 2010-1-11 ����03:11:58
 */

public interface ConcurrentTestTask {
    /**
     * 
     * @param index
     *            �߳�������
     * @param times
     *            ����
     * @throws Exception TODO
     */
    public void run(int index, int times) throws Exception;
}
