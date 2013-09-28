package com.taobao.metamorphosis.server.utils;

/**
 * ���ĳ��topic���������ã���ʹ��ȫ�����ã�
 * 
 * @author �޻�
 * @since 2011-8-18 ����2:30:35
 */
// TODO ���������ĳ��topic�������������Ƶ�����
public class TopicConfig {
    private String topic;
    private int unflushThreshold;
    private int unflushInterval;
    private String dataPath;
    private String deleteWhen;
    private String deletePolicy;
    private int numPartitions;


    public TopicConfig(final String topic, final MetaConfig metaConfig) {
        this.topic = topic;
        this.unflushThreshold = metaConfig.getUnflushThreshold();
        this.unflushInterval = metaConfig.getUnflushInterval();
        this.dataPath = metaConfig.getDataPath();
        this.deleteWhen = metaConfig.getDeleteWhen();
        this.deletePolicy = metaConfig.getDeletePolicy();
        this.numPartitions = metaConfig.getNumPartitions();
    }


    public int getNumPartitions() {
        return this.numPartitions;
    }


    public void setNumPartitions(final int numPartitions) {
        this.numPartitions = numPartitions;
    }


    public String getDeletePolicy() {
        return this.deletePolicy;
    }


    public void setDeletePolicy(final String deletePolicy) {
        this.deletePolicy = deletePolicy;
    }


    public String getDeleteWhen() {
        return this.deleteWhen;
    }


    public void setDeleteWhen(final String deleteWhen) {
        this.deleteWhen = deleteWhen;
    }


    public String getDataPath() {
        return this.dataPath;
    }


    public void setDataPath(final String dataPath) {
        this.dataPath = dataPath;
    }


    public String getTopic() {
        return this.topic;
    }


    public void setTopic(final String topic) {
        this.topic = topic;
    }


    public int getUnflushThreshold() {
        return this.unflushThreshold;
    }


    public void setUnflushThreshold(final int unflushThreshold) {
        this.unflushThreshold = unflushThreshold;
    }


    public int getUnflushInterval() {
        return this.unflushInterval;
    }


    public void setUnflushInterval(final int unflushInterval) {
        this.unflushInterval = unflushInterval;
    }


    @Override
    public String toString() {
        return "TopicConfig [topic=" + this.topic + ", unflushThreshold=" + this.unflushThreshold
                + ", unflushInterval=" + this.unflushInterval + ", dataPath=" + this.dataPath + ", deleteWhen="
                + this.deleteWhen + "]";
    }

}
