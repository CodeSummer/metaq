package com.taobao.metamorphosis.utils;

import com.taobao.metamorphosis.Message;


/**
 * ��Ϣflag������ flag��32λ���������Ľṹ���£�</br></br>
 * <ul>
 * <li>��һλ��1��ʾ����Ϣ���ԣ�����û��</li>
 * <li>������ʱ����</li>
 * </ul>
 * 
 * @author boyan
 * @Date 2011-4-29
 * 
 */
public class MessageFlagUtils {

    public static int getFlag(final Message message) {
        int flag = 0;
        if (message != null && message.getAttribute() != null) {
            // ��һλ����Ϊ1
            flag = flag & 0xFFFFFFFE | 1;
        }
        return flag;
    }


    public static int getFlagCompress(final int flag, final boolean compress) {
        int resultFlag = flag;
        if (compress) {
            // �Ͷ�λ����Ϊ1
            resultFlag = resultFlag & 0xFFFFFFFD | 2;
        }
        return resultFlag;
    }


    public static boolean hasAttribute(final int flag) {
        return (flag & 0x1) == 1;
    }


    public static boolean isCompress(final int flag) {
        return (flag & 0x2) == 2;
    }

}
