/**
 * $Id: AppendMessageStatus.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

/**
 * д����Ϣ״̬
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public enum AppendMessageStatus {
    // �ɹ�׷����Ϣ
    PUT_OK,
    // �ߵ��ļ�ĩβ
    END_OF_FILE,
    // ��Ϣ��С����
    MESSAGE_SIZE_EXCEEDED,
    // δ֪����
    UNKNOWN_ERROR,
}
