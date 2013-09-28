/**
 * $Id: GetMessageStatus.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

/**
 * ����Ϣ״̬��
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public enum GetMessageStatus {
    // �ҵ���Ϣ
    FOUND,
    // offset��ȷ�����ǹ��˺�û��ƥ�����Ϣ
    NO_MATCHED_MESSAGE,
    // offset��ȷ���������������Ϣ���ڱ�ɾ��
    MESSAGE_WAS_REMOVING,
    // offset��ȷ�����Ǵ��߼�����û���ҵ����������ڱ�ɾ��
    OFFSET_FOUND_NULL,
    // offset�����������
    OFFSET_OVERFLOW_BADLY,
    // offset�������1��
    OFFSET_OVERFLOW_ONE,
    // offset����̫С��
    OFFSET_TOO_SMALL,
    // û�ж�Ӧ���߼�����
    NO_MATCHED_LOGIC_QUEUE,
    // ������һ����Ϣ��û��
    NO_MESSAGE_IN_QUEUE,
}
