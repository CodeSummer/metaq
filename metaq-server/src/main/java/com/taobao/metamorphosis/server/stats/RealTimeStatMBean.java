package com.taobao.metamorphosis.server.stats;

import java.util.List;


/**
 * 
 * 
 * 
 * @author boyan
 * 
 * @since 1.0, 2009-9-16 ����12:00:27
 */

public interface RealTimeStatMBean {

    /**
     * �鿴ʵʱͳ�Ƶ�key��Ϣ
     * 
     * @return
     */
    public  List<String> getRealTimeStatItemNames();


    /**
     * ���¿�ʼʵʱͳ��
     */
    public  void resetStat();


    /**
     * ʵʱͳ�ƽ��е�ʱ�䣬��λ��
     * 
     * @return
     */
    public  long getStatDuration();


    /**
     * ��ȡʵʱͳ�ƽ��
     * 
     * @param key1
     * @param key2
     * @param key3
     * @return
     */
    public  String getStatResult(String key1, String key2, String key3);


    public  String getStatResult(String key1, String key2);


    public  String getStatResult(String key1);


    public String getGroupedRealTimeStatResult(String key1);


    public long getDuration();
}