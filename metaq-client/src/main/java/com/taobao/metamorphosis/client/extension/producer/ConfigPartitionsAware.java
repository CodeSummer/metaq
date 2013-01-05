package com.taobao.metamorphosis.client.extension.producer;

import java.util.List;
import java.util.Map;

import com.taobao.metamorphosis.cluster.Partition;


/**
 * ֧�ֻ�ȡĳtopicԤ���õķ����ֲ����
 * 
 * @author �޻�
 * @since 2011-8-2 ����02:49:27
 */
public interface ConfigPartitionsAware {

    /**
     * ����˳����Ϣ���õ����������Ϣ
     * */
    public void setConfigPartitions(Map<String/* topic */, List<Partition>/* partitions */> map);


    /**
     * ��ȡĳ��topic��Ϣ�����������Ϣ
     * */
    public List<Partition> getConfigPartitions(String topic);
}
