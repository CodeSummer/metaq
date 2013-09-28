package com.taobao.metamorphosis.utils.codec;

import java.io.IOException;

/**
 * 
 * @author wuxin
 * @since 1.0, 2009-10-20 ����09:41:40
 */
public interface Serializer {
	/**
	 * ��ָ���Ķ���������л�.
	 * 
	 * @param obj - ��Ҫ���л��Ķ���
	 * @return    - ���ض������л�����ֽ���
	 */
	public byte[] encodeObject(Object obj)throws IOException;
}
