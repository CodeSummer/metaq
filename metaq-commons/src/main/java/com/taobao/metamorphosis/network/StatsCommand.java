package com.taobao.metamorphosis.network;

import org.apache.commons.lang.StringUtils;

import com.taobao.gecko.core.buffer.IoBuffer;


/**
 * ͳ����Ϣ��ѯ ��ʽ��</br> stats item opaque\r\n
 * 
 * @author boyan
 * @Date 2011-4-21
 * 
 */
public class StatsCommand extends AbstractRequestCommand {
    static final long serialVersionUID = -1L;
    // ͳ����Ŀ���ƣ�����Ϊ��
    private final String item;


    public StatsCommand(final Integer opaque, final String item) {
        super(null, opaque);
        this.item = item;
    }


    public String getItem() {
        return this.item;
    }


    @Override
    public IoBuffer encode() {
        if (StringUtils.isBlank(this.item)) {
            return IoBuffer.wrap((MetaEncodeCommand.STATS_CMD + " " + " " + this.getOpaque() + "\r\n").getBytes());
        }
        else {
            final IoBuffer buf = IoBuffer.allocate(9 + this.item.length() + ByteUtils.stringSize(this.getOpaque()));
            ByteUtils.setArguments(buf, MetaEncodeCommand.STATS_CMD, this.item, this.getOpaque());
            buf.flip();
            return buf;
        }
    }

}
