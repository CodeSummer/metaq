package com.taobao.metamorphosis.server.store;

import java.io.File;


/**
 * �ļ���ɾ������
 * 
 * @author boyan
 * @Date 2011-4-29
 * 
 */
public interface DeletePolicy {
    /**
     * �ж��ļ��Ƿ����ɾ��
     * 
     * @param file
     * @param checkTimestamp
     * @return
     */
    public boolean canDelete(File file, long checkTimestamp);


    /**
     * ��������ļ�
     * 
     * @param file
     */
    public void process(File file);


    /**
     * ��������
     * 
     * @return
     */
    public String name();


    /**
     * ��ʼ��
     * 
     * @param values
     */
    public void init(String... values);
}
