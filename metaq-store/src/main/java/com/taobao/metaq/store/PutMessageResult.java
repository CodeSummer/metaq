/**
 * $Id: PutMessageResult.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

/**
 * д����Ϣ���
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class PutMessageResult {
    private AppendMessageResult appendMessageResult;


    public PutMessageResult(AppendMessageResult appendMessageResult) {
        this.appendMessageResult = appendMessageResult;
    }


    public boolean isOk() {
        return this.appendMessageResult.isOk();
    }


    public AppendMessageResult getAppendMessageResult() {
        return appendMessageResult;
    }


    public void setAppendMessageResult(AppendMessageResult appendMessageResult) {
        this.appendMessageResult = appendMessageResult;
    }
}
