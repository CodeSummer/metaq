package com.taobao.metamorphosis.metaslave;
/**
 * ����һ����������master��Ϣʱ�Ĵ���
 * @author �޻�
 * @since 2011-6-28 ����03:35:30
 */

public class SubscribeMasterMessageException extends RuntimeException {

    private static final long serialVersionUID = 3449735809236405427L;

    public SubscribeMasterMessageException() {
        super();

    }


    public SubscribeMasterMessageException(String message, Throwable cause) {
        super(message, cause);

    }


    public SubscribeMasterMessageException(String message) {
        super(message);

    }


    public SubscribeMasterMessageException(Throwable cause) {
        super(cause);

    }
}
