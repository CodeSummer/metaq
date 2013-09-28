package com.taobao.metamorphosis.tools.utils;

/**
 * ����jmx����쳣
 * 
 * @author �޻�
 * @since 2011-8-23 ����5:19:56
 */

public class RemoteExecuteException extends RuntimeException {

    private static final long serialVersionUID = -7410016800727397507L;


    public RemoteExecuteException(String message) {
        super(message);
    }


    public RemoteExecuteException(Throwable cause) {
        super(cause);
    }


    public RemoteExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
