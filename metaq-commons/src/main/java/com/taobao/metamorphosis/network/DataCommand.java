package com.taobao.metamorphosis.network;

import com.taobao.gecko.core.buffer.IoBuffer;


/**
 * Ӧ�����Э���ʽ���£� value total-length opaque\r\n data,����data�Ľṹ���£�
 * <ul>
 * <li>4���ֽڵ���Ϣ���ݳ��ȣ����ܰ������ԣ�</li>
 * <li>4���ֽڵ�check sum</li>
 * <li>8���ֽڵ���Ϣid</li>
 * <li>4���ֽڵ�flag</li>
 * <li>��Ϣ���ݣ���������ԣ���Ϊ��
 * <ul>
 * <li>4���ֽڵ����Գ���+ ��Ϣ���� + payload</li>
 * </ul>
 * </li> ����Ϊ��
 * <ul>
 * <li>payload</li>
 * <ul>
 * </li>
 * </ul>
 * 
 * @author boyan
 * @Date 2011-4-19
 * 
 */
public class DataCommand extends AbstractResponseCommand {
    private final byte[] data;
    static final long serialVersionUID = -1L;


    public byte[] getData() {
        return this.data;
    }


    public DataCommand(final byte[] data, final Integer opaque) {
        super(opaque);
        this.data = data;
    }


    @Override
    public boolean isBoolean() {
        return false;
    }


    @Override
    public IoBuffer encode() {
        // �����κ����飬����data command��transferTo���
        return null;
    }

}
