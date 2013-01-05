package com.taobao.metamorphosis.tools.shell;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * �������С����
 * 
 * @author �޻�
 * @since 2011-8-23 ����3:44:25
 */

public abstract class ShellTool {

    protected PrintWriter out;

    final static protected String METACONFIG_NAME = "com.taobao.metamorphosis.server.utils:type=MetaConfig,*";


    public ShellTool(PrintWriter out) {
        this.out = out;
    }


    public ShellTool(PrintStream out) {
        this.out = new PrintWriter(out);
    }


    /** ��������� */
    abstract public void doMain(String[] args) throws Exception;


    protected void println(String x) {
        if (this.out != null) {
            this.out.println(x);
            this.out.flush();
        }
        else {
            System.out.println(x);
        }
    }

}
