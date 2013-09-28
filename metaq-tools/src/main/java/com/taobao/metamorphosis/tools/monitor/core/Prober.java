package com.taobao.metamorphosis.tools.monitor.core;

import com.taobao.metamorphosis.tools.monitor.InitException;


/**
 * @author �޻�
 * @since 2011-5-27 ����11:59:22
 */

public interface Prober {

    public void init() throws InitException;


    public void prob() throws InterruptedException;


    public void stopProb();

}
