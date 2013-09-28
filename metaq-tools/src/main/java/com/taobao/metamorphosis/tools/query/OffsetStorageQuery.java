package com.taobao.metamorphosis.tools.query;

import java.util.List;

/**
 * offset�Ĳ�ѯ�ӿ�
 * 
 * @author pingwei
 */
public interface OffsetStorageQuery {

    /**
     * �ṩoffset�Ĳ�ѯ������ʵ��ȷ����ͬ�Ĳ�ѯ����Դ
     * 
     * @param queryDO
     * @return
     */
    String getOffset(OffsetQueryDO queryDO);

    public List<String> getConsumerGroups();

    public List<String> getTopicsExistOffset(String group);

    public List<String> getPartitionsOf(String group, String topic);
}
