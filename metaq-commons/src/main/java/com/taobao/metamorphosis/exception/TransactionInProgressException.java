package com.taobao.metamorphosis.exception;

/**
 * ����������쳣
 * 
 * @author boyan
 * 
 */
public class TransactionInProgressException extends MetaClientException {
    static final long serialVersionUID = -1L;


    public TransactionInProgressException() {
        super();

    }


    public TransactionInProgressException(final String message, final Throwable cause) {
        super(message, cause);

    }


    public TransactionInProgressException(final String message) {
        super(message);

    }


    public TransactionInProgressException(final Throwable cause) {
        super(cause);

    }

}
