/**
 * $Id: MessageFilter.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.util.Set;

/**
 * ��Ϣ���˽ӿ�
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public interface MessageFilter {
    public boolean isMessageMatched(final Set<Integer> types, int type);
}
