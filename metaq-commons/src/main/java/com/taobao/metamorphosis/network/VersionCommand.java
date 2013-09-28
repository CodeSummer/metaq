package com.taobao.metamorphosis.network;

import com.taobao.gecko.core.buffer.IoBuffer;
import com.taobao.gecko.core.command.kernel.HeartBeatRequestCommand;


/**
 * ��ѯ�������汾��Ҳ����������⣬Э�飺version opaque\r\n
 * 
 * @author boyan
 * @Date 2011-4-22
 * 
 */
public class VersionCommand extends AbstractRequestCommand implements HeartBeatRequestCommand {
    static final long serialVersionUID = -1L;


    public VersionCommand(final Integer opaque) {
        super(null, opaque);
    }


    @Override
    public IoBuffer encode() {
        return IoBuffer.wrap((MetaEncodeCommand.VERSION_CMD + " " + this.getOpaque() + "\r\n").getBytes());
    }

}
