package com.taobao.metamorphosis.utils.codec;

import java.io.IOException;

/**
 * 
 * @author wuxin
 * @since 1.0, 2009-10-20 ����09:42:35
 */
public interface Deserializer {
	/**
	 * ��ָ�����ֽ��뷴���л�.
	 * 
	 * @param in - ָ�����ֽ�������
	 * @return   - ���ط����л���Ķ���
	 */
	public Object decodeObject(byte[] in)throws IOException;
}
