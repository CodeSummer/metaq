package com.taobao.metamorphosis.gregor.slave;



/**
 * 
 * ������������
 * 
 * @see OrderedThreadPoolExecutor
 */
public interface IoEvent extends Runnable {

    public IoCatalog getIoCatalog();
}
